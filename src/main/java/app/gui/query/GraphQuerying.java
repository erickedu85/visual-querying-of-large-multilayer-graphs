package app.gui.query;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.controlsfx.control.StatusBar;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;

import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.PositionShape;
import app.graph.structure.Relationships;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.database.GraphDBView;
import app.gui.ghost.GhostSliceItem;
import app.gui.ghost.PieGhost;
import app.gui.main.Constants;
import app.gui.main.SumgraBuffer;
import app.lib.ogdf.Layer;
import app.utils.DiversParser;
import app.utils.GeoAnalytic;
import app.utils.GeoUtil;
import app.utils.GraphUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import sumgra.application.MainSumgra;
import sumgra.data.GraphDatabase;

@SuppressWarnings("restriction")
public class GraphQuerying extends Node {

	public final static Logger logger = Logger.getLogger(GraphQuerying.class);
	public static GraphDBView processingApp; // handle action
	public static LayoutGraphQuery parent; // handle action

	// TOOL BARS
	private ToolBar topToolBar = new ToolBar();
	private ToolBar leftToolBar = new ToolBar();
	private StatusBar bottomStatusBar = new StatusBar();

	// TOP TOOL MENU
	public static ToggleButton tbSearch;

	// LEFT TOOL MENU
	public static List<EdgeType> edgeTypes = new ArrayList<EdgeType>();
	private ComboBox<EdgeType> cmbAddEdge = new ComboBox<EdgeType>();
	private ToggleGroup toggleLeftGroup = new ToggleGroup();
	private ToggleButton tbAddNode;
	private ToggleButton tbDelete;
	private Button btnFm3;
	public static ToggleButton tbGhost;

	// BOTTOM STATUS BAR
	private Label captionStatusBar = new Label("Ready");

	// COMBOBOX FOR Labels in Nodes
	public static ComboBox<String> cmbLabelNodes;
	private static ObservableList<String> observableLabelsNodes = FXCollections.observableArrayList();

	//
	public static Graph graphQuery;

	// VISUAL GRAPH QUERY
	private Vertex nodeQuerySource, nodeQueryTarget;
	private Canvas canvas;
	private GraphicsContext gc;
	private boolean isAddingEdge;
	private int indexSelectedEdgeType = 0;
	private static double nodeDiameter = Constants.GRAPH_QUERY_NODE_DIAMETER;

	// image node
	private Image nodeImage = new Image(this.getClass().getClassLoader()
			.getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat("node_icon.png")));

	// SUMGRA
	private SumgraBuffer sumgraBuffer;
	public static int sumgraThresholdBufferBacktraking = Constants.SUMGRA_THRESHOLD_BUFFER_BACKTRAKING;
	public static int sumgraSleepBacktraking = Constants.SUMGRA_SLEEP_BACKTRAKING;

	// EDGE UNDER MOUSE
	private Edge edgeUnderMouse;

	public GraphQuerying() {

		graphQuery = new Graph();
		canvas = new Canvas(Constants.VISUAL_CANVAS_WIDTH, Constants.VISUAL_CANVAS_HEIGHT);
		gc = canvas.getGraphicsContext2D();

		createTopToolBar();
		createLeftToolBar();
		createBottomStatusBar();

		// ---------------------- CANVAS ACTION EVENTS------------------

		canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent mouseCursor) {

				nodeQuerySource = getNodeUnderMouse(mouseCursor.getX(), mouseCursor.getY());

				// Removing the combobox of label nodes
				if (cmbLabelNodes.isVisible()) {
					parent.getPannableCanvas().getChildren().remove(cmbLabelNodes);
				}

				if (mouseCursor.getButton() == MouseButton.PRIMARY) {

					if (mouseCursor.getClickCount() == 1) {

						// Add a node
						if (tbAddNode.isSelected() && nodeQuerySource == null) {
							PositionShape addNodePosition = new PositionShape(mouseCursor.getX(), mouseCursor.getY());

							addNode(addNodePosition);
						}
						// Delete a node | Delete an edge
						else if (tbDelete.isSelected()) {
							// Delete a node
							if (nodeQuerySource != null) {

								graphQuery.deleteAdjacentEdgesOfNode(nodeQuerySource.getId());
								graphQuery.deleteNode(nodeQuerySource);

								// prepareGraphQueryTopology();
								clearStopAllInProcessing();
							}
							// Delete an edge
							else if (edgeUnderMouse != null) {

								graphQuery.deleteEdge(edgeUnderMouse);
								// prepareGraphQueryTopology();
								clearStopAllInProcessing();
							}
						}
						// Change state of a Ghost edge
						else if (tbGhost.isSelected()) {
							if (edgeUnderMouse != null) {
								// Change state 'Is a Ghost' to 'Was a Ghost'
								if (edgeUnderMouse.getStateGhost() == Constants.GRAPH_QUERY_EDGE_IS_GHOST) {
									edgeUnderMouse.setStateGhost(Constants.GRAPH_QUERY_EDGE_WAS_GHOST);
									clearStopAllInProcessing();
								} else if (edgeUnderMouse.getStateGhost() == Constants.GRAPH_QUERY_EDGE_WAS_GHOST) {
									// Change state 'Was a Ghost' to 'Is a
									// Ghost'
									edgeUnderMouse.setStateGhost(Constants.GRAPH_QUERY_EDGE_IS_GHOST);
									clearStopAllInProcessing();
								}
							}
						}
						// // Pin node
						// else if (tbPinNode.isSelected() && nodeQuerySource !=
						// null) {
						//
						// cmbLabelNodes.setMinWidth(150);
						// cmbLabelNodes.setVisible(true);
						// cmbLabelNodes.setTranslateX(mouseCursor.getX());
						// cmbLabelNodes.setTranslateY(mouseCursor.getY());
						//
						// parent.getPannableCanvas().getChildren().add(cmbLabelNodes);
						//
						// } else if (tbPinNode.isSelected() && nodeQuerySource
						// ==
						// null) {
						// // click in the white space in the drawing area
						// tbPinNode.setSelected(false);
						// parent.getPannableCanvas().getChildren().remove(cmbLabelNodes);
						// }
					} else if (mouseCursor.getClickCount() == 2 && !tbAddNode.isSelected()) {
						toggleLeftGroup.selectToggle(null);
						cmbAddEdgeUnselect();
					}

				}

				if (mouseCursor.getButton() == MouseButton.PRIMARY) {

				} else if (mouseCursor.getButton() == MouseButton.SECONDARY) {

					// Filling the observableLabelsNodes
					if (observableLabelsNodes.size() == 0) {
						observableLabelsNodes.add("*");
						// loop the listNode (non order) to add the item to the
						// observable list
						for (Vertex vertex : processingApp.getGraph().getListNode()) {
							observableLabelsNodes.add(vertex.getLabel());
						}
						ComponentCreator.fillCmbLabel(cmbLabelNodes, observableLabelsNodes);
					}

					if (nodeQuerySource != null) {
						cmbLabelNodes.setVisible(true);
						cmbLabelNodes.setTranslateX(mouseCursor.getX());
						cmbLabelNodes.setTranslateY(mouseCursor.getY());

						parent.getPannableCanvas().getChildren().add(cmbLabelNodes);
					}

				}

				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				display(0, 0);

			}
		});

		canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseCursor) {

				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				display(mouseCursor.getX(), mouseCursor.getY());
			}
		});

		canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseCursor) {
				// Getting the node under the mouse point if any
				nodeQuerySource = getNodeUnderMouse(mouseCursor.getX(), mouseCursor.getY());
			}
		});

		canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseCursor) {
				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

				// Adding an edge
				if (isAddingEdge && nodeQuerySource != null) {
					// Drawing a line
					EdgeType addEdgeType = edgeTypes.get(indexSelectedEdgeType);
					double sourceX = nodeQuerySource.getPosition().getX1();
					double sourceY = nodeQuerySource.getPosition().getY1();
					double targetX = mouseCursor.getX();
					double targetY = mouseCursor.getY();

					gc.setStroke(addEdgeType.getColor());
					gc.setLineWidth(Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT_DRAWING);
					gc.strokeLine(sourceX, sourceY, targetX, targetY);

					gc.setFill(Color.BLACK);
					gc.setTextAlign(TextAlignment.CENTER);
					gc.setTextBaseline(VPos.CENTER);
					gc.setFont(Constants.GRAPH_QUERY_EDGE_LABEL_FONT);
					PositionShape edgeMiddlePoint = GeoUtil.middlePointBetweenTwoPoints(sourceX, sourceY, targetX,
							targetY);
					gc.fillText(addEdgeType.getLabel(), edgeMiddlePoint.getX1(), edgeMiddlePoint.getY1());

				}
				// Moving a Node
				else if (nodeQuerySource != null && !tbDelete.isSelected()) {
					// VALIDATE CANVAS BORDERS
					int factore = 12;
					if (mouseCursor.getX() > factore && mouseCursor.getY() > factore
							&& mouseCursor.getX() < (canvas.getWidth() - factore)
							&& mouseCursor.getY() < (canvas.getHeight() - factore)) {
						PositionShape positionDrag = new PositionShape(mouseCursor.getX(), mouseCursor.getY());
						nodeQuerySource.setPosition(positionDrag);
					}
				}

				display(mouseCursor.getX(), mouseCursor.getY());
			}
		});

		canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseCursor) {

				nodeQueryTarget = getNodeUnderMouse(mouseCursor.getX(), mouseCursor.getY());

				// Complete adding an edge final step
				if (isAddingEdge && nodeQueryTarget != null && nodeQuerySource != null
						&& nodeQueryTarget != nodeQuerySource
						&& graphQuery.edgeTypeBetweenTwoNodes(nodeQuerySource.getId(), nodeQueryTarget.getId(),
								indexSelectedEdgeType) == null) {

					// logger.info("adding edge");
					addEdge(nodeQuerySource.getId(), nodeQueryTarget.getId(), indexSelectedEdgeType,
							Constants.GRAPH_QUERY_EDGE_NEVER_GHOST);

				}
				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				display(mouseCursor.getX(), mouseCursor.getY());
				nodeQueryTarget = null;
				nodeQuerySource = null;
			}
		});

	}

	/**
	 * Method to execute the FM3 algorithm
	 */
	private void executeFm3Algorithm() {
		toggleLeftGroup.selectToggle(null);
		cmbAddEdgeUnselect();

		Graph graphFM3 = Layer.executeFMMMLayout(graphQuery);
		DiversParser.saveGML(graphFM3, Constants.OGDF_PATH_TEMP_LAYOUT_FM3);

		loadQueryGraph(DiversParser.loadGML(Constants.OGDF_PATH_TEMP_LAYOUT_FM3));
		Layer.deleteTempGraphFile();

		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		display(0, 0);
	}

	/**
	 * Method to add a new Edge
	 * 
	 * @param idNodeSource
	 *            id the Node Source
	 * @param idNodeTarget
	 *            id the Node Target
	 * @param edgeType
	 *            edge type
	 * @param isGhost
	 */
	private void addEdge(int idNodeSource, int idNodeTarget, int edgeType, int stateGhost) {
		int id = graphQuery.getListEdge().size();
		EdgeType selectedEdge = edgeTypes.get(edgeType);
		String label = selectedEdge.getLabel();
		int type = edgeType;

		// Vertex nodeSource = graphQuery.getNodeById(idNodeSource);
		// Vertex nodeTarget = graphQuery.getNodeById(idNodeTarget);

		Vertex nodeSource = GraphUtil.getNodeById(graphQuery.getListNode(), idNodeSource);
		Vertex nodeTarget = GraphUtil.getNodeById(graphQuery.getListNode(), idNodeTarget);

		PositionShape position = new PositionShape(nodeSource.getPosition(), nodeTarget.getPosition());

		// Lines do not have FILL
		Stroke stroke = new Stroke(true, 0, Constants.GRAPH_QUERY_EDGE_STROKE_OPACITY,
				Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT);
		double distance = 0.0d;
		boolean isLabelled = true;
		boolean isVisible = true;

		Edge e = new Edge(id, label, type, position, stroke, null, isLabelled, isVisible, idNodeSource, idNodeTarget,
				distance);
		e.setStateGhost(stateGhost);
		graphQuery.addEdge(e);
	}

	/**
	 * Method to add a Node
	 * 
	 * @param idNode
	 *            Id to the new node
	 * @param positionNode
	 *            Position of the new node
	 */
	private void addNode(PositionShape positionNode) {

		int id = graphQuery.getMaxIdListNode();

		String label = String.valueOf("*");// "*";
		Fill fill = new Fill(Constants.GRAPH_QUERY_NODE_FILL);
		Stroke stroke = new Stroke(Constants.GRAPH_QUERY_NODE_STROKE);
		boolean isLabelled = true;
		boolean isVisible = true;

		graphQuery.addNode(new Vertex(id, label, Constants.GRAPH_DB_NODE_PATTERN_DEFAULT_TYPE,
				new PositionShape(positionNode.getX1(), positionNode.getY1()), stroke, fill, isLabelled, isVisible,
				nodeDiameter));

		clearStopAllInProcessing();
	}

	private void cmbAddEdgeSelect() {
		toggleLeftGroup.selectToggle(null);
		isAddingEdge = true;
		indexSelectedEdgeType = cmbAddEdge.getSelectionModel().getSelectedIndex();
		cmbAddEdge.setId("cmbAddEdge-selected");
	}

	/**
	 * Method to set the combobox of edges unselected
	 */
	private void cmbAddEdgeUnselect() {
		cmbAddEdge.setId("cmbAddEdge-unselected");
		isAddingEdge = false;
	}

	// ------------ GHOST--------------
	/**
	 * Method to add a node and edge of a Ghost
	 * 
	 * @param sliceAngle
	 * @param idNodeSource
	 * @param edgeType
	 */
	public void addExternalGhostNodeEdge(double sliceAngle, int idNodeSource, int edgeType) {

		int idNewNode = graphQuery.getMaxIdListNode();// graphQuery.getListNode().size();

		Vertex node = GraphUtil.getNodeById(graphQuery.getListNode(), idNodeSource);

		PositionShape positionAngle = GeoAnalytic.getCoordinatesByAngle(node.getPosition(), sliceAngle,
				Constants.GRAPH_QUERY_EDGE_GHOST_DISTANCE);

		addNode(positionAngle);
		addEdge(idNodeSource, idNewNode, edgeType, Constants.GRAPH_QUERY_EDGE_NEVER_GHOST);

		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		display(0, 0);

		clearStopAllInProcessing();
		// executeFm3Algorithm();
	}

	/**
	 * 
	 */
	private void loopBacktracking() {
		new Thread(new Runnable() {
			public void run() {
				while (GraphDBView.runningBacktracking) {
					sumgraBuffer.pickUp();
				}
			}
		}).start();
	}

	/**
	 * Method to clear all the process to start a new graph querying
	 * 
	 */
	private void clearStopAllInProcessing() {
		processingApp.noLoop();
		processingApp.emptyAllPatternsInMainGraph();
		processingApp.loop();
	}

	/**
	 * Method to display Node and Edges of the Graph Query
	 * 
	 * @param mouseX
	 * @param mouseY
	 */
	public void display(double mouseX, double mouseY) {
		captionStatusBar.setText("Ready");// Ready
		displayEdge(mouseX, mouseY);
		displayNode(mouseX, mouseY);
	}

	/**
	 * Method to display the nodes
	 * 
	 * @param mouseX
	 *            To under the mouse behavior
	 * @param mouseY
	 *            To under the mouse behavior
	 */
	private void displayNode(double mouseX, double mouseY) {

		for (Vertex node : graphQuery.getListNode()) {

			node.setDiameter(nodeDiameter);
			
			double x = node.getPosition().getX1() - (node.getDiameter() / 2);
			double y = node.getPosition().getY1() - (node.getDiameter() / 2);

			double tolerance = (node.getDiameter() / 2);
			if (graphQuery.isNodeNearMouse(node, mouseX, mouseY, tolerance)) {
				node.getFill().setFillColor(ColorShape.getHSB_NodePatternHover());
				// captionStatusBar.setText(node.toString());
				//captionStatusBar.setText("Node: " + node.getLabel());
			} else {
				node.getFill().setFillColor(ColorShape.getHSB_NodePattern());
			}

			gc.setFill(ColorShape.parserColorProcessingToJavafx(node.getFill().getFillColor()));
			gc.fillOval(x, y, node.getDiameter(), node.getDiameter());

			// Stroke
			// if (node.getType() == Constants.NODE_EDITING_TYPE) { //
			// Node-is-edited
			// gc.setStroke(Color.RED);
			// gc.setLineWidth(2.0d);
			// gc.strokeOval(x, y, node.getDiameter(), node.getDiameter());
			// }

			if (node.getLabel().equals("*")) {
				// show node image
				gc.drawImage(nodeImage, node.getPosition().getX1() - (Constants.GRAPH_QUERY_NODE_IMAGE_WIDTH / 2),
						node.getPosition().getY1() - (Constants.GRAPH_QUERY_NODE_IMAGE_HEIGHT / 2),
						Constants.GRAPH_QUERY_NODE_IMAGE_WIDTH, Constants.GRAPH_QUERY_NODE_IMAGE_HEIGHT);
			} else {
				// show label
				gc.setFill(Color.BLACK);
				gc.setTextAlign(TextAlignment.CENTER);
				gc.setTextBaseline(VPos.CENTER);
				gc.setFont(Constants.GRAPH_QUERY_EDGE_LABEL_FONT);
				gc.fillText(String.valueOf(node.getLabel()), node.getPosition().getX1(), node.getPosition().getY1());

			}
		}
	}

	/**
	 * Method to display Edges
	 * 
	 * @param mouseX
	 *            To under the mouse behavior
	 * @param mouseY
	 *            To under the mouse behavior
	 */
	private void displayEdge(double mouseX, double mouseY) {

		edgeUnderMouse = null;

		List<Relationships> listRelationships = graphQuery.getRelationsNodeWithEdge();

		// loop each relation
		for (Relationships relation : listRelationships) {
			// get ALL edges between idSource and idTarget of relation

			int idNodeSource = relation.getIdSource();
			int idNodeTarget = relation.getIdTarget();

			Vertex nodeSource = GraphUtil.getNodeById(graphQuery.getListNode(), idNodeSource);// .get();
			Vertex nodeTarget = GraphUtil.getNodeById(graphQuery.getListNode(), idNodeTarget);

			List<Edge> listEdgesBetween = graphQuery.edgesBetweenTwoNodes(idNodeSource, idNodeTarget);
			int numberOfEdges = listEdgesBetween.size();

			// Always numberOfEdges > 0, but we never know
			if (numberOfEdges > 0) {
				// The number of edges between two nodes
				double[] radius = GraphUtil.ojala(numberOfEdges, nodeDiameter);

				// Getting factor between the radius
				double factorRadius = 0;
				if (numberOfEdges > 1) {
					factorRadius = (nodeDiameter / 2 - radius[0]);
					if (factorRadius > 0 && factorRadius < Constants.GRAPH_QUERY_EDGE_PARALLEL_DISTANCE) {
						nodeDiameter = nodeDiameter + Constants.GRAPH_QUERY_EDGE_PARALLEL_DISTANCE;
					}
				}

				List<PositionShape> positionTangentEdges = new ArrayList<PositionShape>();
				for (double radio : radius) {
					// logger.info("radio: " + radio);
					// get outer tangents from two imaginary circles with radio
					double[][] outerTangents = GeoAnalytic.tangentPoints2Circles(nodeSource.getPosition().getX1(),
							nodeSource.getPosition().getY1(), radio, nodeTarget.getPosition().getX1(),
							nodeTarget.getPosition().getY1(), radio);

					// first outer tangent
					positionTangentEdges.add(new PositionShape(outerTangents[0][0], outerTangents[0][1],
							outerTangents[0][2], outerTangents[0][3]));
					// second outer tangent
					positionTangentEdges.add(new PositionShape(outerTangents[1][0], outerTangents[1][1],
							outerTangents[1][2], outerTangents[1][3]));
				}

				for (int e = 0; e < listEdgesBetween.size(); e++) {
					// captionStatusBar.setText("");
					Edge edge = listEdgesBetween.get(e);
					edge.setPosition(new PositionShape(positionTangentEdges.get(e)));
					edge.getStroke().setStrokeWeight(Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT);

					// edge under mouse
					if (getNodeUnderMouse(mouseX, mouseY) == null
							&& graphQuery.isEdgeNearMouse(edge, mouseX, mouseY, Constants.GRAPH_QUERY_EDGE_TOLERANCE)) {
						edgeUnderMouse = edge;
						edge.getStroke().setStrokeWeight(Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT_HOVER);
						// captionStatusBar.setText(edge.toString());
						captionStatusBar.setText("Edge type: " + edge.getLabel());
					} else if (getNodeUnderMouse(mouseX, mouseY) != null) {
						// to hightlight all other edges
					}

					// draw stroke line
					gc.save();
					if (edge.getStateGhost() == Constants.GRAPH_QUERY_EDGE_IS_GHOST) {
						gc.setLineDashes(Constants.GRAPH_QUERY_EDGE_STROKE_GHOST_DASHES);
					}
					gc.setStroke(edgeTypes.get(edge.getType()).getColor());
					gc.setLineWidth(edge.getStroke().getStrokeWeight());
					gc.setGlobalAlpha(Constants.GRAPH_QUERY_EDGE_ALPHA);
					gc.strokeLine(edge.getPosition().getX1(), edge.getPosition().getY1(), edge.getPosition().getX2(),
							edge.getPosition().getY2());

					gc.restore();

					// label AND background rectangle
					gc.save();
					PositionShape edgeMiddlePoint = GeoUtil.middlePointBetweenTwoPoints(edge.getPosition().getX1(),
							edge.getPosition().getY1(), edge.getPosition().getX2(), edge.getPosition().getY2());
					double slope = GeoUtil.slopeBetweenTwoPoints(edge.getPosition().getX1(), edge.getPosition().getY1(),
							edge.getPosition().getX2(), edge.getPosition().getY2());
					double degreeSlope = GeoUtil.degreeOfSlope(slope);

					gc.translate(edgeMiddlePoint.getX1(), edgeMiddlePoint.getY1());
					gc.rotate(degreeSlope);
System.out.println("id : " +edge.getId() + "tipo : " +  edge.getType());
					// rect - testing
					// gc.setFill(Color.WHITE);
					// double height = 10;
					// double width =
					// processingApp.textWidth(edge.getLabel().concat("----"));;
					// gc.fillRect(-width / 2, -height / 2, width, height);

					// label
					gc.setFill(Color.BLACK);
					gc.setStroke(Color.WHITE);
					gc.setTextAlign(TextAlignment.CENTER);
					gc.setTextBaseline(VPos.CENTER);
					gc.setFont(Constants.GRAPH_QUERY_EDGE_LABEL_FONT);
					gc.fillText(edge.getLabel(), 0, 0);
					gc.restore();
					//
				}
			}
		}
	}

	/**
	 * Method to remove External the Pie Ghost and the Internal Ghost line
	 * dashes
	 */
	private void removeExternalInternalGhost() {
		// Removing External PieGhost
		List<Node> listRemovePieGhost = new ArrayList<Node>();
		for (Node n : parent.getPannableCanvas().getChildren()) {
			if (n instanceof PieGhost) {
				listRemovePieGhost.add(n);
			}
		}
		parent.getPannableCanvas().getChildren().removeAll(listRemovePieGhost);
		//

		// Removing Internal line dashes
		List<Edge> listRemoveInternalGhost = new ArrayList<Edge>();
		for (Edge edge : graphQuery.getListEdge()) {
			if (edge.getStateGhost() == Constants.GRAPH_QUERY_EDGE_IS_GHOST) {
				listRemoveInternalGhost.add(edge);
			}
		}
		graphQuery.getListEdge().removeAll(listRemoveInternalGhost);
		//

		// Refresh canvas
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		display(0, 0);
	}

	// /**
	// * Method to know if some toggle bottom is selected
	// *
	// * @return
	// */
	// private boolean isAnyTbSelected() {
	// if (tbAddNode.isSelected() || tbDelete.isSelected()) {
	// return true;
	// }
	// return false;
	// }

	/**
	 * Method to know if some node is actually editing
	 * 
	 * @return
	 */
	// private boolean isSomeNodeEdited() {
	// for (Vertex node : graphQuery.getListNode()) {
	// if (node.getType() == Constants.NODE_EDITING_TYPE) {
	// return true;
	// }
	// }
	// return false;
	// }

	/**
	 * Method to set a type in all nodes
	 * 
	 * @param type
	 */
	private void setAllNodesType(int type) {
		graphQuery.setVerticesType(type);
	}

	/**
	 * Method to know if a node is under the mouse pointer
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return Node under the mouse, otherwise null
	 */
	private Vertex getNodeUnderMouse(double mouseX, double mouseY) {
		for (Vertex node : graphQuery.getListNode()) {
			double tolerance = node.getDiameter() / 2;
			if (graphQuery.isNodeNearMouse(node, mouseX, mouseY, tolerance)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Method to load a Graph Query
	 * 
	 * @param g
	 *            Graph to load
	 */
	private void loadQueryGraph(Graph g) {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		graphQuery = new Graph(g.getListNode(), g.getListEdge());

		// logger.info("normalization " + canvas.getWidth() + " - " +
		// canvas.getHeight());
		double padding = Constants.GRAPH_QUERY_NODE_DIAMETER;
		graphQuery.normalizationNodePosition(canvas.getWidth(), canvas.getHeight(), padding, padding, padding, padding);
		for (Vertex v : graphQuery.getListNode()) {
			v.setDiameter(Constants.GRAPH_QUERY_NODE_DIAMETER);
		}
		for (Edge e : graphQuery.getListEdge()) {
			e.getStroke().setStroked(true);
			e.setStateGhost(Constants.GRAPH_QUERY_EDGE_NEVER_GHOST);
		}
	}

	/**
	 * Method to save a graphQuery to a TXT file
	 * 
	 * @param file
	 */
	private void saveTxtFile(File file) {
		cmbAddEdgeUnselect();
		DiversParser.saveTXT(graphQuery, file.getAbsolutePath());
	}

	/**
	 * Method to save a graphQuery to a GML file
	 * 
	 * @param file
	 */
	private void saveGMLFile(File file) {
		cmbAddEdgeUnselect();
		DiversParser.saveGML(graphQuery, file.getAbsolutePath());
	}

	/**
	 * Method to save a graphQuery to a PNG file
	 * 
	 * @param file
	 */
	@SuppressWarnings("unused")
	private void savePngFile(File file) {
		WritableImage wim = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
		canvas.snapshot(null, wim);
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
		} catch (Exception s) {
		}
	}

	/**
	 * Method to open a GML file
	 * 
	 * @param file
	 */
	private void openGMLFile(File file) {
		Graph gGML = DiversParser.loadGML(file.getAbsolutePath());
		cmbAddEdgeUnselect();
		loadQueryGraph(gGML);
	}

	/**
	 * Method to create the Top ToolBar
	 */
	public void createTopToolBar() {
		// to translate all the top toolbar buttons by the width of a button
		int translateX = Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH + 2;

		Button btnOpen = ComponentCreator.makeButton("Load", "open.png", ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, translateX, 0,
				this.getClass(), "Load query");

		btnOpen.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				String[] allowedExtension = { "gml" };
				FileChooser fileChooser = ComponentCreator.makeFileChooser(allowedExtension);
				File file = fileChooser.showOpenDialog(null);
				if (file != null) {
					openGMLFile(file);
				}
				display(0, 0);
			};

		});

		Button btnNew = ComponentCreator.makeButton("New", "new.png", ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, translateX, 0,
				this.getClass(), "New query");

		btnNew.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (graphQuery.getListNode().size() != 0) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle(Constants.GUI_TITLE_MSG_DIALOG);
					alert.setHeaderText(Constants.GUI_MSG_CONFIRMATION_NEW);

					ButtonType buttonTypeYes = new ButtonType("Yes");
					ButtonType buttonTypeNo = new ButtonType("No");
					ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
					alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == buttonTypeYes) {
						// ... user chose OK
						// Show save file dialog
						String[] allowedExtension = { "gml" };
						FileChooser fileChooser = ComponentCreator.makeFileChooser(allowedExtension);
						// File file = fileChooser.showSaveDialog(primaryStage);
						File file = fileChooser.showSaveDialog(null); // there

						if (file != null) {
							gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
							toggleLeftGroup.selectToggle(null);
							cmbAddEdgeUnselect();
							DiversParser.saveGML(graphQuery, file.getAbsolutePath());
							graphQuery = new Graph();
							nodeDiameter = Constants.GRAPH_QUERY_NODE_DIAMETER;
							clearStopAllInProcessing();
							removeExternalInternalGhost();
							tbGhost.setDisable(true);
						}
					} else if (result.get() == buttonTypeNo) {
						graphQuery = new Graph();
						nodeDiameter = Constants.GRAPH_QUERY_NODE_DIAMETER;
						gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
						toggleLeftGroup.selectToggle(null);
						cmbAddEdgeUnselect();
						clearStopAllInProcessing();
						removeExternalInternalGhost();
						tbGhost.setDisable(true);
					} else {
						// ... user chose CANCEL or closed the dialog
					}
				}
				display(0, 0);
			};
		});

		Button btnSave = ComponentCreator.makeButton("Save", "save.png", ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, translateX, 0,
				this.getClass(), "Save query");

		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (graphQuery.getListNode().size() != 0) {
					String[] allowedExtension = { "gml" };
					FileChooser fileChooser = ComponentCreator.makeFileChooser(allowedExtension);
					File file = fileChooser.showSaveDialog(null);
					if (file != null) {
						saveGMLFile(file);
						// String selectedExtension =
						// fileChooser.getSelectedExtensionFilter().getExtensions().get(0);
						// switch (selectedExtension) {
						// case "*.txt":
						// saveTxtFile(file);
						// break;
						// case "*.gml":
						// saveGMLFile(file);
						// break;
						// case "*.sumgra":
						// saveSumgraFile(file);
						// break;
						// case "*.png":
						// savePngFile(file);
						// break;
						// default:
						// break;
						// }
					}
				}
				display(0, 0);
			};
		});

		tbSearch = ComponentCreator.makeToggleButton("Search", "search.png", null, ContentDisplay.LEFT,
				Constants.VISUAL_GRAPH_QUERY_BTN_SEARCH_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, translateX, 0,
				this.getClass(), "Search embeddings in the Graph");
		tbSearch.getStyleClass().add("btnToggleMenu");

		tbSearch.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				if (graphQuery.isConnected()) {

					if (processingApp.isRunningBacktracking()) {

						tbSearch.setGraphic(new ImageView(new Image(this.getClass().getClassLoader()
								.getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat("search.png")))));
						tbSearch.setText("Resume");

						processingApp.setAppBlocked(false);
						processingApp.setPauseBacktracking(true);
						processingApp.setRunningBacktracking(false);
						processingApp.setStopBacktracking(false);

						processingApp.pauseSumgraProcess();

					} else if (processingApp.isPauseBacktracking()) {

						tbSearch.setGraphic(new ImageView(new Image(this.getClass().getClassLoader()
								.getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat("pause.png")))));
						tbSearch.setText("Stop");

						processingApp.setAppBlocked(false);
						processingApp.setRunningBacktracking(true);
						processingApp.setPauseBacktracking(false);
						processingApp.setStopBacktracking(false);

						loopBacktracking();

					} else if (!processingApp.isStopBacktracking()) {

						String sumgraGraph = Constants.PATH_DATA.concat(Constants.SUMGRA_GRAPH_FILE);
						String queryGraph = Constants.PATH_DATA.concat(Constants.QUERY_GRAPH_FILE);

						toggleLeftGroup.selectToggle(null);
						cmbAddEdgeUnselect();

						tbSearch.setGraphic(new ImageView(new Image(this.getClass().getClassLoader()
								.getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat("pause.png")))));
						tbSearch.setText("Stop");

						tbGhost.setDisable(true);

						processingApp.setAppBlocked(false);
						processingApp.setRunningBacktracking(true);
						processingApp.setPauseBacktracking(false);
						processingApp.setStopBacktracking(false);
						processingApp.setSearchingPattern(true);

						// Saving the Query Graph in a file
						File file = new File(queryGraph);
						removeExternalInternalGhost();
						prepareGraphQueryTopology();
						saveTxtFile(file);

						// SUMGRA
						sumgraBuffer = new SumgraBuffer(sumgraThresholdBufferBacktraking);
						SumgraBuffer.processingApp = processingApp;

						// Constraints
						int[] constraints = new int[graphQuery.getListNode().size()];
						for (int index = 0; index < graphQuery.getListNode().size(); index++) {
							constraints[index] = graphQuery.getListNode().get(index).getAttachedPin();
						}
						
						logger.info("Constraints " + constraints );

						MainSumgra main = new MainSumgra(sumgraGraph, queryGraph, constraints, sumgraSleepBacktraking);
						GraphDatabase.sumgraBuffer = sumgraBuffer;
						main.start();

						// loop backtracking
						loopBacktracking();

					} else if (processingApp.isStopBacktracking()) {
						logger.info("no hacer nada");
						tbSearch.setSelected(false);
					}

				} else {
					tbSearch.setSelected(false);
				}
			}
		});

		// Spacer
		final Pane spacer = new Pane();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		int spacerWidth = (int) (Constants.LAYOUT_QUERY_VIEW_WIDTH - (4 * Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH)
				- Constants.VISUAL_GRAPH_QUERY_BTN_SEARCH_WIDTH - 20);
		spacer.setMinSize(spacerWidth, 1);
		spacer.setMaxSize(spacerWidth, 1);

		topToolBar.getStyleClass().add("backgroundMenu");
		topToolBar.setPadding(new Insets(1, 0, 1, 0));
		topToolBar.setOrientation(Orientation.HORIZONTAL);
		topToolBar.getItems().addAll(btnOpen, btnNew, btnSave, spacer, tbSearch);

		for (Node n : topToolBar.getItems()) {
			if (n instanceof Button) {
				n.getStyleClass().add("btnMenu");
			}
		}

	}

	/**
	 * Method to prepare the topology
	 */
	private void prepareGraphQueryTopology() {

		Map<Integer, Integer> keyValue = new HashMap<Integer, Integer>();

		// Removing edges with the "IS A Ghost" state
		List<Edge> listTmpIsGhost = new ArrayList<Edge>();
		for (Edge edge : graphQuery.getListEdge()) {
			if (edge.getStateGhost() == Constants.GRAPH_QUERY_EDGE_IS_GHOST) {
				listTmpIsGhost.add(edge);
			}
		}
		graphQuery.getListEdge().removeAll(listTmpIsGhost);
		//

		// Setting the key - value
		for (Edge edge : graphQuery.getListEdge()) {
			if (!keyValue.containsValue(edge.getIdSource())) {
				keyValue.put(keyValue.size(), edge.getIdSource());
			}
			if (!keyValue.containsValue(edge.getIdTarget())) {
				keyValue.put(keyValue.size(), edge.getIdTarget());
			}
		}

		// Changing the idSource and idTarget in the listEdge
		for (Edge edge : graphQuery.getListEdge()) {
			int idSource = GraphUtil.getKeyOfValue(keyValue, edge.getIdSource());
			int idTarget = GraphUtil.getKeyOfValue(keyValue, edge.getIdTarget());

			edge.setIdSource(idSource);
			edge.setIdTarget(idTarget);

			// Setting the edge in a never ghost state
			edge.setStateGhost(Constants.GRAPH_QUERY_EDGE_NEVER_GHOST);
		}

		// Changing the idNode in the listNode to make coherence
		for (Vertex node : graphQuery.getListNode()) {
			int idNode = GraphUtil.getKeyOfValue(keyValue, node.getId());
			node.setId(idNode);
		}

		// Sort ascending the listNode by id
		// Because the ghost topology is order
		Collections.sort(graphQuery.getListNode(), (Constants.NODE_ID_COMPARATOR));

	}

	/**
	 * Method to create the Left ToolBar
	 */
	public void createLeftToolBar() {

		edgeTypes.add(ComponentCreator.makeEdgeType(0, "TVCG"));
		edgeTypes.add(ComponentCreator.makeEdgeType(1, "InfoVis"));
		edgeTypes.add(ComponentCreator.makeEdgeType(2, "CGF"));
		edgeTypes.add(ComponentCreator.makeEdgeType(3, "EuroVis"));
		edgeTypes.add(ComponentCreator.makeEdgeType(4, "PacificVis"));
		edgeTypes.add(ComponentCreator.makeEdgeType(5, "Graph Drawing"));
		edgeTypes.add(ComponentCreator.makeEdgeType(6, "CG&A"));
		edgeTypes.add(ComponentCreator.makeEdgeType(7, "IV"));
		edgeTypes.add(ComponentCreator.makeEdgeType(8, "DASFAA"));
		edgeTypes.add(ComponentCreator.makeEdgeType(9, "EDBT"));
		edgeTypes.add(ComponentCreator.makeEdgeType(10, "ICDE"));
		edgeTypes.add(ComponentCreator.makeEdgeType(11, "ICDM"));
		edgeTypes.add(ComponentCreator.makeEdgeType(12, "ICDT"));
		edgeTypes.add(ComponentCreator.makeEdgeType(13, "KDD"));
		edgeTypes.add(ComponentCreator.makeEdgeType(14, "SDM"));
		edgeTypes.add(ComponentCreator.makeEdgeType(15, "SSDBM"));
		edgeTypes.add(ComponentCreator.makeEdgeType(16, "SIGMOD"));
		edgeTypes.add(ComponentCreator.makeEdgeType(17, "VLDB"));

		tbAddNode = ComponentCreator.makeToggleButton("Add", "add-node.png", toggleLeftGroup,
				ContentDisplay.GRAPHIC_ONLY, Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH,
				Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, 0, 0, this.getClass(), "Add a vertex in the query");

		cmbAddEdge.getItems().addAll(edgeTypes);
		cmbAddEdge.setValue(edgeTypes.get(0));
		cmbAddEdge.setMinWidth(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH);
		cmbAddEdge.setMaxWidth(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH);
		cmbAddEdge.setMinHeight(Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT);
		cmbAddEdge.setMaxHeight(Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT);
		cmbAddEdge.setPrefSize(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT);
		cmbAddEdge.setTooltip(new Tooltip("Add an edge in the query"));

		cmbAddEdge.setCellFactory(new Callback<ListView<EdgeType>, ListCell<EdgeType>>() {
			public ListCell<EdgeType> call(ListView<EdgeType> p) {
				return new ListCell<EdgeType>() {
					@Override
					protected void updateItem(EdgeType item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
							setText(null);
						} else {
							// setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
							// int cmbAddEdgeWidth = 5;
							// setPrefWidth(cmbAddEdgeWidth);
							// setMaxWidth(cmbAddEdgeWidth);
							// setMinWidth(cmbAddEdgeWidth);
							ImageView imageView = new ImageView(
									new Image(this.getClass().getClassLoader().getResourceAsStream(
											Constants.RESOURCES_IMAGE_PATH_EDGE_TYPES.concat(item.getRelativePath()))));
							setGraphic(imageView);
							setText(item.getLabel());
						}
					}
				};
			}
		});

		// SET THE VALUE NEXTSTEP TO THE BUTTONCELL
		cmbAddEdge.setButtonCell(new ListCell<EdgeType>() {
			@Override
			protected void updateItem(EdgeType item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
					setText(null);
				} else {
					setPadding(new Insets(0, 0, 0, 0));
					setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					ImageView imageView = new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream(
							Constants.RESOURCES_IMAGE_PATH_EDGE_TYPES.concat(item.getRelativePath()))));
					String label = item.getLabel();
					setGraphic(imageView);
					setText(label);
				}
			}
		});

		// necessary
		cmbAddEdge.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				cmbAddEdgeSelect();
			}
		});

		// necesary
		cmbAddEdge.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				cmbAddEdgeSelect();
			}
		});

		// tbMoveNode = ComponentCreator.makeToggleButton("Move",
		// "move-node.png", toggleLeftGroup, ContentDisplay.TOP,
		// Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH,
		// Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, 0, 0, this.getClass(),
		// "Move a vertex on the query graph");
		//
		// tbPinNode = ComponentCreator.makeToggleButton("Pin", "pin.png",
		// toggleLeftGroup, ContentDisplay.TOP,
		// Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH,
		// Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, 0, 0, this.getClass(),
		// "Pin vertex on the query graph");
		//
		// tbPinNode.setOnAction(new EventHandler<ActionEvent>() {
		// public void handle(ActionEvent e) {
		// if (tbPinNode.isSelected()) {
		// // If observableLabelsNodes is empty then fill with the
		// // labels
		// if (observableLabelsNodes.size() == 0) {
		// fillCmbLabelNodesToPin(processingApp.getGraph().getListNode());
		// }
		// } else if (!tbPinNode.isSelected()) {
		// // Removing the combobox of label nodes
		// parent.getPannableCanvas().getChildren().remove(cmbLabelNodes);
		//
		// }
		// }
		// });

		tbDelete = ComponentCreator.makeToggleButton("Delete", "delete.png", toggleLeftGroup,
				ContentDisplay.GRAPHIC_ONLY, Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH,
				Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, 0, 0, this.getClass(),
				"Delete a vertex/edge in the query");

		tbGhost = ComponentCreator.makeToggleButton("Ghost", "ghost.png", toggleLeftGroup, ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, 0, 0, this.getClass(),
				"Enable query suggestions");

		tbGhost.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				if (tbGhost.isSelected()) {

					// Refresh canvas
					gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					display(0, 0);

					// ----------- Internal Ghost ----------
					List<Edge> internalTopK = processingApp.getGraphGhostInternal()
							.getTopKInternal(Constants.GRAPH_QUERY_EDGE_GHOST_INTERNAL_TOP_K);
					
					for (Edge edgeGhost : internalTopK) {
						addEdge(edgeGhost.getIdSource(), edgeGhost.getIdTarget(), edgeGhost.getType(),
								Constants.GRAPH_QUERY_EDGE_IS_GHOST);
					}
					//-----------
					// clearStopAllInProcessing();

					// ----------- External Ghost ----------
					double pivotTranslate = 0;
					Map<Integer, List<Edge>> externalTopK = processingApp.getMapGhostExternal();
					for (Map.Entry<Integer, List<Edge>> entry : externalTopK.entrySet()) {

						if (entry.getValue().size() > 0) {

							LinkedHashMap<Integer, Integer> edgeTopKAppears = GraphUtil.sortExternalK(entry.getValue(),
									1);

							List<GhostSliceItem> edgeTypesOfNode = new ArrayList<GhostSliceItem>();

							for (Map.Entry<Integer, Integer> entrySliceGhost : edgeTopKAppears.entrySet()) {
								GhostSliceItem item = new GhostSliceItem(entry.getKey(),
										edgeTypes.get(entrySliceGhost.getKey()), entrySliceGhost.getValue());
								edgeTypesOfNode.add(item);
							}

							PieGhost pieGhost = new PieGhost(edgeTypesOfNode, captionStatusBar);

							// ERROR
							double positionX = graphQuery.getListNode().get(entry.getKey()).getPosition().getX1();
							double positionY = graphQuery.getListNode().get(entry.getKey()).getPosition().getY1();

							double pieGhostWidth = nodeDiameter + Constants.VISUAL_GRAPH_QUERY_PIE_GHOST_WIDTH;
							double pieGhostHeight = nodeDiameter + Constants.VISUAL_GRAPH_QUERY_PIE_GHOST_HEIGHT;
							//
							pieGhost.setVisible(true);
							pieGhost.setPadding(new Insets(0, 0, 0, 0));
							pieGhost.setTranslateX(positionX - (pieGhostWidth / 2));
							pieGhost.setTranslateY(positionY - (pieGhostHeight / 2));
							pieGhost.setMinWidth(pieGhostWidth);
							pieGhost.setMaxWidth(pieGhostWidth);
							pieGhost.setMinHeight(pieGhostHeight);
							pieGhost.setMaxHeight(pieGhostHeight);
							pivotTranslate = pivotTranslate + pieGhostWidth;

							parent.getPannableCanvas().getChildren().add(pieGhost);
						}
					}
					//
					// Refresh canvas
					gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					display(0, 0);

				} else {
					removeExternalInternalGhost();
				}
			}

		});

		tbGhost.setDisable(true);

		btnFm3 = ComponentCreator.makeButton("Layout", "fm3.png", ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, 0, 0, this.getClass(),
				"Execute a force-directed layout in the query");

		btnFm3.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				executeFm3Algorithm();
			}
		});

		cmbLabelNodes = new ComboBox<String>();
		cmbLabelNodes.setVisible(true);
		cmbLabelNodes.setEditable(true);
		cmbLabelNodes.setMinWidth(Constants.VISUAL_GRAPH_QUERY_CMBLABELNODES_WIDTH);
		cmbLabelNodes.setMaxWidth(Constants.VISUAL_GRAPH_QUERY_CMBLABELNODES_WIDTH);
		cmbLabelNodes.setPrefWidth(Constants.VISUAL_GRAPH_QUERY_CMBLABELNODES_WIDTH);
		cmbLabelNodes.setItems(observableLabelsNodes);
		cmbLabelNodes.setVisibleRowCount(5);

		// When select a node attribute
		cmbLabelNodes.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				try {
					// Removing the combobox
					parent.getPannableCanvas().getChildren().remove(cmbLabelNodes);

					// reduce -1 because we added "*" item to the combo label
					// index 0 = *
					// index is attache to search in the nodeList en el mismo
					// index orden
					int index = cmbLabelNodes.getSelectionModel().getSelectedIndex() - 1;
					String value = cmbLabelNodes.getSelectionModel().getSelectedItem();

					if (index >= -1) {
						nodeQuerySource.setAttachedPin(index);
						nodeQuerySource.setLabel(value);
					}

					gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					display(0, 0);

					clearStopAllInProcessing();

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});

		toggleLeftGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				cmbAddEdgeUnselect();
				if (!tbGhost.isSelected()) {
					removeExternalInternalGhost();
				}
			}
		});

		leftToolBar.getStyleClass().add("backgroundMenu");
		leftToolBar.setPadding(new Insets(0, 2, 0, 2));
		leftToolBar.setOrientation(Orientation.VERTICAL);
		// leftToolBar.setMinWidth(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH);
		// leftToolBar.setPrefWidth(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH);
		// leftToolBar.setMaxWidth(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH);

		// tbGhost
		leftToolBar.getItems().addAll(tbAddNode, cmbAddEdge, tbDelete, tbGhost, btnFm3);

		for (Node n : leftToolBar.getItems()) {
			if (n instanceof Button || n instanceof ComboBox<?>) {
				n.getStyleClass().add("btnMenu");
			} else if (n instanceof ToggleButton) {
				n.getStyleClass().add("btnToggleMenu");
			}
		}
	}

	/**
	 * Method to create a Bottom Status Bar
	 */
	public void createBottomStatusBar() {
		bottomStatusBar.setMinHeight(Constants.VISUAL_GRAPH_QUERY_STATUS_HEIGHT);
		bottomStatusBar.setMaxHeight(Constants.VISUAL_GRAPH_QUERY_STATUS_HEIGHT);
		bottomStatusBar.setPrefHeight(Constants.VISUAL_GRAPH_QUERY_STATUS_HEIGHT);

		bottomStatusBar.setText("");
		bottomStatusBar.getLeftItems().add(captionStatusBar);
	}

	public Graph getGraphQuery() {
		return graphQuery;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public ToolBar getTopToolBar() {
		return topToolBar;
	}

	public ToolBar getLeftToolBar() {
		return leftToolBar;
	}

	public StatusBar getBottomStatusBar() {
		return bottomStatusBar;
	}

	@Override
	protected NGNode impl_createPeer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseBounds impl_computeGeomBounds(BaseBounds bounds, BaseTransform tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean impl_computeContains(double localX, double localY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object impl_processMXNode(MXNodeAlgorithm alg, MXNodeAlgorithmContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	private void histoghost() {
		// -------------------------HISTOGHOST
		// ObservableList<PieChart.Data> pieChartData =
		// FXCollections.observableArrayList(
		// new PieChart.Data("Grapefruit", 43), new
		// PieChart.Data("Oranges", 25),
		// new PieChart.Data("Plums", 10), new
		// PieChart.Data("Pears", 42),
		// new PieChart.Data("Apples", 40));
		//
		// final PieChart chart = new PieChart(pieChartData);
		//
		// chart.setLabelsVisible(true);
		//
		// double positionX = node.getPosition().getX1();
		// double positionY = node.getPosition().getY1();
		// logger.info(positionX + ", " + positionY);
		//
		// double val = 70;
		// chart.setTranslateX(positionX + 15);
		// chart.setTranslateY(positionY - 405 + pivotTranslate
		// - 1);
		//
		// // subo
		// // resto la mitad de lo que subo
		//
		// chart.setMinWidth(val);
		// chart.setPrefWidth(val);
		// chart.setMaxWidth(val);
		//
		// chart.setMinHeight(val);
		// chart.setPrefHeight(val);
		// chart.setMaxHeight(val);
		//
		// toolLeftMenu.getItems().add(chart);
		// toolLeftMenu.setMinWidth(50);
		// toolLeftMenu.setPrefWidth(50);
		// toolLeftMenu.setMaxWidth(50);
		//
		// pivotTranslate = pivotTranslate - 70;

		// List<HistoghostItem> edgeTypesOfNode = new
		// ArrayList<HistoghostItem>();
		//
		// int idNode = node.getId();
		//
		// IntObjectHashMap<Object> currentNodeQuery =
		// processingApp.getGhostFinal().get(idNode);
		//
		// logger.info("For node id: " + idNode + "ghost: " +
		// currentNodeQuery);
		//
		// if (currentNodeQuery != null) {
		//
		// // logger.info("analysed node id: " + idNode);
		//
		// MutableIntSet mutableInt = currentNodeQuery.keySet();
		// MutableIntIterator itr = mutableInt.intIterator();
		//
		// // toma los K primeros, hay que ordenar
		// for (int i = 0; i < Constants.TOP_K_GHOST; i++) {
		// int edgeType = itr.next();
		// int numAppears = (int)
		// currentNodeQuery.get(edgeType);
		// // logger.info("edge type: " + edgeType + "
		// // numAppears: " + numAppears);
		// HistoghostItem item = new HistoghostItem(idNode,
		// edgeTypes.get(edgeType), numAppears);
		// edgeTypesOfNode.add(item);
		// }
		//
		// double widthHistoghost = 20;
		// double heightHistoghost = 10;
		// double gTranslateX = node.getPosition().getX1() - 60;
		// double gTranslateY = node.getPosition().getY1() - 370
		// + pivotTranslate;
		//
		// Histoghost histoGhost = new Histoghost(new
		// CategoryAxis(), new NumberAxis(),
		// edgeTypesOfNode);
		// histoGhost.setVisible(true);
		// histoGhost.setTranslateX(gTranslateX);
		// histoGhost.setTranslateY(gTranslateY);
		// histoGhost.setPrefSize(widthHistoghost,
		// heightHistoghost);
		// histoGhost.maxHeight(widthHistoghost);
		// histoGhost.maxWidth(heightHistoghost);
		//
		// toolLeftMenu.getItems().add(initIndexLeftPanel,
		// histoGhost);
		// initIndexLeftPanel++;
		//
		// pivotTranslate = pivotTranslate - 154;
		// }

	}

	public Edge getEdgeUnderMouse() {
		return edgeUnderMouse;
	}

	public static void setNodeDiameter(double nodeDiameter) {
		GraphQuerying.nodeDiameter = nodeDiameter;
	}

}
