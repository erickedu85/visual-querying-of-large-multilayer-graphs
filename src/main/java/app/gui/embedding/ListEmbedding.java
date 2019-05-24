package app.gui.embedding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.Graph;
import app.graph.structure.PositionShape;
import app.graph.structure.Relationships;
import app.graph.structure.Vertex;
import app.gui.main.Constants;
import app.utils.GeoAnalytic;
import app.utils.GeoUtil;
import app.utils.GraphUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import processing.core.PApplet;

public class ListEmbedding extends TableView<EmbeddingItem> {

	public final static Logger logger = Logger.getLogger(ListEmbedding.class);
	private Map<Color, Boolean> listBorderColors;
	private List<EmbeddingItem> listSelectedEmbeddingsItem;
	private static double nodeDiameter = Constants.LIST_EMBEDDING_NODE_DIAMETER;

	/**
	 * @param listFusionEmbeddings
	 */
	public ListEmbedding(List<Graph> listFusionEmbeddings) {
		if (listFusionEmbeddings == null) {
			return;
		}

		//logger.info("numero de list Embeddings" + listFusionEmbeddings.size());

		setId("listEmbedding");

		listBorderColors = new LinkedHashMap<Color, Boolean>();
		listBorderColors.put(Color.web("#f66"), false); // RED
		listBorderColors.put(Color.web("#6f6"), false); // GREEN
		listBorderColors.put(Color.web("#66f"), false); // BLUE
		listBorderColors.put(Color.web("#ffb266"), false); // ORANGE
		listBorderColors.put(Color.web("#f6f"), false); // PURPLE
		listBorderColors.put(Color.web("#96693c"), false);// BROWN
		listBorderColors.put(Color.web("#e77899"), false);// PINK

		listSelectedEmbeddingsItem = new ArrayList<EmbeddingItem>();
		ObservableList<EmbeddingItem> listEmbeddingsItem = FXCollections.observableArrayList();
		for (Graph graph : listFusionEmbeddings) {
			double mbr = graph.getMbr();
			int numEmbedding = graph.getNumberEmbedding();
			Canvas canvas = createGraphicsContext(graph);
			EmbeddingItem embeddingItem = new EmbeddingItem(canvas, graph, mbr, numEmbedding);
			listEmbeddingsItem.addAll(embeddingItem);
		}

		/* initialize and specify table column */
		// Column graph image
		TableColumn<EmbeddingItem, Canvas> graphImage = new TableColumn<EmbeddingItem, Canvas>("Fusion Embedding");
		graphImage.setCellValueFactory(new PropertyValueFactory<EmbeddingItem, Canvas>("canvas"));
		graphImage.setResizable(false);
		graphImage.setSortable(false);
		graphImage.setPrefWidth(Constants.LIST_EMBEDDING_COLUMN_GRAPH_WIDTH);

		// Column MBR
		TableColumn<EmbeddingItem, Integer> minimumBoundingRec = new TableColumn<EmbeddingItem, Integer>("MBR");
		minimumBoundingRec
				.setCellValueFactory(new PropertyValueFactory<EmbeddingItem, Integer>("minimumBoundingRectangle"));
		minimumBoundingRec.setResizable(false);
		minimumBoundingRec.setSortable(true);
		minimumBoundingRec.setPrefWidth(Constants.LIST_EMBEDDING_COLUMN_MBR_WIDTH);

		// Column number of embedding abstraction
		TableColumn<EmbeddingItem, Integer> numberEmbedding = new TableColumn<EmbeddingItem, Integer>("Aggregations");
		numberEmbedding.setCellValueFactory(new PropertyValueFactory<EmbeddingItem, Integer>("numberEmbeddings"));
		numberEmbedding.setResizable(false);
		numberEmbedding.setSortable(true);
		numberEmbedding.setPrefWidth(Constants.LIST_EMBEDDING_COLUMN_AGGREGATION_WIDTH);

		/* Add columns to the tableview and set its items */
		getColumns().add(graphImage);
		getColumns().add(minimumBoundingRec);
		getColumns().add(numberEmbedding);
		setItems(listEmbeddingsItem);
		// setSelectionModel(null);
		// getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		// getSelectionModel().setCellSelectionEnabled(false);

		setRowFactory(new Callback<TableView<EmbeddingItem>, TableRow<EmbeddingItem>>() {
			public TableRow<EmbeddingItem> call(TableView<EmbeddingItem> tableView) {
				final TableRow<EmbeddingItem> row = new TableRow<EmbeddingItem>();
				row.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
							EmbeddingItem embeddingClickedRow = row.getItem();
							if (!listSelectedEmbeddingsItem.contains(embeddingClickedRow)) {
								if (listSelectedEmbeddingsItem
										.size() >= Constants.LIST_EMBEDDING_MAXIMUM_SELECTED_ITEMS)
									return;
								embeddingClickedRow.setBorderColor(getAvailableBorderColor());
								Canvas canvas = embeddingClickedRow.getCanvas();
								Color borderColor = embeddingClickedRow.getBorderColor();
								embeddingClickedRow.setCanvas(changeBorderCanvas(canvas, borderColor));
								listSelectedEmbeddingsItem.add(embeddingClickedRow);
							} else {
								setFreedomBorderColor(embeddingClickedRow.getBorderColor());
								Canvas canvas = embeddingClickedRow.getCanvas();
								Color borderColor = Color.WHITE;
								embeddingClickedRow.setCanvas(changeBorderCanvas(canvas, borderColor));
								listSelectedEmbeddingsItem.remove(embeddingClickedRow);
							}
							refresh();
						}
					}
				});
				return row;
			}
		});
	}

	/**
	 * Method to clear selected embedding item
	 */
	public void clearListSelectedEmbeddingItems() {
		for (EmbeddingItem embeddingItem : getItems()) {
			setFreedomBorderColor(embeddingItem.getBorderColor());
			Canvas canvas = embeddingItem.getCanvas();
			Color borderColor = Color.WHITE;
			embeddingItem.setCanvas(changeBorderCanvas(canvas, borderColor));
			listSelectedEmbeddingsItem.remove(embeddingItem);
		}
		getListSelectedEmbeddingsItem().clear();
	}

	/**
	 * @return The first available color
	 */
	public Color getAvailableBorderColor() {
		for (Map.Entry<Color, Boolean> entry : listBorderColors.entrySet()) {
			if (entry.getValue() == false) {
				entry.setValue(true);
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Method to make free (available) a color
	 * 
	 * @param color
	 *            Color to make available
	 */
	public void setFreedomBorderColor(Color color) {
		for (Map.Entry<Color, Boolean> entry : listBorderColors.entrySet()) {
			if (entry.getKey() == color) {
				entry.setValue(false);
				break;
			}
		}
	}

	/**
	 * @param canvas
	 *            Canvas
	 * @param borderColor
	 *            Border color
	 * @return
	 */
	private Canvas changeBorderCanvas(Canvas canvas, Color borderColor) {
		Canvas canvasWithBorder = canvas;
		GraphicsContext gc = canvasWithBorder.getGraphicsContext2D();
		double lineWidth = Constants.LIST_EMBEDDING_COLUMN_GRAPH_BORDER_SELECTED;

		gc.setStroke(borderColor);
		gc.setLineWidth(lineWidth);
		gc.strokeRect((lineWidth), lineWidth, (canvas.getWidth() - ((3 * lineWidth) / 2)),
				(canvas.getHeight() - ((3 * lineWidth) / 2)));
		return canvas;
	}

	/**
	 * Method to create a Graphics Context from a Graph g
	 * 
	 * @param g
	 *            A graph
	 * @return A graphics context of a graph G
	 */
	private Canvas createGraphicsContext(Graph g) {
		Canvas canvas = new Canvas(Constants.LIST_EMBEDDING_COLUMN_GRAPH_WIDTH,
				Constants.LIST_EMBEDDING_COLUMN_GRAPH_HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		double padding = Constants.LIST_EMBEDDING_NODE_DIAMETER;
		g.normalizationNodePosition(Constants.LIST_EMBEDDING_COLUMN_GRAPH_WIDTH,
				Constants.LIST_EMBEDDING_COLUMN_GRAPH_HEIGHT, padding, padding, padding, padding);
		g.setZoom(1.0d);
		for (Vertex v : g.getListNode()) {
			int colorFromLerp = ColorShape.getHSB_FromLerp();
			int colorToLerp = ColorShape.getHSB_ToLerp();

			float radiusLerpColor = PApplet.map((float) v.getDiameter(),
					Constants.VISUAL_GRAPH_DB_PATTERN_NODE_DIAMETER_MIN,
					Constants.VISUAL_GRAPH_DB_PATTERN_NODE_DIAMETER_MAX, (float) 0.0, (float) 1.0);
			int lerpColor = PApplet.lerpColor(colorFromLerp, colorToLerp, (float) radiusLerpColor, 1);
			// v.getFill().setFillColor(lerpColor);
			v.getFill().setFillColor(lerpColor);
			v.setStroke(Constants.LIST_EMBEDDING_NODE_STROKE);
			v.setDiameter(Constants.VISUAL_GRAPH_DB_PATTERN_NODE_DIAMETER_MAX);
		}
		displayEdge(gc, g);
		displayNode(gc, g);
		return canvas;
	}

	private void displayEdge(GraphicsContext gc, Graph g) {

		List<Relationships> listRelationships = g.getRelationsNodeWithEdge();

		// loop each relation
		for (Relationships relation : listRelationships) {
			// get ALL edges between idSource and idTarget of relation

			int idNodeSource = relation.getIdSource();
			int idNodeTarget = relation.getIdTarget();

			Vertex nodeSource = GraphUtil.getNodeById(g.getListNode(), idNodeSource);
			Vertex nodeTarget = GraphUtil.getNodeById(g.getListNode(), idNodeTarget);

			List<Edge> listEdgesBetween = g.edgesBetweenTwoNodes(idNodeSource, idNodeTarget);
			int numberOfEdges = listEdgesBetween.size();
			// Always numberOfEdges > 0, but we never know
			if (numberOfEdges > 0) {
				// It means more than one edge between nodes
				double[] radius = GraphUtil.ojala(numberOfEdges, nodeDiameter);

				// Getting factor between the radius
				// double factorRadius = 0;
				// if (numberOfEdges > 1) {
				// factorRadius = (nodeDiameter / 2 - radius[0]);
				// if (factorRadius > 0 && factorRadius <
				// Constants.LIST_EMBEDDING_EDGE_PARALLEL_DISTANCE) {
				// nodeDiameter = nodeDiameter +
				// Constants.LIST_EMBEDDING_EDGE_PARALLEL_DISTANCE;
				// }
				// }

				List<PositionShape> positionTangentEdges = new ArrayList<PositionShape>();
				for (double radio : radius) {
					// get outer tangents from two imaginary circles with radio

					// logger.info("source " + idNodeSource + " - " +
					// nodeSource);
					// logger.info("target " + idNodeTarget + " - " +
					// nodeTarget);
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

					Edge edge = listEdgesBetween.get(e);
					edge.setPosition(new PositionShape(positionTangentEdges.get(e)));

					// label
					gc.save();
					PositionShape edgeMiddlePoint = GeoUtil.middlePointBetweenTwoPoints(edge.getPosition().getX1(),
							edge.getPosition().getY1(), edge.getPosition().getX2(), edge.getPosition().getY2());
					double slope = GeoUtil.slopeBetweenTwoPoints(edge.getPosition().getX1(), edge.getPosition().getY1(),
							edge.getPosition().getX2(), edge.getPosition().getY2());
					double degreeSlope = GeoUtil.degreeOfSlope(slope);
					gc.setFill(Color.BLACK);
					gc.setTextAlign(TextAlignment.CENTER);
					gc.setTextBaseline(VPos.CENTER);
					gc.setFont(Constants.LIST_EMBEDDING_EDGE_LABEL_FONT);
					gc.translate(edgeMiddlePoint.getX1(), edgeMiddlePoint.getY1());
					gc.rotate(degreeSlope);
					gc.setLineWidth(1);
					gc.fillText(edge.getLabel(), 0, 0);
					gc.restore();
					//

					// draw line
					gc.save();
					Color color = ColorShape.parserColorProcessingToJavafx(
							ColorShape.getHSBGoogle_ColorCategory(edge.getType()), edge.getStroke().getStrokeOpacity());
					gc.setStroke(color);
					gc.setLineWidth(edge.getStroke().getStrokeWeight());
					gc.setGlobalAlpha(Constants.GRAPH_QUERY_EDGE_ALPHA);
					gc.strokeLine(edge.getPosition().getX1(), edge.getPosition().getY1(), edge.getPosition().getX2(),
							edge.getPosition().getY2());
					gc.restore();
				}
			}
		}
	}

	private void displayNode(GraphicsContext gc, Graph g) {
		for (Vertex node : g.getListNode()) {
			node.setDiameter(nodeDiameter);

			double x = node.getPosition().getX1() - (node.getDiameter() / 2);
			double y = node.getPosition().getY1() - (node.getDiameter() / 2);

			// Fill
			gc.setFill(ColorShape.parserColorProcessingToJavafx(node.getFill().getFillColor()));
			gc.fillOval(x, y, node.getDiameter(), node.getDiameter());
			// Stroke
			gc.setStroke(ColorShape.parserColorProcessingToJavafx(node.getStroke().getStrokeColor()));
			gc.strokeOval(x, y, node.getDiameter(), node.getDiameter());
			// Drawing label
			gc.setFill(Color.BLACK);
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.CENTER);
			gc.setFont(Constants.LIST_EMBEDDING_NODE_LABEL_FONT);
			gc.fillText(node.getLabel(), node.getPosition().getX1(), node.getPosition().getY1());
		}
	}

	public List<EmbeddingItem> getListSelectedEmbeddingsItem() {
		return listSelectedEmbeddingsItem;
	}

}
