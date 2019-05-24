package app.gui.main;

import java.text.DecimalFormat;
import java.util.Comparator;

import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.Hyperedge;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import javafx.geometry.Insets;
import javafx.scene.text.Font;

public final class Constants {

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#");
	public static final int FRAME_MINIMUM_SIZE_WIDTH = 900;
	public static final int FRAME_MINIMUM_SIZE_HEIGHT = 700;
	public static final String MAIN_TITLE = "VERTIGo";

	// RUNNING PATHS
	// "data/others/miserables/"
	// "data/others/sierpinski/"
	// "data/biogrid/"
	// "data/dblp_vis_bd/"
	// "data/dip/"
	// "data/hoja/"

	public static final String PATH_DATA = "data/dblp_vis_bd/";
	public static final String MAIN_GRAPH_FILE = "graph_fm3.gml";
	public static final String SUMGRA_GRAPH_FILE = "relationships.txt";
	public static final String QUERY_GRAPH_FILE = "query.txt";
	public static final String RESOURCES_IMAGE_PATH = "img/";
	public static final String RESOURCES_IMAGE_PATH_EDGE_TYPES = "img/edge_types/";

	// ------- JAVAFX -------
	//
	public static final String JAVAFX_STYLE_FILE = "stylesheet.css";

	// ------- MAIN TITLE TOOLBAR -------
	public static final int MAIN_TOOLBAR_HEIGHT = 34;

	// ------- GRAPH QUERY VIEW LAYOUT -------
	//
	public static final int LAYOUT_QUERY_VIEW_WIDTH = 370; // 350 lirmm //370 for suggestion
	public static final int LAYOUT_EMBEDDINGS_VIEW_WIDTH = 590; //430 teaser // 610 for lisembeddings
	public static final String LAYOUT_GRAPH_DB_VIEW_TITLE = "Graph";
	public static final String LAYOUT_GRAPH_QUERY_VIEW_TITLE = "Query";
	public static final String LAYOUT_EMBEDDINGS_VIEW_TITLE = "Embeddings";
	// public static final String LAYOUT_GRAPH_DB_VIEW_TITLE = "Graph DB View";
	// public static final String LAYOUT_GRAPH_QUERY_VIEW_TITLE = "Graph Query
	// View";
	// public static final String LAYOUT_EMBEDDINGS_VIEW_TITLE = "Node Oriented
	// Result List";

	// ----- GRAPH QUERY ----
	//
	public static final int VISUAL_GRAPH_QUERY_BTN_WIDTH = 37; // 38
	public static final int VISUAL_GRAPH_QUERY_BTN_HEIGHT = 33;
	public static final int VISUAL_GRAPH_QUERY_BTN_SEARCH_WIDTH = 110;
	public static final int VISUAL_GRAPH_QUERY_CANVAS_HEIGHT = 300; // 260 lirm
	public static final int VISUAL_GRAPH_QUERY_CANVAS_WIDTH = LAYOUT_QUERY_VIEW_WIDTH - VISUAL_GRAPH_QUERY_BTN_HEIGHT;
	public static final int VISUAL_CANVAS_WIDTH = VISUAL_GRAPH_QUERY_CANVAS_WIDTH; // 1080;
	public static final int VISUAL_CANVAS_HEIGHT = VISUAL_GRAPH_QUERY_CANVAS_HEIGHT; // 1080;

	public static final int VISUAL_GRAPH_QUERY_STATUS_HEIGHT = 30;
	public static final int VISUAL_GRAPH_QUERY_CMBLABELNODES_WIDTH = 150; // 150
	public static final int VISUAL_GRAPH_QUERY_NUMBER_EDGE_TYPES = 18;
	
	// GRAPH QUERY VERTEX GHOST
	public static final int VISUAL_GRAPH_QUERY_PIE_GHOST_WIDTH = 33; //33 lirmm
	public static final int VISUAL_GRAPH_QUERY_PIE_GHOST_HEIGHT = 33; //33
	public static final int VISUAL_GRAPH_QUERY_PIE_GHOST_ANGLE_START = 0;
	
	// GRAPH QUERY VERTEX
	public static final double GRAPH_QUERY_NODE_DIAMETER = 40.0d; //45 for ghost
	public static final Fill GRAPH_QUERY_NODE_FILL = new Fill(true, ColorShape.getHSB_NodePattern(), 0);
	public static final Stroke GRAPH_QUERY_NODE_STROKE = new Stroke(true, 0, 0, 1.0d);
	public static final int GRAPH_QUERY_NODE_IMAGE_WIDTH = 24; //24 lirmm
	public static final int GRAPH_QUERY_NODE_IMAGE_HEIGHT = 24;

	// GRAPH QUERY EDGE (do not have Fill)
	public static final int GRAPH_QUERY_EDGE_PARALLEL_DISTANCE = 11;
	public static final Font GRAPH_QUERY_EDGE_LABEL_FONT = new Font("Arial", 11);
	public static final double GRAPH_QUERY_EDGE_STROKE_OPACITY = 100.0d;
	public static final double GRAPH_QUERY_EDGE_STROKE_WEIGHT = 4.0d;
	public static final double GRAPH_QUERY_EDGE_STROKE_WEIGHT_HOVER = 5.0d;
	public static final double GRAPH_QUERY_EDGE_STROKE_WEIGHT_DRAWING = 7.0d;
	public static final double GRAPH_QUERY_EDGE_ALPHA = 0.5; // 0-1
	public static final int GRAPH_QUERY_EDGE_TOLERANCE = 4;

	// GRAPH QUERY EDGE GHOST (do not have Fill)
	public static final int GRAPH_QUERY_EDGE_GHOST_EXTERNAL_TOP_K = 8;
	public static final int GRAPH_QUERY_EDGE_GHOST_INTERNAL_TOP_K = 3;
	public static final double GRAPH_QUERY_EDGE_GHOST_DISTANCE = 110;
	public static final double[] GRAPH_QUERY_EDGE_STROKE_GHOST_DASHES = new double[] { 5, 10 }; //new double[] { 5, 15 };
	public static final int GRAPH_QUERY_EDGE_IS_GHOST = 0;
	public static final int GRAPH_QUERY_EDGE_WAS_GHOST = 1;
	public static final int GRAPH_QUERY_EDGE_NEVER_GHOST = 2;

	// ------------ HISTOGRAM ----------
	//
	public static final int HISTOGRAM_NUMBER_CATEGORIES = 5;
	public static final String HISTOGRAM_TITLE = "Distribution of Embedding's MBR";
	public static final String HISTOGRAM_X_LABEL = "From lower to higher MBR Area";
	public static final String HISTOGRAM_Y_LABEL = "#Embeddings";
	public static final String HISTOGRAM_ITEM_STYLE_SELECTED = "-fx-bar-fill: #238443;";
	public static final String HISTOGRAM_ITEM_STYLE_DESELECTED = "-fx-bar-fill: #d9f0a3;";

	// ------- MAIN GRAPH DATABASE-------
	//

	public static final int VISUAL_GRAPH_DB_BTN_WIDTH = 37;
	public static final int VISUAL_GRAPH_DB_BTN_HEIGHT = 33;
	public static final int VISUAL_GRAPH_DB_CMBLABELNODES_WIDTH = 150;
	public static final int VISUAL_GRAPH_DB_CMBLABELNODES_HEIGHT = 25;
	public static final int VISUAL_GRAPH_DB_PATTERN_NODE_DIAMETER_MIN = 13;
	public static final int VISUAL_GRAPH_DB_PATTERN_NODE_DIAMETER_MAX = 35;
	public static final double VISUAL_GRAPH_DB_ZOOM_THRESHOLD_OUT = (float) 0.1;
	public static final double VISUAL_GRAPH_DB_ZOOM_THRESHOLD_IN = (float) 10000;
	// always > 1.0
	public static final double VISUAL_GRAPH_DB_ZOOM_FACTOR_IN = (float) 1.1;
	// always < 1.0
	public static final double VISUAL_GRAPH_DB_ZOOM_FACTOR_OUT = (float) 0.9;

	// MAIN GRAPH DATABASE VERTEX
	public static final double GRAPH_DB_NODE_DIAMETER = 3.0d;
	public static final int GRAPH_DB_NODE_TYPE = 0; // no tiene nada que ver
	public static final int GRAPH_DB_NODE_PATTERN_DEFAULT_TYPE = 0;
	public static final int GRAPH_DB_NODE_PATTERN_SELECTED_TYPE = 1;
	public static final int GRAPH_DB_NODE_PATTERN_FIXED_TYPE = 2;
	public static final Fill GRAPH_DB_NODE_FILL = new Fill(true, ColorShape.getHSB_Black(), 30.0d);
	public static final Stroke GRAPH_DB_NODE_STROKE = new Stroke(false, ColorShape.getHSB_Black(), 100.0d, 1.0d);
	public static final Stroke GRAPH_DB_NODE_ARC_STROKE = new Stroke(false, ColorShape.getHSB_Arc(), 100.0d, 15.0d);
	public static final double GRAPH_DB_NODE_ARC_DIAMETER = 50.0d;

	// MAIN GRAPH DATABASE VERTEX PATTERN

	public static final Fill GRAPH_DB_NODE_PATTERN_RECT_BACKGROUND_FILL = new Fill(true,
			ColorShape.getHSB_NodePattern(), 100.0d);
	public static final double GRAPH_DB_NODE_PATTERN_RECT_PADDING = 0;

	public static final Fill GRAPH_DB_NODE_PATTERN_FILL = new Fill(true, ColorShape.getHSB_NodePattern(), 100.0d);
	public static final Stroke GRAPH_DB_NODE_PATTERN_STROKE = new Stroke(true, ColorShape.getHSB_NodePatternStroke(),
			100.0d, 1.0d);
	public static final Fill GRAPH_DB_NODE_PATTERN_FILL_HOVER = new Fill(true, ColorShape.getHSB_NodePatternHover(),
			100.0d);
	public static final Stroke GRAPH_DB_NODE_PATTERN_STROKE_HOVER = new Stroke(true,
			ColorShape.getHSB_NodePatternStroke(), 100.0d, 1.0d);
	public static final Fill GRAPH_DB_NODE_PATTERN_FILL_SELECTED = new Fill(true,
			ColorShape.getHSB_NodePatternSelected(), 100.0d);
	public static final Stroke GRAPH_DB_NODE_PATTERN_STROKE_SELECTED = new Stroke(true,
			ColorShape.getHSB_NodePatternSelectedStroke(), 100.0d, 1.0d);
	public static final Fill GRAPH_DB_NODE_PATTERN_FILL_SELECTED_HOVER = new Fill(true,
			ColorShape.getHSB_NodePatternSelectedHover(), 100.0d);
	public static final Stroke GRAPH_DB_NODE_PATTERN_STROKE_SELECTED_HOVER = new Stroke(true,
			ColorShape.getHSB_NodePatternSelectedHoverStroke(), 100.0d, 1.0d);

	// MAIN GRAPH DATABASE EDGES
	public static final int GRAPH_DB_EDGE_STROKE_OPACITY = 100;
	public static final double GRAPH_DB_EDGE_STROKE_WEIGHT = 1.0d;

	// ------- EMBEDDINGS VIEW LAYOUT -------
	//

	public static final Font LIST_EMBEDDING_LEGENDE_TITLE_LABEL_FONT = new Font("Arial", 13);
	public static final Font LIST_EMBEDDING_LEGENDE_SUBTITLE_LABEL_FONT = new Font("Arial", 12);
	
	public static final int LIST_EMBEDDING_COLUMN_MBR_WIDTH = 45;
	public static final int LIST_EMBEDDING_COLUMN_AGGREGATION_WIDTH = 55;
	public static final int LIST_EMBEDDING_COLUMN_GRAPH_WIDTH = LAYOUT_EMBEDDINGS_VIEW_WIDTH
			- LIST_EMBEDDING_COLUMN_MBR_WIDTH - LIST_EMBEDDING_COLUMN_AGGREGATION_WIDTH - 45;
	public static final int LIST_EMBEDDING_COLUMN_GRAPH_HEIGHT = 170; // 170 teaser  //140 normal

	public static final int LIST_EMBEDDING_BTN_HEIGHT = 33;
	public static final int LIST_EMBEDDING_BTN_VISUALIZE_WIDTH = 100;
	public static final int LIST_EMBEDDING_BTN_CLEAR_WIDTH = 60;
	public static final int LIST_EMBEDDING_BTN_HEIGHT_ROW = 55;
	public static final int LIST_EMBEDDING_BTN_WIDTH_COLUMNA2 = 140;
	public static final int LIST_EMBEDDING_BTN_WIDTH_COLUMNA1 = LAYOUT_EMBEDDINGS_VIEW_WIDTH
			- LIST_EMBEDDING_BTN_WIDTH_COLUMNA2 - 45;

	public static final int LIST_EMBEDDING_COLUMN_GRAPH_BORDER_SELECTED = 6;
	public static final int LIST_EMBEDDING_MAXIMUM_SELECTED_ITEMS = 5; // Max 7
	public static final String LIST_EMBEDDING_MSG_EMPTY = "Not embeddings for: ";

	// LIST EMBEDDING VERTEX
	public static final double LIST_EMBEDDING_NODE_DIAMETER = 35.0d; //42 LIRMM
	public static final Font LIST_EMBEDDING_NODE_LABEL_FONT = new Font("Arial", 11); //14 LIRMM
	public static final Stroke LIST_EMBEDDING_NODE_STROKE = new Stroke(true, ColorShape.getHSB_NodePatternStroke(),
			100.0d, 1.0d);

	// LIST EMBEDDING EDGE (do not have Fill)
	public static final Font LIST_EMBEDDING_EDGE_LABEL_FONT = new Font("Arial", 11); //13 LIRMM
	public static final int LIST_EMBEDDING_EDGE_PARALLEL_DISTANCE = 6; //10 LIRMM
	public static final double LIST_EMBEDDING_EDGE_STROKE_OPACITY = 1; // 0-1
	public static final double LIST_EMBEDDING_EDGE_STROKE_WEIGHT = 4; // 1.5;

	// ------- KELP LIKE DIAGRAMS -------
	//
	public static final double KELP_DECREASE_SATURATION_FACTOR = 1.8d;
	public static final boolean KELP_EDGE_VISIBLE = true;
	public static final boolean KELP_CHECK_OVERLAP_EDGE_NODE = true;
	public static final double KELP_OVERLAP_MAX_DAMPING_FACTOR = 1.1; //> 1
	public static final int KELP_EDGE_FACTOR = 20;
	public static final int KELP_NODE_FACTOR = 35;
	public static final int KELP_TEXT_SIZE = 15;
	public static final Fill KELP_TEXT_BACKGROUND_FILL = new Fill(true, ColorShape.getHSB_KelpTextBackground(), 80.0d);
	public static final double KELP_TEXT_BACKGROUND_PADDING = 5.0d;

	// ------- HEATMAPS -------
	//
	public static final int HEATMAP_INIT_TRANSITION_ZOOM = 6;
	public static final int HEATMAP_INIT_RADIUS = 10;

	// ------------ SUMGRA ------------
	//
	public static final String SUMGRA_MSG_LINE_1 = "Updating";
	public static final double SUMGRA_RECTANGLE_OPACITY = 30;
	// 1=on voit update; 1000 non
	public static final int SUMGRA_THRESHOLD_BUFFER_BACKTRAKING = 1000;
	// higher we saw better
	public static final int SUMGRA_SLEEP_BACKTRAKING = 0;

	// ------------ OGDF ------------
	//
	public static final String OGDF_PATH_TEMP_FM3 = "temp.gml";
	public static final String OGDF_PATH_TEMP_LAYOUT_FM3 = "temp-layout.gml";

	// ------------ GUI Messages ------------
	//
	public static final String GUI_TITLE_MSG_DIALOG = "Graphes";
	public static final String GUI_MSG_PATH_FILE = "Path: ";
	public static final String GUI_MSG_ORDER_GRAPH = "Order of the graph: ";
	public static final String GUI_MSG_NUM_EDGES = "Number of edges: ";
	public static final String GUI_MSG_SAVE = "File saved to: ";
	public static final String GUI_MSG_CONFIRMATION_PLAY = "Executed Fast Multipole Multilevel Method (FM3) Force Directed Layout in Query Graph";
	public static final String GUI_MSG_CONFIRMATION_NEW = "Do you want to save the Query Graph?";
	public static final String GUI_LIST_EMBEDDING_EMPTY = "No embeddings found";

	// ------- PREFERENCES -------
	//
	public static final int PREF_HEIGHT = 740;
	public static final int PREF_VERTICAL_GAP = 10;
	public static final int PREF_HORIZONTAL_GAP = 15;
	public static final Insets PREF_INSETS = new Insets(10, 10, 10, 10);
	public static final double PREF_WIDTH_COLUMNA1 = (LAYOUT_QUERY_VIEW_WIDTH / 3);
	public static final double PREF_WIDTH_COLUMNA2 = LAYOUT_QUERY_VIEW_WIDTH - PREF_WIDTH_COLUMNA1
			- 2 * PREF_VERTICAL_GAP - PREF_INSETS.getLeft() - PREF_INSETS.getRight();

	public static final String PREF_TAB_TITLE = "Preferences";
	public static final String PREF_PANE_GRAPH_DATABASE = "Graph";
	public static final String PREF_PANE_GRAPH_ENGINE = "Query Engine - SumGRa";
	public static final String PREF_PANE_GRAPH_EMBEDDINGS = "Embedding Results";
	public static final String PREF_PANE_GRAPH_QUERY = "Query";
	public static final String PREF_PANE_KELP_DIAGRAMS = "Set Relations - Kelp-like Diagrams";

	// --------- COMPARATORS -------------
	//
	public static final Comparator<Hyperedge> HYPEREDGE_SORT_DRAW_COMPARATOR = new Comparator<Hyperedge>() {
		public int compare(Hyperedge hyperedge1, Hyperedge hyperedge2) {
			Integer orderToDraw1 = hyperedge1.getOrderToDraw();
			Integer orderToDraw2 = hyperedge2.getOrderToDraw();
			return orderToDraw1.compareTo(orderToDraw2);
		}
	};

	public static final Comparator<Graph> GRAPH_MBR_COMPARATOR = new Comparator<Graph>() {
		public int compare(Graph g1, Graph g2) {
			Double mbr1 = g1.getMbr();
			Double mbr2 = g2.getMbr();
			return mbr1.compareTo(mbr2);
		}
	};

	public static final Comparator<Graph> GRAPH_NUM_EMBEDDING_COMPARATOR = new Comparator<Graph>() {
		public int compare(Graph g1, Graph g2) {
			Integer numEmbedding1 = g1.getNumberEmbedding();
			Integer numEmbedding2 = g2.getNumberEmbedding();
			return numEmbedding1.compareTo(numEmbedding2);
		}
	};

	public static final Comparator<Edge> EDGE_TYPE_COMPARATOR = new Comparator<Edge>() {
		public int compare(Edge o1, Edge o2) {
			Integer type1 = o1.getType();
			Integer type2 = o2.getType();
			return type1.compareTo(type2);
		}
	};

	public static final Comparator<Edge> EDGE_WEIGHT_COMPARATOR = new Comparator<Edge>() {
		public int compare(Edge o1, Edge o2) {
			Double weight1 = o1.getWeight();
			Double weight2 = o2.getWeight();
			return weight1.compareTo(weight2);
		}
	};

	public static final Comparator<Vertex> NODE_WEIGHT_COMPARATOR = new Comparator<Vertex>() {
		public int compare(Vertex o1, Vertex o2) {
			Double weight1 = o1.getWeight();
			Double weight2 = o2.getWeight();
			return weight1.compareTo(weight2);
		}
	};

	public static final Comparator<Vertex> NODE_DIAMETER_COMPARATOR = new Comparator<Vertex>() {
		public int compare(Vertex o1, Vertex o2) {
			Double diameter1 = o1.getDiameter();
			Double diameter2 = o2.getDiameter();
			return diameter1.compareTo(diameter2);
		}
	};

	public static final Comparator<Vertex> NODE_LABEL_COMPARATOR = new Comparator<Vertex>() {
		public int compare(Vertex o1, Vertex o2) {
			String label1 = o1.getLabel();
			String label2 = o2.getLabel();
			return label1.compareToIgnoreCase(label2);
		}
	};

	public static final Comparator<Vertex> NODE_ID_COMPARATOR = new Comparator<Vertex>() {
		public int compare(Vertex v1, Vertex v2) {
			Integer idVertex1 = v1.getId();
			Integer idVertex2 = v2.getId();
			return idVertex1.compareTo(idVertex2);
		}
	};

}
