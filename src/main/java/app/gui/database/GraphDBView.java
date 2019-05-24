package app.gui.database;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gs.collections.api.iterator.MutableIntIterator;
import com.gs.collections.api.set.primitive.MutableIntSet;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import com.gs.collections.impl.map.mutable.primitive.ObjectIntHashMap;

import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.Hyperedge;
import app.graph.structure.Hypergraph;
import app.graph.structure.PositionShape;
import app.graph.structure.Relationships;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.embedding.EmbeddingItem;
import app.gui.embedding.LayoutEmbeddings;
import app.gui.histogram.HistogramItem;
import app.gui.main.Application;
import app.gui.main.Constants;
import app.gui.query.ComponentCreator;
import app.gui.query.GraphQuerying;
import app.gui.query.LayoutGraphQuery;
import app.mst.EdgeMst;
import app.utils.GraphUtil;
import app.utils.In;
import app.utils.MathUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;
import sumgra.data.GraphDatabase;

/**
 * @author Erick Cuenca
 *
 */
// implements ActionListener, ChangeListener
public class GraphDBView extends PApplet {

	private static final long serialVersionUID = 1L;
	public final static Logger logger = Logger.getLogger(GraphDBView.class);

	public static LayoutGraphQuery fxLeftPanel;
	public static LayoutEmbeddings fxRightPanel;

	private float startAngle = 0;

	//
	public Graph graph;
	private int backgroundColor;
	private Vertex nodeGraphPatternSelected;

	// Backtracking behavior
	public static boolean runningBacktracking; // to update the backtracking
	private boolean pauseBacktracking;
	private boolean stopBacktracking;

	// Histogram
	private int histogramNumCategories = Constants.HISTOGRAM_NUMBER_CATEGORIES;

	// Msg
	private String msg_puntos = "";

	// HeatMap
	// to visualize the heatmap after click in search pattern button
	private boolean isSearchingPattern;
	private int heatMapTransitionZoom = Constants.HEATMAP_INIT_TRANSITION_ZOOM;
	private List<Vertex> listHeatMap = new ArrayList<Vertex>();
	private int heatMapRadius = Constants.HEATMAP_INIT_RADIUS;

	// Kelp-like Diagrams
	public Hypergraph hypergraph;
	private List<EmbeddingItem> selectedEmbeddingItems = new ArrayList<EmbeddingItem>();
	private int kelpFacteurNode = Constants.KELP_NODE_FACTOR;
	private int kelpFacteurEdge = Constants.KELP_EDGE_FACTOR;
	private boolean showKelpLines = true;
	private boolean kelpOverlapLines = true;

	//
	private List<Vertex> nodesPatternSelectedInRed = new ArrayList<Vertex>();
	//
	private boolean showVerticesGraphDB = true;
	private boolean showEdges = true;
	private boolean showEmbeddingsGraphDB = true;
	//
	//

	Vertex nodeDisplayArc;

	// Save a screenshot
	private boolean saveScreenshot;
	private String savePathScreenshot;

	// Blocked the processing app
	private boolean appBlocked;

	// Speed behavior
	private boolean mouseWheel;
	private boolean mouseDragged;

	// ------------ GHOST -------------
	private boolean isFinishGhost;
	private List<IntObjectHashMap<Object>> ghostFinal;

	// Este no viene de Sumgra ya que sumgra solo tiene par de nodos y edges
	// esta es la relacion del nodo con todos sus edges adjacentes agrupados por
	// tipo
	// el tamano deberia ser el numero de id maximo de nodes
	// private IntObjectHashMap<Object> nodeRelationAdjacentEdgesTypes = new
	// IntObjectHashMap<Object>(300000);

	// Relacion del nodo con sus edges
	private IntObjectHashMap<Object> nodeRelationAdjacentEdges = new IntObjectHashMap<Object>(300000);

	// Ghost internal
	private Graph ghostInternal;
	// Ghost external
	private Map<Integer, List<Edge>> ghostExternal = new HashMap<Integer, List<Edge>>();

	//
	//
	//
	// public static void main(String[] args) {
	// PApplet.main(new String[] { "--present", "fr.um.general.ProcessingApp"
	// });
	// }

	public void settings() {
		// fullScreen();
		// size(500,500);
		// size(displayWidth, displayHeight);
		// Java 2D API Default works
		// FX2D processing.javafx.PSurfaceFX cannot be cast to
		// processing.awt.PSurfaceAWT
		// P2D processing.opengl.PSurfaceJOGL cannot be cast to
		// processing.awt.PSurfaceAWT
		// P3D processing.opengl.PSurfaceJOGL cannot be cast to
		// processing.awt.PSurfaceAWT
		// pixelDensity(displayDensity());
	}

	public void setup() {
		cursor(WAIT);

		colorMode(HSB, 360, 100, 100, 100); // Hue-Saturation-Brightness-Alpha

		ColorShape.parent = this;
		Graph.parent = this;
		Hypergraph.parent = this;
		Edge.parent = this;
		Vertex.parent = this;

		hypergraph = new Hypergraph();

		loadGraphDatabase(Constants.PATH_DATA.concat(Constants.MAIN_GRAPH_FILE));

		size(Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT);

		backgroundColor = ColorShape.getHSB_White();

		cursor(ARROW);
	}

	// public void emptyAll() {
	// emptyAllPatternsInMainGraph();
	// }

	/**
	 * @param pathName
	 */
	private void loadGraphDatabase(String pathName) {
		// long startTime = System.currentTimeMillis();
		In in = new In(pathName);
		graph = new Graph(in);
		graph.setName(pathName);

		// Normalization of the node position according to the size of the
		// screen
		double padding = Constants.GRAPH_QUERY_NODE_DIAMETER;
		graph.normalizationNodePosition(getHeight(), getHeight(), padding, padding, padding, padding);

		// long start = System.currentTimeMillis();
		// for (Vertex node : graph.getListNode()) {
		// int nodeId = node.getId();
		// if (!nodeRelationAdjacentEdgesTypes.containsKey(nodeId)) {
		// List<Edge> adjacentEdgesOfNode =
		// graph.getAdjacentEdgesOfNode(nodeId);
		// nodeRelationAdjacentEdges.put(nodeId, adjacentEdgesOfNode);
		// IntIntHashMap groupeAdjacentEdgesByType =
		// Graph.groupAdjacentEdgesByType(adjacentEdgesOfNode);
		// nodeRelationAdjacentEdgesTypes.put(nodeId,
		// groupeAdjacentEdgesByType);
		// }
		// }
		// logger.info(System.currentTimeMillis() - start);

	}

	public void draw() {
		if (!appBlocked && graph != null) {
			// PImage cursorImg =
			// loadImage("src/main/resources/img/screenshot.png");
			// cursor(cursorImg,16,16);
			displayMain();

			// To save a screenshot
			if (saveScreenshot) {
				save(savePathScreenshot);
				saveScreenshot = false;
			}
		}
	}

	/**
	 * @param embedding
	 */
	private void internalGhost(int[] embedding) {

		// Traer modelo unico desde graphQuery
		// Es decir, traer la lista de edges
		// Por cada edge, tomar Source y Target
		// Source y Target son los index en el array embedding

		List<Relationships> alreadyEdgesInternal = new ArrayList<Relationships>();
		List<Edge> internalEdges = GraphQuerying.graphQuery.getListEdge();
		for (Edge edgeInternal : internalEdges) {

			int source = embedding[edgeInternal.getIdSource()];
			int target = embedding[edgeInternal.getIdTarget()];

			Relationships pairInternalNodes = new Relationships(source, target);

			// alreadyEdgesInternal para no repetir edges
			// 0 1 type 0
			// 0 1 type 1
			if (!alreadyEdgesInternal.contains(pairInternalNodes)) {
				alreadyEdgesInternal.add(pairInternalNodes);
				// siempre habra un lien entre source y target en graphdb
				String edgesBetweenPairNodes = GraphDatabase.relacionDosNodosEdges.get(pairInternalNodes);
				String[] arrayEdgesBetweenPairNodes = edgesBetweenPairNodes.split(",");

				// Traer los edges entre Source y Target desde graphQuery
				List<Edge> edgesInternal = GraphQuerying.graphQuery.edgesBetweenTwoNodes(edgeInternal.getIdSource(),
						edgeInternal.getIdTarget());
				// Crear un array de Strings de los tipos de edges internols
				String[] arrayEdgesInternal = GraphUtil.arrayOfEdgeType(edgesInternal);

				// Differentiation
				String[] arrayEdgesDifferentiation = GraphUtil.restArray(arrayEdgesBetweenPairNodes,
						arrayEdgesInternal);

				// Update GraphGhost with source and target and the
				// arrayEdgesDifferentiation
				updateInternalGhost(edgeInternal.getIdSource(), edgeInternal.getIdTarget(), arrayEdgesDifferentiation);
			}
		}
	}

	/**
	 * @param source
	 * @param target
	 * @param edgesTypes
	 */
	private void updateInternalGhost(int source, int target, String[] edgesTypes) {

		for (String eType : edgesTypes) {
			int id = 0;
			String label = "From " + source + " to " + target;
			int edgeType = Integer.valueOf(eType);
			PositionShape position = null;
			Stroke stroke = null;
			Fill fill = null;
			boolean isLabelled = true;
			boolean isVisible = false;
			double distance = 0;
			Edge edgeGhost = new Edge(id, label, edgeType, position, stroke, fill, isLabelled, isVisible, source,
					target, distance);

			// Si el ghostInternal no contiene este tipo de edge hay que
			// agregarle
			if (!ghostInternal.getListEdge().contains(edgeGhost)) {
				ghostInternal.addEdge(edgeGhost);
			} else {
				// Si el Graph Ghost ya contine ese tipo de edge, hay que
				// sumarle 1 en el weight del edge
				Edge e = ghostInternal.edgeTypeBetweenTwoNodes(source, target, edgeType);
				e.setWeight(e.getWeight() + 1);
			}
		}
	}

	/**
	 * 
	 * @param embedding
	 */
	private void externalGhost(int[] embedding) {

		// logger.info("embedding " + Arrays.toString(embedding));
		for (int indexEmbedding = 0; indexEmbedding < embedding.length; indexEmbedding++) {

			int focusNode = embedding[indexEmbedding];
			// logger.info(" focus " + focusNode);

			// In GraphQuery
			List<Vertex> internalToExcludeInEmbedding = GraphQuerying.graphQuery.getAdjacentNodesOfNode(indexEmbedding);
			int[] nodesInternalInEmbedding = new int[internalToExcludeInEmbedding.size()];
			for (int j = 0; j < internalToExcludeInEmbedding.size(); j++) {
				// Node Ids in nodeQuery are the indices in the embedding
				nodesInternalInEmbedding[j] = embedding[internalToExcludeInEmbedding.get(j).getId()];
			}

			// logger.info(" interno " +
			// Arrays.toString(nodesInternalInEmbedding));

			@SuppressWarnings("unchecked")
			List<Edge> adjacentEdgeOfNodeFocus = new ArrayList<Edge>(
					(List<Edge>) nodeRelationAdjacentEdges.get(focusNode));

			List<Edge> internalEdges = new ArrayList<Edge>();
			for (int nodeInternal : nodesInternalInEmbedding) {
				for (Edge edge : adjacentEdgeOfNodeFocus) {
					if (edge.containOneVertex(nodeInternal)) {
						internalEdges.add(edge);
					}
				}
			}
			adjacentEdgeOfNodeFocus.removeAll(internalEdges);

			// Futuro se podria ver el nodo al que mayor numero de edges van :)
			// Aqui ir sumando los resultados al node con el index i en graph
			// query
			ghostExternal.put(indexEmbedding,
					mergeTwoListEdgeByType(ghostExternal.get(indexEmbedding), adjacentEdgeOfNodeFocus));

		}

	}

	/**
	 * Method to merge two list of edge by type
	 * 
	 * @param listA
	 * @param listB
	 * @return
	 */
	private List<Edge> mergeTwoListEdgeByType(List<Edge> listA, List<Edge> listB) {

		IntObjectHashMap<Object> relationEdgeTypeNumAppears = new IntObjectHashMap<Object>();

		List<Edge> result = new ArrayList<Edge>();

		for (Edge a : listA) {
			if (!relationEdgeTypeNumAppears.containsKey(a.getType())) {
				relationEdgeTypeNumAppears.put(a.getType(), a.getWeight());
			} else {
				Double weight = (Double) relationEdgeTypeNumAppears.get(a.getType());
				relationEdgeTypeNumAppears.put(a.getType(), (weight + 1));
			}
		}
		for (Edge b : listB) {
			if (!relationEdgeTypeNumAppears.containsKey(b.getType())) {
				relationEdgeTypeNumAppears.put(b.getType(), b.getWeight());
			} else {
				Double weight = (Double) relationEdgeTypeNumAppears.get(b.getType());
				relationEdgeTypeNumAppears.put(b.getType(), (weight + 1));
			}
		}

		MutableIntSet mutableInt = relationEdgeTypeNumAppears.keySet();
		MutableIntIterator itr = mutableInt.intIterator();
		while (itr.hasNext()) {
			int type = itr.next();
			Double weight = (Double) relationEdgeTypeNumAppears.get(type);
			int source = 0;
			int target = 0;
			String label = "";
			result.add(new Edge(0, label, type, null, null, null, true, false, source, target, 0, weight,
					Constants.GRAPH_QUERY_EDGE_IS_GHOST));
		}

		return result;

	}

	/**
	 * Method to calculate the GHOST
	 */
	private void calculateGhost() {

		logger.info(" -------- Initialize GHOST ---------- ");

		if (!isFinishGhost && graph.getListEmbeddings().size() > 0) {

			// Create ghostInternal same topology as GraphQuery
			ghostInternal = new Graph(GraphQuerying.graphQuery);

			// Initialize the ghostExternal as arrayList
			for (int i = 0; i < graph.getListEmbeddings().get(0).length; i++) {
				ghostExternal.put(i, new ArrayList<Edge>());
			}

			logger.info("	1/3 Filling the edge adjacent list for each node in embeddings...");
			for (int[] embedding : graph.getListEmbeddings()) {
				for (int nodeId : embedding) {
					if (!nodeRelationAdjacentEdges.containsKey(nodeId)) {
						List<Edge> adjacentEdgesOfNode = graph.getAdjacentEdgesOfNode(nodeId);
						nodeRelationAdjacentEdges.put(nodeId, adjacentEdgesOfNode);
					}
				}
			}

			logger.info("	2/3 Getting the external ghost relations...");
			long start = System.currentTimeMillis();
			for (int[] embedding : graph.getListEmbeddings()) {
				externalGhost(embedding);
			}
			logger.info("		ET " + (System.currentTimeMillis() - start));
			//

			logger.info("	3/3 Getting the internal ghost relations...");
			start = System.currentTimeMillis();
			for (int[] embedding : graph.getListEmbeddings()) {
				internalGhost(embedding);
			}
			logger.info("		ET " + (System.currentTimeMillis() - start));

			isFinishGhost = true;
			fxLeftPanel.updateGhostButton();

			logger.info(" ------ Terminate GHOST :) --------");
		} else {
		}

	}

	private boolean isVisibleHeatMap() {
		if (graph.getZoom() <= heatMapTransitionZoom) {
			return true;
		}
		return false;
	}

	private void displayMain() {

		background(backgroundColor);

		pushMatrix();
		translate((float) (-graph.getxView() * graph.getZoom()), (float) (-graph.getyView() * graph.getZoom()));

		if (showVerticesGraphDB) {
			graph.displayNode();
			if (nodeDisplayArc != null) {
				nodeDisplayArc.displayArc(startAngle);
				startAngle = (float) (startAngle + 0.5);
			}
		}

		if (isSearchingPattern) {
			if (listHeatMap.size() > 0 && isVisibleHeatMap()) {
				graph.displayHeatmap(listHeatMap, heatMapRadius);
			} else if (!isVisibleHeatMap() && showEmbeddingsGraphDB) {
				graph.displayNodePattern(mouseWheel);
				mouseWheel = false;

				// display hyperedges after displayNode
				// when the coordinates are already in the orignal position
				// (0,0)
				if (hypergraph.getListHyperedge().size() > 0) {
					hypergraph.displayHypers(showKelpLines, kelpOverlapLines);
				}
			}
		}

		popMatrix();

		//
		float mapx = map((float) (graph.getxView() * graph.getZoom()), 0, getWidth(), 0, 200);
		float mapy = map((float) (graph.getyView() * graph.getZoom()), 0, getHeight(), 0, 200);
		int upperLeftCornerX = (int) (getWidth() - 200 + mapx);
		int upperLeftCornerY = (int) (getHeight() - 200 + mapy);
		int widthCopy = 195;
		int heightCopy = 195;

		// Only one time
		// load image
		// PImage fondoImg = loadImage("fondo.png");
		// image(fondoImg, getWidth() - 200, getHeight() - 200, widthCopy,
		// heightCopy);

		// // rectangle
		// stroke(255);
		// strokeWeight(5);
		// noFill();
		// rect((float) (upperLeftCornerX), (float) (upperLeftCornerY),
		// widthCopy, heightCopy);

		// To show the gray rectangle when the backtracking is running...
		// out of the translate to not distort if we drag the mouse
		if (isRunningBacktracking()) {
			float msgCoordinateX = (float) (Application.SCREEN_WIDTH - Constants.LAYOUT_QUERY_VIEW_WIDTH
					- Constants.LAYOUT_EMBEDDINGS_VIEW_WIDTH) / 2;
			float msgCoordinateY = (float) Application.SCREEN_HEIGHT / 2;
			float spaceBetweenMsg = 80;

			fill(GRAY, 40);
			rect(0, 0, Application.SCREEN_WIDTH, Application.SCREEN_HEIGHT);

			// --MSG Updating...
			textSize(65);
			fill(ColorShape.getHSB_Black());
			textAlign(PConstants.CENTER, PConstants.CENTER);
			text(Constants.SUMGRA_MSG_LINE_1 + msg_puntos, msgCoordinateX, msgCoordinateY - spaceBetweenMsg);
			if (msg_puntos.length() == 3) {
				msg_puntos = "";
			} else {
				msg_puntos = msg_puntos.concat(".");
			}

			textSize(20);
			fill(ColorShape.getHSB_Blue());
			textAlign(PConstants.CENTER, PConstants.CENTER);
			text("#Embeddings Found: " + graph.getListEmbeddings().size(), msgCoordinateX, msgCoordinateY);

			// + graph.getListNodePattern().size(), msgCoordinateX,
			// msgCoordinateY);
			// text("#Embeddings: " + graph.getListHistoricEmbeddings().size() +
			// "\n" + "#Embedding nodes: "
			// + graph.getListHistoricNodesPattern().size(), msgCoordinateX,
			// msgCoordinateY);

			// --- MSG Click anywhere
			// textSize(45);
			// fill(ColorShape.getHSB_Black());
			// textAlign(PConstants.CENTER, PConstants.CENTER);
			// text(Constants.MSG_LINE_3, msgCoordinateX, msgCoordinateY +
			// spaceBetweenMsg);
		}
	}

	/**
	 * 
	 * MOUSE BEHAVIOR
	 * 
	 */
	// public void mouseWheelMoved(MouseWheelEvent event) {
	// if (wheelMovementTimer != null && wheelMovementTimer.isRunning()) {
	// wheelMovementTimer.stop();
	// }
	// wheelMovementTimer = new Timer(TIMER_DELAY, new
	// WheelMovementTimerActionListener());
	// wheelMovementTimer.setRepeats(false);
	// wheelMovementTimer.start();
	//
	// super.mouseWheelMoved(event);
	// }
	//
	// private class WheelMovementTimerActionListener implements ActionListener
	// {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// graph.setMirarLabel(true);
	// }
	// }

	/**
	 * Zoom-In and Zoom-out
	 * 
	 * @param e
	 *            Zoom in = 1; Zoom out = -1
	 */
	public void mouseWheel(MouseEvent event) {
		nodeDisplayArc = null;
		mouseWheel = true;

		if (!isRunningBacktracking()) {
			float e = -event.getCount();
			zoom(e);
		}
	}

	/**
	 * @param e
	 *            -1 Zoom-In
	 * @param graphDatabase
	 *            Graph G
	 * @param mousePosition
	 *            MousePosition X, Y
	 */
	private void zoom(float e) {
		// e==1 IN
		// e=-1 OUT
		double sf = (float) ((e == 1) ? Constants.VISUAL_GRAPH_DB_ZOOM_FACTOR_IN
				: Constants.VISUAL_GRAPH_DB_ZOOM_FACTOR_OUT);
		double z = graph.getZoom() * sf;
		z = constrain((float) z, (float) Constants.VISUAL_GRAPH_DB_ZOOM_THRESHOLD_OUT,
				(float) Constants.VISUAL_GRAPH_DB_ZOOM_THRESHOLD_IN);

		double wmX = getXMousePixel(mouseX);
		double wmY = getYMousePixel(mouseY);

		graph.setZoom(z);
		hypergraph.setZoom(z);

		graph.setxView(wmX - (mouseX / graph.getZoom()));
		graph.setyView(wmY - (mouseY / graph.getZoom()));
	}

	private double getXMousePixel(double mouseX) {
		return graph.getxView() + (mouseX / graph.getZoom());
	}

	private double getYMousePixel(double mouseY) {
		return graph.getyView() + (mouseY / graph.getZoom());
	}

	/**
	 * @param label
	 */
	public boolean searchNodePatternByLabel(String label) {
		startAngle = 0;
		mouseWheel = true;
		mouseDragged = false;
		// loop the list of the node patterns
		for (Vertex node : graph.getListNodePattern()) {
			if (node.getLabel().toLowerCase().equals(label.toLowerCase())) {
				// Moving the GraphDB view
				PositionShape newCoordinates = GraphUtil.translatePosition(node.getPosition().getX1(),
						node.getPosition().getY1(), graph.getZoom());
				nodeDisplayArc = new Vertex(node);
				nodeDisplayArc.setPosition(newCoordinates);

				int factorWidht = (Application.SCREEN_HEIGHT - Constants.LAYOUT_QUERY_VIEW_WIDTH
						- Constants.LAYOUT_EMBEDDINGS_VIEW_WIDTH) / 2;
				int factorHeight = Application.SCREEN_HEIGHT / 2;
				// graph.setxView((newCoordinates.getX1() - pmouseX -
				// factorWidht) / graph.getZoom());
				// graph.setyView((newCoordinates.getY1() - pmouseY -
				// factorHeight) / graph.getZoom());
				graph.setxView((newCoordinates.getX1() - factorWidht) / graph.getZoom());
				graph.setyView((newCoordinates.getY1() - factorHeight) / graph.getZoom());

				return true;
			}
		}
		return false;
	}

	/**
	 * Method called when: pause a sumgra process, or when finish a sumgra
	 * process
	 */
	public void pauseSumgraProcess() {

		// Normalization of the diameter of the list of node patterns
		graph.normalizationDiameterNodePatterns(Constants.VISUAL_GRAPH_DB_PATTERN_NODE_DIAMETER_MIN,
				Constants.VISUAL_GRAPH_DB_PATTERN_NODE_DIAMETER_MAX);

		// Create the histogram from the nodeListPattern
		List<Graph> listGraphHistograms = getGraphsForHistogram();
		createHistogramChart(listGraphHistograms, histogramNumCategories);

		// Filling node labels list in the menu of the search BEFORE sort the
		// patterns by diameter
		graph.sortListNodePattern(0, Constants.NODE_LABEL_COMPARATOR);
		ObservableList<String> items = FXCollections.observableArrayList();
		for (Vertex node : graph.getListNodePattern()) {
			items.add(node.getLabel());
		}
		ComponentCreator.fillCmbLabel(GraphDBToolBar.cmbLabelNodesPatterns, items);
		// Sort the listNodePattern by diameter size
		graph.sortListNodePattern(0, Constants.NODE_DIAMETER_COMPARATOR);
	}

	/**
	 * 
	 */
	public void stopSumgraProcess() {
		stopBacktracking = true;
		runningBacktracking = false;
		pauseBacktracking = false;
		nodesPatternSelectedInRed.clear();

		fxLeftPanel.updateSearchBtn();

		calculateGhost();
	}

	private void createHistogramChart(List<Graph> listGraphHistograms, int numHistoCategories) {
		if (listGraphHistograms != null && listGraphHistograms.size() > 0) {
			fxLeftPanel.createTabHistogram(listGraphHistograms, numHistoCategories);
		}
	}

	public void mouseClicked() {

		nodeDisplayArc = null;

		if (!isRunningBacktracking() && !isVisibleHeatMap() && mouseButton == LEFT) {

			nodeGraphPatternSelected = graph.getNodePatternUnderMouse();
			if (nodeGraphPatternSelected != null) {
				if (!nodesPatternSelectedInRed.contains(nodeGraphPatternSelected)) {
					nodesPatternSelectedInRed.add(nodeGraphPatternSelected);
					nodeGraphPatternSelected.setType(Constants.GRAPH_DB_NODE_PATTERN_SELECTED_TYPE);
				} else {
					nodesPatternSelectedInRed.remove(nodeGraphPatternSelected);
					nodeGraphPatternSelected.setType(Constants.GRAPH_DB_NODE_PATTERN_DEFAULT_TYPE);
				}

				if (nodesPatternSelectedInRed.size() > 0) {
					int[] ids = new int[nodesPatternSelectedInRed.size()];
					String[] labels = new String[nodesPatternSelectedInRed.size()];
					// String labels = "";
					for (int i = 0; i < nodesPatternSelectedInRed.size(); i++) {
						Vertex v = nodesPatternSelectedInRed.get(i);

						ids[i] = v.getId();
						labels[i] = v.getLabel();
						// if (i == 0) {
						// labels = v.getLabel();
						// } else {
						// labels = labels + ", " + v.getLabel();
						// }
					}
					fxRightPanel.updateSelectedVertex(ids, labels);
				} else {
					fxRightPanel.clearListEmbeddings();
				}
				hypergraph.getListHyperedge().clear();
				// graph.getListHyperedge().clear();
			}
		}
	}

	public void mouseDragged() {
		nodeDisplayArc = null;

		if (!isRunningBacktracking()) {
			// ABS() to prevent shifting when open a file dialog box
			// if (!guiQueryPanel.isMouseOverQueryPanel() && abs(mouseX -
			// pmouseX) <
			// 100 && abs(mouseY - pmouseY) < 100) {
			graph.setxView(graph.getxView() - (mouseX - pmouseX) / graph.getZoom());
			graph.setyView(graph.getyView() - (mouseY - pmouseY) / graph.getZoom());
			mouseDragged = true;
			// }
		}
	}

	public void mousePressed() {
		nodeDisplayArc = null;
	}

	public void mouseReleased() {
		nodeDisplayArc = null;

		if (mouseDragged) {
			// Call when the previous action was mouseDragged
			// Thus, avoid to call mouseClicked() function
			mouseWheel = true;
			mouseDragged = false;
		}
	}

	private boolean nodesAreInEmbedding(int[] nodesIds, int[] embedding) {
		for (int node : nodesIds) {
			if (!isNodeInEmbedding(node, embedding)) {
				return false;
			}
		}
		return true;
	}

	private boolean isNodeInEmbedding(int nodeId, int[] embedding) {
		for (int nodeCurrent : embedding) {
			if (nodeCurrent == nodeId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to create a list of embedding by a list of id nodes patterns
	 * 
	 * @param nodesIds
	 *            [] ids of pattern nodes
	 * @param nodesLabels
	 *            labels
	 */
	public void embeddingByNodes(int[] nodesIds, String[] nodesLabels) {
		noLoop();
		List<Graph> listEmbeddings = new ArrayList<Graph>();

		for (int[] embedding : graph.getListEmbeddings()) {

			if (nodesAreInEmbedding(nodesIds, embedding)) {
				// To verify that all nodes ids in the line exist in the current
				// ListNodePattern-that because we can filter by histogram
				if (graph.isAllNodesInListPattern(embedding)) {

					Graph g = new Graph();
					// Adding the nodes of the embedding
					// Has the same order than the node ids in GraphQuery
					for (int nodeId : embedding) {
						Vertex node = new Vertex(GraphUtil.getNodeById(graph.getListNodePattern(), nodeId));
						g.addNode(node);
					}

					g.setMbr(MathUtil.calculateMinimalBoundingRectangle(g.getListNode()));

					// Graph Query Topology is already ordered
					// Adding edges to g from the graphQuery
					// Has the same order than the node ids in GraphQuery
					for (Edge edge : GraphQuerying.graphQuery.getListEdge()) {
						int idNodeSource = edge.getIdSource();
						int idNodeTarget = edge.getIdTarget();
						int type = edge.getType();

						// In embedding
						int idSourceMatchEmbedding = embedding[idNodeSource];
						int idTargetMatchEmbedding = embedding[idNodeTarget];

						String labelEdge = GraphQuerying.edgeTypes.get(edge.getType()).getLabel();
						Stroke stroke = new Stroke(true, ColorShape.getHSBGoogle_ColorCategory(type),
								Constants.LIST_EMBEDDING_EDGE_STROKE_OPACITY,
								Constants.LIST_EMBEDDING_EDGE_STROKE_WEIGHT);
						Fill fill = new Fill();
						double distance = 0.0d;
						boolean isLabelled = false;
						boolean isVisible = true;
						Edge e = new Edge(0, labelEdge, type, new PositionShape(), stroke, fill, isLabelled, isVisible,
								idSourceMatchEmbedding, idTargetMatchEmbedding, distance);
						g.addEdge(e);
					}
					g.setId(listEmbeddings.size());
					listEmbeddings.add(g);
				}
			}

		}

		List<Graph> listGraphAbstraction = new ArrayList<Graph>(createListGraphAbstraction(listEmbeddings));

		// GET THE ABSTRACTION OF THE EMBEDDING GRAPHS
		if (listGraphAbstraction.size() > 0) {

			fxRightPanel.updateTabListEmbedding(listGraphAbstraction);
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(Constants.GUI_TITLE_MSG_DIALOG);
			alert.setHeaderText(null);
			alert.setContentText(Constants.LIST_EMBEDDING_MSG_EMPTY + Arrays.asList(nodesLabels));
			alert.showAndWait();
		}
		loop();
	}

	// /**
	// * Method to merge to list of edge by type
	// *
	// * @param oneList
	// * first list of edges
	// * @param twoList
	// * second list of edges
	// * @return list of edge merged
	// */
	// public List<Edge> mergeEdges(List<Edge> oneList, List<Edge> twoList) {
	// // PONER UN LIMITE AL SUBIR
	// double subir = 0.5;
	// for (Edge oneEdge : oneList) {
	// for (Edge twoEdge : twoList) {
	// if (oneEdge.equals(twoEdge)) {
	// double oneEdgeStroke = oneEdge.getStroke().getStrokeWeight();
	// oneEdge.getStroke().setStrokeWeight(oneEdgeStroke + subir);
	// }
	// }
	// }
	// return oneList;
	// }

	/**
	 * Method to create an abstraction of a list of graph by topology
	 * 
	 * @param listGraphs
	 *            List of Graphs initial
	 * @return a new List of Graphs with the same vertices and edges merged
	 */
	private List<Graph> createListGraphAbstraction(List<Graph> listGraphs) {

		List<Graph> listGraphAbstracted = new ArrayList<Graph>();

		// new
		IntObjectHashMap<Graph> uniqueGraphs = new IntObjectHashMap<Graph>();
		// changing id to faster after
		ObjectIntHashMap<Graph> tmpControlIdGraph = new ObjectIntHashMap<Graph>();
		for (Graph graph : listGraphs) {
			int id;
			if (!tmpControlIdGraph.containsKey(graph)) {
				id = tmpControlIdGraph.size();
				graph.setId(id);
				tmpControlIdGraph.put(graph, id);
			} else {
				id = tmpControlIdGraph.get(graph);
				graph.setId(id);
			}
		}

		for (Graph graph : listGraphs) {
			if (!uniqueGraphs.containsValue(graph)) {
				graph.setNumberEmbedding(1);
				uniqueGraphs.put(graph.getId(), graph);
			} else {
				Graph g = uniqueGraphs.get(graph.getId());
				try {
					Graph gFusion = g.fusion(graph);
					gFusion.setNumberEmbedding(g.getNumberEmbedding() + 1);
					uniqueGraphs.put(graph.getId(), gFusion);
				} catch (Exception e) {
					uniqueGraphs.put(graph.getId(), g);
					logger.info("error in fusion embedding " + g);
					// TODO: handle exception
				}
			}
		}

		MutableIntSet mutableInt = uniqueGraphs.keySet();
		MutableIntIterator itrCurrent = mutableInt.intIterator();
		while (itrCurrent.hasNext()) {
			int key = itrCurrent.next();
			Graph gFusion = (Graph) uniqueGraphs.get(key);
			listGraphAbstracted.add(gFusion);
		}

		return listGraphAbstracted;
	}

	/**
	 * Method to create a list of grqph for every embedding in the listEmbeddins
	 * (histogram)
	 * 
	 * @return
	 */
	public List<Graph> getGraphsForHistogram() {
		List<Graph> listGraphHistograms = new ArrayList<Graph>();

		for (int[] embedding : graph.getListEmbeddings()) {
			// Creating a graph from one embedding
			Graph graphEmbedding = new Graph();
			// Adding nodes to the graph
			for (int nodeId : embedding) {
				Vertex vertexEmbedding = new Vertex(graph.getListNode().get(nodeId));
				graphEmbedding.addNode(vertexEmbedding);
			}
			// Adding edge from graphQuery
			graphEmbedding.setMbr(MathUtil.calculateMinimalBoundingRectangle(graphEmbedding.getListNode()));
			listGraphHistograms.add(graphEmbedding);
		}
		return listGraphHistograms;
	}

	/**
	 * To clean ALL
	 */
	public void emptyAllPatternsInMainGraph() {

		// setting visible all the nodes in listNode
		graph.setListNodeVisible(true);

		//
		hypergraph.getListHyperedge().clear();
		graph.getListNodePattern().clear();
		graph.getListEmbeddings().clear();
		graph.getListEmbeddingsNodeNumAppears().clear();
		graph.getListEmbeddingLabels().clear();

		// clear heatmap
		listHeatMap = new ArrayList<Vertex>();
		isSearchingPattern = false;

		// clear histogram
		fxLeftPanel.clearTabHistogram();

		// clear list embedding
		fxRightPanel.clearListEmbeddings();

		// clear sumgra
		runningBacktracking = false;
		pauseBacktracking = false;
		stopBacktracking = false;

		// clear ghost
		isFinishGhost = false;

		fxLeftPanel.updateSearchBtn();
	}

	public void clearKelpDiagrams() {
		// noLoop();
		// hypergraph.getListHyperedge().clear();
		hypergraph = new Hypergraph();
		// loop();
	}

	public void createKelpDiagrams(List<EmbeddingItem> listEmbeddingItems) {
		noLoop();

		graph.setShowEmbeddingLabels(false);

		selectedEmbeddingItems = new ArrayList<EmbeddingItem>(listEmbeddingItems);
		drawKelpDiagrams(selectedEmbeddingItems);
		loop();
	}

	// EL mst es calculado una sola vez xq el zoo no va afectar el mst
	// a ver despues
	/**
	 * @param listSelectedEmbeddings
	 */
	private void drawKelpDiagrams(List<EmbeddingItem> listSelectedEmbeddings) {

		hypergraph = new Hypergraph();
		hypergraph.setZoom(graph.getZoom());

		IntIntHashMap historic = new IntIntHashMap();

		for (EmbeddingItem embeddingItem : listSelectedEmbeddings) {
			double hue = embeddingItem.getBorderColor().getHue(); // 0-1
			double saturation = embeddingItem.getBorderColor().getSaturation();// 0-1
			double brightness = embeddingItem.getBorderColor().getBrightness();// 0-1

			int colorKelp = color((float) hue, (float) saturation * 100, (float) brightness * 100, (float) 100);

			// MAKE AN HYPEREDGE FOR EACH EMBEDDING ITEM

			// 1 Get the minimum spanning tree
			List<EdgeMst> listEdgeMST = GraphUtil.getMinimumSpanningTree(embeddingItem.getGraph());

			// 2 Create an Hyperedge with nodes and edges
			Hyperedge hyperedge = new Hyperedge();

			hyperedge.setOrderToDraw(hypergraph.getListHyperedge().size());

			// For each edge in Minimum Spanning Tree List
			for (EdgeMst edgeMst : listEdgeMST) {

				Stroke stroke = new Stroke(true, colorKelp, 100, 1);
				Fill fill = new Fill(true, colorKelp, 100);

				int idNodeSource;
				int idNodeTarget;

				if (!historic.containsValue(edgeMst.getvTrue())) {
					idNodeSource = historic.size();
					historic.put(idNodeSource, edgeMst.getvTrue());
				} else {
					idNodeSource = GraphUtil.getKeyOfValue(historic, edgeMst.getvTrue());
				}

				if (!historic.containsValue(edgeMst.getwTrue())) {
					idNodeTarget = historic.size();
					historic.put(idNodeTarget, edgeMst.getwTrue());
				} else {
					idNodeTarget = GraphUtil.getKeyOfValue(historic, edgeMst.getwTrue());
				}

				Vertex vSource = new Vertex(graph.getListNode().get(edgeMst.getvTrue()));
				vSource.setId(idNodeSource);
				vSource.setFill(fill);
				vSource.getStroke().setStroked(false);
				vSource.setLabelled(true);

				Vertex vTarget = new Vertex(graph.getListNode().get(edgeMst.getwTrue()));
				vTarget.setId(idNodeTarget);
				vTarget.setFill(fill);
				vTarget.getStroke().setStroked(false);
				vTarget.setLabelled(true);

				// Adding node source and target to the HYPERGRAPH
				hypergraph.addNode(vSource);
				hypergraph.addNode(vTarget);

				// Adding node source and target to the HyperEdge
				hyperedge.addNode(vSource);
				hyperedge.addNode(vTarget);

				// Adding an edge to the HyperEdge
				int edgeId = 1;
				int edgeType = 5;
				PositionShape edgePosition = null;
				double distance = 0;
				Edge edge = new Edge(edgeId, "", edgeType, edgePosition, stroke, fill, false, false, idNodeSource,
						idNodeTarget, distance);
				hyperedge.addEdge(edge);
			}

			// add the HyperEdge to the HYPERGRAPH
			hypergraph.addHyperedge(hyperedge);

			// HyperEdge are updated in the HYPERGRAPH as they appears
			// Stroke Nodes according to the HyperEdges appears in HYPERGRAPH
			for (Vertex node : hyperedge.getListNode()) {
				int numAppearsNode = hypergraph.numAppearsANodeInHyperedges(node);
				double newDiametre = numAppearsNode * kelpFacteurNode;
				node.setDiameter(newDiametre);
			}
			// Stroke Edges according to the hyperedges appears in hypergraph
			for (Edge edge : hyperedge.getListEdge()) {
				int numAppearsEdge = hypergraph.numAppearsAnEdgeInHyperedges(edge);
				double newStrokeWeight = numAppearsEdge * kelpFacteurEdge;
				edge.getStroke().setStrokeWeight(newStrokeWeight);
			}
		}

		// Collections.sort(hypergraph.getListHyperedge(),
		// Collections.reverseOrder(Constants.HYPEREDGE_SORT_DRAW_COMPARATOR));
		// hypergraph.sortByDrawComparator();

		Collections.sort(hypergraph.getListHyperedge(),
				Collections.reverseOrder(Constants.HYPEREDGE_SORT_DRAW_COMPARATOR));

		// logger.info("TERMINO DRAW FUNCION KELPS");
	}

	private void updateKelpsDiagrams() {
		drawKelpDiagrams(selectedEmbeddingItems);
	}

	/*
	 * ActionPerformed is result of the implementation of ActionListener. And is
	 * called at every click in main menu (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
	}

	// To keep the background changed in buttons of Query Panel
	// Give the impression of a button is selected
	/**
	 * Use the keys to make zoom in and zoom out
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void verifyKeyPressedZoom() {
		if (keyPressed && key == CODED) {
			if (keyCode == UP) {
			} else if (keyCode == DOWN) {
			} else if (keyCode == LEFT) {
			} else if (keyCode == RIGHT) {
			}
		}
	}

	public String imprimir(int[] embedding) {
		String result = "[";
		for (int i : embedding) {
			result = result.concat(String.valueOf(i)).concat(", ");
		}
		result = result.concat("]");
		return result;
	}
	
	/**
	 * Method to fetch the embedding from SUMGRA, and also create a HeatMap.
	 * 
	 * @param listFetchedEmbedding
	 */
	public void fetchEmbeddings(FastList<int[]> listFetchedEmbedding) {

		// Adding the fetched embedding to list historic of embedding
		graph.getListEmbeddings().addAll(listFetchedEmbedding);

		// Loop the fetched list to count the number that appears a node in the
		// embedding
		for (int[] embedding : listFetchedEmbedding) {
			System.out.println(imprimir(embedding));
//			logger.info("----------------------");
			for (int idNodeEmbed : embedding) {
//				logger.info(idNodeEmbed);
				if (!graph.getListEmbeddingsNodeNumAppears().containsKey(idNodeEmbed)) {
					// Add to the list of Number appears the node
					graph.getListEmbeddingsNodeNumAppears().put(idNodeEmbed, 1);
					// Add to the list of Node patterns
					graph.addNodePattern(new Vertex(graph.getListNode().get(idNodeEmbed)));
					// Set visible = false to do not duplicate the draw in list
					// node and list pattern
					graph.getListNode().get(idNodeEmbed).setVisible(false);
				} else {
					// Update the number appears a node embedding
					int numAppears = graph.getListEmbeddingsNodeNumAppears().get(idNodeEmbed);
					graph.getListEmbeddingsNodeNumAppears().put(idNodeEmbed, numAppears + 1);
				}
			}
		}

		// Fill the list heat map
		listHeatMap = new ArrayList<Vertex>(graph.getListNodePattern());
	}

	/**
	 * Method to update the listNodePattern and therefore the Heat Map. Called
	 * from Histogram to update (filter) Heat Map
	 * 
	 * @param listHistogram
	 *            List of Histograms; HistogramItem is a ListOfGraphs
	 */
	public void filterEmbeddingsByHistogram(List<HistogramItem> listHistogram) {

		cursor(WAIT);
		noLoop();

		// Remove the nodes selected in red if there is any
		nodesPatternSelectedInRed.clear();
		//
		clearKelpDiagrams();
		// Remove the listNodePattern list in order to fill after with the
		// selected histogram
		graph.getListNodePattern().clear();
		// setting visible all the nodes in listNode
		graph.setListNodeVisible(true);

		// Loop the listHistogram in order to create a new listNodePattern and
		// fill a new listHeatMap
		List<Vertex> nodesToFillListPatterns = new ArrayList<Vertex>();
		for (HistogramItem histogram : listHistogram) {
			// Only if the histogram isSelected
			if (histogram.isSelected()) {
				for (Vertex aNodePattern : histogram.getAllVertexOfListGraph()) {
					if (!nodesToFillListPatterns.contains(aNodePattern)) {
						aNodePattern.setVisible(true);
						nodesToFillListPatterns.add(aNodePattern);
						// Setting visible = false to no repeat the draw in
						// listNode
						graph.getListNode().get(aNodePattern.getId()).setVisible(false);
					}
				}
			}
		}

		// Fill the listNodePatterns
		graph.getListNodePattern().addAll(nodesToFillListPatterns);
		// Normalization of the diameter of the list of node patterns
		graph.normalizationDiameterNodePatterns(Constants.VISUAL_GRAPH_DB_PATTERN_NODE_DIAMETER_MIN,
				Constants.VISUAL_GRAPH_DB_PATTERN_NODE_DIAMETER_MAX);
		// Sort the listNodePatterns by diameter after normalization
		graph.sortListNodePattern(0, Constants.NODE_DIAMETER_COMPARATOR);

		// Fill the list heat map
		listHeatMap = new ArrayList<Vertex>(nodesToFillListPatterns);

		appBlocked = false;
		mouseWheel = true;
		mouseDragged = false;

		loop();
		cursor(ARROW);

		// Clear the list of embeddings
		fxRightPanel.clearListEmbeddings();
	}

	public void setSearchingPattern(boolean isSearchingPattern) {
		this.isSearchingPattern = isSearchingPattern;
	}

	public boolean isSearchingPattern() {
		return isSearchingPattern;
	}

	public void saveScreenshot(File file) {
		this.saveScreenshot = true;
		this.savePathScreenshot = file.getAbsolutePath();
	}

	public boolean isStopBacktracking() {
		return stopBacktracking;
	}

	public void setStopBacktracking(boolean stopBacktracking) {
		this.stopBacktracking = stopBacktracking;
	}

	public boolean isRunningBacktracking() {
		return runningBacktracking;
	}

	public void setRunningBacktracking(boolean runningBacktracking) {
		GraphDBView.runningBacktracking = runningBacktracking;
	}

	public boolean isPauseBacktracking() {
		return pauseBacktracking;
	}

	public void setPauseBacktracking(boolean pauseBacktracking) {
		this.pauseBacktracking = pauseBacktracking;
	}

	public boolean isAppBlocked() {
		return appBlocked;
	}

	public void setAppBlocked(boolean appBlocked) {
		this.appBlocked = appBlocked;
	}

	public void setShowKelpLines(boolean showKelpLines) {
		this.showKelpLines = showKelpLines;
	}

	public boolean isShowKelpLines() {
		return showKelpLines;
	}

	public List<IntObjectHashMap<Object>> getGhostFinal() {
		return ghostFinal;
	}

	public boolean isFinishGhost() {
		return isFinishGhost;
	}

	public void setFinishGhost(boolean isFinishGhost) {
		this.isFinishGhost = isFinishGhost;
	}

	public void setShowVerticesGraphDB(boolean showVerticesGraphDB) {
		this.showVerticesGraphDB = showVerticesGraphDB;
	}

	public void setShowEdges(boolean showEdges) {
		this.showEdges = showEdges;
	}

	public boolean isShowVerticesGraphDB() {
		return showVerticesGraphDB;
	}

	public boolean isShowEdges() {
		return showEdges;
	}

	public void setShowEmbeddingsGraphDB(boolean showEmbeddingsGraphDB) {
		this.showEmbeddingsGraphDB = showEmbeddingsGraphDB;
	}

	public Graph getGraphGhostInternal() {
		return ghostInternal;
	}

	public Map<Integer, List<Edge>> getMapGhostExternal() {
		return ghostExternal;
	}

	public void setHeatMapTransitionZoom(int heatMapTransitionZoom) {
		this.heatMapTransitionZoom = heatMapTransitionZoom;
	}

	public void setKelpFacteurNode(int kelpFacteurNode) {
		this.kelpFacteurNode = kelpFacteurNode;
		updateKelpsDiagrams();
	}

	public void setKelpFacteurEdge(int kelpFacteurEdge) {
		this.kelpFacteurEdge = kelpFacteurEdge;
		updateKelpsDiagrams();
	}

	public List<EmbeddingItem> getSelectedEmbeddingItems() {
		return selectedEmbeddingItems;
	}

	public void setSelectedEmbeddingItems(List<EmbeddingItem> selectedEmbeddingItems) {
		this.selectedEmbeddingItems = selectedEmbeddingItems;
	}

	public void setKelpOverlapLines(boolean kelpOverlapLines) {
		this.kelpOverlapLines = kelpOverlapLines;
	}

	public void setHeatMapRadius(int heatMapRadius) {
		this.heatMapRadius = heatMapRadius;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setHistogramNumCategories(int histogramNumCategories) {
		this.histogramNumCategories = histogramNumCategories;
		List<Graph> listGraphHistograms = getGraphsForHistogram();
		fxLeftPanel.createTabHistogram(listGraphHistograms, histogramNumCategories);
	}

}
