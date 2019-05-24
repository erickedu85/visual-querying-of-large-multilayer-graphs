package app.graph.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.google.common.primitives.Ints;
import com.gs.collections.api.iterator.MutableIntIterator;
import com.gs.collections.api.set.primitive.MutableIntSet;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import app.gui.main.Constants;
import app.heatmap.HeatMapBuilder;
import app.utils.GeoUtil;
import app.utils.GraphUtil;
import app.utils.In;
import app.utils.MathUtil;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * The graph class represent an abstraction of the a Graph G
 * 
 * @author Erick Cuenca
 *
 */
public class Graph {

	public static PApplet parent;
	public static Logger logger = Logger.getLogger(Graph.class);
	private String name = "";
	private int id;
	private double zoom = 1.0d;
	private double xView;
	private double yView;
	private int numOfEmbeddings = 0;
	private double mbr = 0.0d;
	// NODES (Order)
	private List<Vertex> listNode = new CopyOnWriteArrayList<Vertex>();
	// EDGES
	private List<Edge> listEdge = new CopyOnWriteArrayList<Edge>();

	// TEST
	private List<Edge> listDTEdge = new CopyOnWriteArrayList<Edge>();

	// ----------- EMBEDDINGS -----------
	// List historic of All embedding from Sumgra (0 1 2)
	private FastList<int[]> listEmbeddings = new FastList<int[]>();
	private IntIntHashMap listEmbeddingsNodeNumAppears = new IntIntHashMap();
	// Nodes (NO order)
	private List<Vertex> listNodePattern = new CopyOnWriteArrayList<Vertex>();

	//
	// EMBEDDING STROKE
	private boolean showEmbeddingStroke = true;
	// EMBEDDING LABEL
	private boolean showEmbeddingLabels = true;
	private boolean showEmbeddingRectangleLabels = false;
	private Fill backgroundEmbeddingRectangle = new Fill(); // Constants.GRAPH_DB_NODE_PATTERN_RECT_BACKGROUND_FILL;
	private double backgroundEmbeddingPadding = 666; // Constants.GRAPH_DB_NODE_PATTERN_RECT_PADDING;

	private List<Vertex> listEmbeddingLabels = new ArrayList<Vertex>();
	private boolean updateListEmbeddigLabels = false;

	// ----------- KELPS -----------
	// private List<Hyperedge> listHyperedge = new
	// CopyOnWriteArrayList<Hyperedge>();

	/**
	 * Initialize graph structure by a listNode and listEdge. Copied by
	 * Dynamically
	 * 
	 * @param listNode
	 *            List of nodes
	 * @param listEdge
	 *            List of edges
	 */
	public Graph(List<Vertex> listNode, List<Edge> listEdge) {
		for (Vertex v : listNode) {
			this.listNode.add(new Vertex(v));
		}
		for (Edge e : listEdge) {
			this.listEdge.add(new Edge(e));
		}
	}

	/**
	 * Method to create a new Graph from another Graph g. Where, list the nodes
	 * copies and list the edges no copies
	 * 
	 * @param g
	 */
	public Graph(Graph g) {
		for (Vertex v : g.getListNode()) {
			this.listNode.add(new Vertex(v));
		}
	}

	/**
	 * Initializes graph structure empty
	 */
	public Graph() {
		listNode = new ArrayList<Vertex>();
		listEdge = new ArrayList<Edge>();
	}

	/**
	 * Initializes a new graph from an input .gml file
	 * 
	 * @param p
	 *            processing PDE
	 * @param in
	 *            the input file
	 */
	public Graph(In in) {
		String allString = in.readAll();
		listNode = in.getNodes(allString);
		listEdge = in.getEdges(allString);
	}

	// private void changeRelationNodePosition() {
	// Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
	//
	// logger.info("height " + parent.getHeight());
	// logger.info("dimension " + screenDimension.getHeight());
	//
	// // 991 lirmm machine
	// // 679 vaio
	//
	// int height = 991; // (int) screenDimension.getHeight();
	// for (Vertex vertex : listNode) {
	// vertex.getPosition().setY1(height - vertex.getPosition().getY1());
	// }
	// }

	/**
	 * Collision detection, get the node pattern that is under the mouse
	 * 
	 * @return Node pattern under the mouse, null otherwise
	 */
	public Vertex getNodePatternUnderMouse() {
		for (Vertex node : listNodePattern) {
			PositionShape nodePosition = node.getPosition();
			PositionShape translateNodePosition = GraphUtil.translatePosition(nodePosition.getX1(),
					nodePosition.getY1(), zoom);
			node.setPosition(translateNodePosition);
			double tolerance = node.getDiameter() / 2;
			if (isNodeNearMouse(node, parent.mouseX, parent.mouseY, tolerance)) {
				// back to initial position and return the node
				node.setPosition(nodePosition);
				return node;
			}
			node.setPosition(nodePosition);
		}
		return null;
	}

	/**
	 * Collision detection. Verify if a node in the graph database is near to
	 * the mouse
	 * 
	 * @param node
	 * @param mouseX
	 * @param mouseY
	 * @param tolerance
	 * @return
	 */
	public boolean isNodeNearMouse(Vertex node, double mouseX, double mouseY, double tolerance) {
		if (GraphUtil.isNearCircle(node.getPosition().getX1() + (-xView * zoom),
				node.getPosition().getY1() + (-yView * zoom), mouseX, mouseY, tolerance)) {
			return true;
		}
		return false;
	}

	/**
	 * Collision detection. Method to verify if an edge is near to the mouse
	 * 
	 * @param edge
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public boolean isEdgeNearMouse(Edge edge, double mouseX, double mouseY, int tolerance) {
		PVector p1 = new PVector((int) edge.getPosition().getX1(), (int) edge.getPosition().getY1());
		PVector p2 = new PVector((int) edge.getPosition().getX2(), (int) edge.getPosition().getY2());
		PVector mouse = new PVector((int) mouseX, (int) mouseY);
		if (GraphUtil.isNearLine(mouse, p1, p2, tolerance)) {
			return true;
		}
		return false;
	}

	/**
	 * Return a boolean if a node is inside the screen. To draw only nodes
	 * inside the screen
	 * 
	 * @param node
	 * @return true or false
	 */
	private boolean isVertexInsideScreen(Vertex node) {
		// parent.getWidth() - Constants.LEFT_PANEL_WIDTH
		return ((node.getPosition().getX1() + (-xView * zoom) >= 0)
				&& (node.getPosition().getX1() + (-xView * zoom) < (parent.getWidth())))
				&& (((node.getPosition().getY1() + (-yView * zoom) >= 0))
						&& (node.getPosition().getY1() + (-yView * zoom) < parent.getHeight()));
	}

	/**
	 * Method to display nodes
	 * 
	 */
	public void displayNode() {
		for (Vertex node : listNode) {
			// Save the original coordinates
			PositionShape nodePosition = node.getPosition();
			// To calculate the ZOOM from the start position
			PositionShape newCoordinates = GraphUtil.translatePosition(nodePosition.getX1(), nodePosition.getY1(),
					zoom);
			node.setPosition(newCoordinates);

			if (isVertexInsideScreen(node) && node.isVisible()) {
				node.display();
			}
			node.setPosition(nodePosition);
		}
	}

	/**
	 * Method to display the pattern nodes
	 * 
	 * @return
	 */

	public void displayNodePattern(boolean mouseWheel) {

		List<Vertex> verticesInsideScreen = new CopyOnWriteArrayList<Vertex>();// ArrayList<>();
		for (Vertex node : listNodePattern) {
			// Save the original coordinates
			PositionShape nodePosition = node.getPosition();
			// To calculate the ZOOM from the start position
			PositionShape translateNodePosition = GraphUtil.translatePosition(nodePosition.getX1(),
					nodePosition.getY1(), zoom);
			node.setPosition(translateNodePosition);
			node.getStroke().setStroked(showEmbeddingStroke);

			if (isVertexInsideScreen(node) && node.isVisible()) {
				verticesInsideScreen.add(new Vertex(node));
				double toleranceMouse = node.getDiameter() / 2;
				if (node.getType() == Constants.GRAPH_DB_NODE_PATTERN_SELECTED_TYPE) {
					node.setFill(Constants.GRAPH_DB_NODE_PATTERN_FILL_SELECTED);
					node.setStroke(Constants.GRAPH_DB_NODE_PATTERN_STROKE_SELECTED);
					if (isNodeNearMouse(node, parent.mouseX, parent.mouseY, toleranceMouse)) {
						node.setFill(Constants.GRAPH_DB_NODE_PATTERN_FILL_SELECTED_HOVER);
						node.setStroke(Constants.GRAPH_DB_NODE_PATTERN_STROKE_SELECTED_HOVER);
					}
				} else if (node.getType() == Constants.GRAPH_DB_NODE_PATTERN_DEFAULT_TYPE) {
					node.setFill(Constants.GRAPH_DB_NODE_PATTERN_FILL);
					node.setStroke(Constants.GRAPH_DB_NODE_PATTERN_STROKE);
					if (isNodeNearMouse(node, parent.mouseX, parent.mouseY, toleranceMouse)) {
						node.setFill(Constants.GRAPH_DB_NODE_PATTERN_FILL_HOVER);
						node.setStroke(Constants.GRAPH_DB_NODE_PATTERN_STROKE_HOVER);
					}
				}
				node.display();
			}
			node.setPosition(nodePosition);
		}

		if (showEmbeddingLabels) {
			if (mouseWheel) {
				updateListEmbeddigLabels = false;
			}
			if (!mouseWheel && !updateListEmbeddigLabels) {
				listEmbeddingLabels = nodeLabelsWithoutOverlapping(verticesInsideScreen);

				updateListEmbeddigLabels = true;
			} else if (!mouseWheel && updateListEmbeddigLabels) {

				// double min = listaUpdated.get(listaUpdated.size() -
				// 1).getDiameter();
				// double max = listaUpdated.get(0).getDiameter();

				for (Vertex node : listEmbeddingLabels) {
					double textHeight;
					textHeight = node.getDiameter();
					// if (embeddingLabelsRelative) {
					// textHeight = PApplet.map((float) node.getDiameter(),
					// (float) min, (float) max, 10, 30);
					// } else {
					// textHeight = node.getDiameter();
					// }

					// GRAPH_DB_NODE_PATTERN_RECT_FILL
					node.displayLabel(textHeight, showEmbeddingRectangleLabels, backgroundEmbeddingRectangle,
							backgroundEmbeddingPadding);
				}
			}

		}
	}

	/**
	 * Method to return a list of nodes without overlapping, ordering by
	 * priority of the node diameter
	 * 
	 * @param listNodeWithOverlap
	 * @return Return a list of vertex without overlapping
	 */
	private List<Vertex> nodeLabelsWithoutOverlapping(List<Vertex> listNodeWithOverlap) {

		Collections.sort(listNodeWithOverlap, Collections.reverseOrder(Constants.NODE_DIAMETER_COMPARATOR));

		List<Vertex> results = new ArrayList<Vertex>();
		for (Vertex node : listNodeWithOverlap) {
			boolean overlaping = isNodeLabelOverlapInList(node, results);
			if (!overlaping) {
				results.add(new Vertex(node));
			}
		}
		return results;
	}

	/**
	 * @param currentNode
	 * @param listNodePriority
	 * @return true if label is overlap, otherwise false
	 */
	public boolean isNodeLabelOverlapInList(Vertex currentNode, List<Vertex> listNodePriority) {
		double factWidth = 1;
		double factHeight = 1;

		Double height = currentNode.getDiameter();
		//
		parent.textSize((float) (height.floatValue()));
		double width = parent.textWidth(currentNode.getLabel());
		//
		PositionShape rectangleEnds = GeoUtil.calculateRectangleByCenterPoint(currentNode.getPosition(), width, height,
				factWidth, factHeight);

		for (Vertex node : listNodePriority) {
			Double heightNodeCurrent = node.getDiameter();
			parent.textSize((float) (heightNodeCurrent.floatValue()));
			double widthNodeCurrent = parent.textWidth(node.getLabel());
			PositionShape rectangleEndsCurrent = GeoUtil.calculateRectangleByCenterPoint(node.getPosition(),
					widthNodeCurrent, heightNodeCurrent, factWidth, factHeight);
			if (GeoUtil.isOverlappingTwoRectangles(rectangleEnds, rectangleEndsCurrent)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to build a Heat Map, updating by zoom - mousewheel
	 */
	public void displayHeatmap(List<Vertex> listHeatmap, int heatMapRadius) {

		double coordinateX, coordinateY;

		// Two dimensions array
		int[][] ptsHeatMap = new int[listHeatmap.size()][2];
		int[] width = new int[listHeatmap.size()];
		int[] height = new int[listHeatmap.size()];

		for (int i = 0; i < listHeatmap.size(); i++) {
			Vertex node = listHeatmap.get(i);
			// get the Coordinates X and Y depending ZOOM
			PositionShape coordinates = GraphUtil.translatePosition(node.getPosition().getX1(),
					node.getPosition().getY1(), zoom);
			coordinateX = coordinates.getX1();
			coordinateY = coordinates.getY1();

			ptsHeatMap[i][0] = (int) coordinateX;
			ptsHeatMap[i][1] = (int) coordinateY;

			// To get Min and Max
			width[i] = (int) coordinateX;
			height[i] = (int) coordinateY;
		}

		// Changing color Mode for build the Heatmap
		// parent.colorMode(PConstants.RGB, 255, 255, 255, 100);
		int minWidth = Ints.min(width);
		int maxWidth = Ints.max(width);
		int minHeight = Ints.min(height);
		int maxHeight = Ints.max(height);

		// Building heatmap
		// This is the expensive part of the function
		HeatMapBuilder heatBuilder = new HeatMapBuilder(minWidth - heatMapRadius, minHeight - heatMapRadius,
				maxWidth + heatMapRadius, maxHeight + heatMapRadius, ptsHeatMap, heatMapRadius, parent);

		heatBuilder.fillListRastered();
		//
		//
		for (Vertex node : heatBuilder.getListNodeRaster()) {
			node.display();
		}
		// parent.colorMode(PConstants.HSB, 360, 100, 100, 100); //
		// Hue-Saturation-Brightness-Alpha,
	}

	/**
	 * Change the current range of diameter of node patterns into a new target
	 * range
	 * 
	 * @param lowerTargetRange
	 * @param upperTargetRange
	 */
	public void normalizationDiameterNodePatterns(int lowerTargetRange, int upperTargetRange) {
		double lowerCurrentRange = (double) PConstants.MAX_FLOAT;
		double upperCurrentRange = (double) PConstants.MIN_FLOAT;
		MutableIntSet mutableInt = listEmbeddingsNodeNumAppears.keySet();

		// Iterate the nodePatternNumEmbeddingsAppear to get the current range
		MutableIntIterator itrCurrent = mutableInt.intIterator();
		while (itrCurrent.hasNext()) {
			int idNode = itrCurrent.next();
			int numberAppears = (int) listEmbeddingsNodeNumAppears.get(idNode);
			if (numberAppears > upperCurrentRange)
				upperCurrentRange = numberAppears;
			if (numberAppears < lowerCurrentRange)
				lowerCurrentRange = numberAppears;
		}

		// Iterate the listEmbeddingsNodeNumAppears to set the new target
		// range
		MutableIntIterator itrTarget = mutableInt.intIterator();
		while (itrTarget.hasNext()) {
			int idNode = itrTarget.next();
			int numberAppears = (int) listEmbeddingsNodeNumAppears.get(idNode);
			double targetDiameter;

			if (lowerCurrentRange == upperCurrentRange) {
				// Means there is not a min/max in the
				// listEmbeddingsNodeNumAppears
				// All have the equal number of appears
				targetDiameter = upperTargetRange;
			} else {
				targetDiameter = PApplet.map((float) numberAppears, (float) lowerCurrentRange,
						(float) upperCurrentRange, lowerTargetRange, upperTargetRange);
			}

			Vertex updateNodePattern = GraphUtil.getNodeById(listNodePattern, idNode);
			if (updateNodePattern != null) {
				updateNodePattern.setDiameter(targetDiameter);
			}
		}
	}

	/**
	 * Change the node position of the listNode in a target range (width and
	 * height)
	 * 
	 * @param widthTarget
	 *            target width range
	 * @param heightTarget
	 *            target height range
	 */
	public void normalizationNodePosition(double widthTarget, double heightTarget, double paddingTop,
			double paddingBottom, double paddingLeft, double paddingRight) {

		// Get the current range
		Map<String, Double> threshold = MathUtil.getPositionThresholds(listNode);
		double xLowerCurrentRange = threshold.get("xMin");
		double xUpperCurrentRange = threshold.get("xMax");
		double yLowerCurrentRange = threshold.get("yMin");
		double yUpperCurrentRange = threshold.get("yMax");

		// Set the target range
//		double xLowerTargetRange = Constants.GRAPH_QUERY_NODE_DIAMETER;
//		double xUpperTargetRange = widthTarget - Constants.GRAPH_QUERY_NODE_DIAMETER;
//		double yLowerTargetRange = Constants.GRAPH_QUERY_NODE_DIAMETER;
//		double yUpperTargetRange = heightTarget - Constants.GRAPH_QUERY_NODE_DIAMETER;

		double xLowerTargetRange = paddingLeft;
		double xUpperTargetRange = widthTarget - paddingRight;
		double yLowerTargetRange = paddingTop;
		double yUpperTargetRange = heightTarget - paddingBottom;

		
		for (Vertex node : listNode) {
			double xTargetPosition = PApplet.map((float) node.getPosition().getX1(), (float) xLowerCurrentRange,
					(float) xUpperCurrentRange, (float) xLowerTargetRange, (float) xUpperTargetRange);
			double yTargetPosition = PApplet.map((float) node.getPosition().getY1(), (float) yLowerCurrentRange,
					(float) yUpperCurrentRange, (float) yLowerTargetRange, (float) yUpperTargetRange);

			node.setPosition(new PositionShape(xTargetPosition, yTargetPosition));
		}
	}

	/**
	 * @return
	 */
	public List<Relationships> getRelationsNodeWithEdge() {
		List<Relationships> results = new ArrayList<Relationships>();

		for (Edge edge : listEdge) {
			Relationships relation = new Relationships(edge.getIdSource(), edge.getIdTarget());
			if (!results.contains(relation)) {
				results.add(relation);
			}
		}

		return results;
	}

	/**
	 * There is a path between every pair of vertices
	 * 
	 * @return True if the graph is connected, false otherwise
	 */
	public boolean isConnected() {

		if (listNode.size() == 0) {
			return false;
		}

		for (Vertex node : listNode) {
			if (getAdjacentEdgesOfNode(node.getId()).size() == 0) {
				return false;
			}
		}
		return true;
	}

	// HYPERGRAPH BEHAIVOR
	/**
	 * Method to return distinct nodes
	 * 
	 * @return a list distinct nodes
	 */
	public List<Vertex> distinctNodes() {
		List<Vertex> result = new ArrayList<Vertex>();
		for (Vertex vertex : listNode) {
			if (!result.contains(vertex)) {
				result.add(vertex);
			}
		}
		return result;
	}

	/**
	 * 
	 * 
	 * 
	 * NODE BEHAIVOR
	 * 
	 * 
	 * 
	 * 
	 */

	public List<Vertex> getListNode() {
		return this.listNode;
	}

	public void setListNode(List<Vertex> listNode) {
		this.listNode = listNode;
	}

	/**
	 * Adding a new node to listNode
	 * 
	 * @param node
	 */
	public void addNode(Vertex node) {
		listNode.add(node);
	}

	/**
	 * Adding a new node to listNode
	 * 
	 * @param node
	 */
	public void addNodePattern(Vertex node) {
		listNodePattern.add(node);
	}

	/**
	 * Deleting a node from listNode
	 * 
	 * @param node
	 * 
	 */
	public void deleteNode(Vertex node) {
		listNode.remove(node);
	}

	/**
	 * Method to verify if ALL node id's are in the listNodePattern
	 * 
	 * @param nodeIds
	 *            List<Integer> of the node id's
	 * @return true if ALL node id's are in the listNodePattern
	 */
	public boolean isAllNodesInListPattern(int[] nodeIds) {
		for (Integer nodeId : nodeIds) {
			Vertex v = GraphUtil.getNodeById(listNodePattern, nodeId);
			if (v == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * 
	 * 
	 * EDGE BEHAIVOR
	 * 
	 * 
	 * 
	 * 
	 */

	public void addDTEdge(Edge edge) {
		if (!listDTEdge.contains(edge)) {
			listDTEdge.add(edge);
		}
	}

	public void addEdge(Edge edge) {
		if (!listEdge.contains(edge)) {
			listEdge.add(edge);
		}
	}

	/**
	 * Delete adjacent edges from a node
	 * 
	 * @param node
	 *            Node to delete all those edges
	 */
	public void deleteAdjacentEdgesOfNode(int nodeId) {
		List<Edge> listEdgesOfNode = getAdjacentEdgesOfNode(nodeId);
		listEdge.removeAll(listEdgesOfNode);
	}

	public void deleteEdge(Edge edge) {
		listEdge.remove(edge);
	}

	public List<Edge> getListEdge() {
		return this.listEdge;
	}

	/**
	 * Method to return the adjacent list of edges of a node
	 * 
	 * @param node
	 *            Node to get the edges
	 * @return List of edges of node
	 */
	public List<Edge> getAdjacentEdgesOfNode(int idNode) {
		List<Edge> result = new ArrayList<Edge>();
		for (Edge edge : listEdge) {
			if (edge.containOneVertex(idNode)) {
				result.add(edge);
			}
		}
		return result;
	}

	/**
	 * Method to return the adjacent list of nodes of a node
	 * 
	 * @param idNode
	 * @return
	 */
	public List<Vertex> getAdjacentNodesOfNode(int idNode) {
		// Get adjacentEdges
		List<Edge> listAdjacentEdges = getAdjacentEdgesOfNode(idNode);

		// Take unique Nodes from the adjacentEdges
		List<Vertex> result = new ArrayList<Vertex>();
		for (Edge edge : listAdjacentEdges) {
			int idAdjNode = edge.adjacentVertex(idNode);
			Vertex adjNode = new Vertex(listNode.get(idAdjNode));
			if (!result.contains(adjNode)) {
				result.add(adjNode);
			}
		}
		return result;
	}

	/**
	 * Method to return a list of edges between two nodes
	 * 
	 * @param source
	 *            id node source
	 * @param target
	 *            id node target
	 * @return the list of edges between source and target
	 */
	public List<Edge> edgesBetweenTwoNodes(int idNodeSource, int idNodeTarget) {
		List<Edge> result = new ArrayList<Edge>();
		for (Edge edge : listEdge) {
			if (edge.containVertices(idNodeSource, idNodeTarget)) {
				result.add(edge);
			}
		}
		return result;
	}

	/**
	 * Method to return THE edge between source and target with a SPECIFIC type
	 * 
	 * @param idNodeSource
	 * @param idNodeTarget
	 * @param edgeType
	 * @return Edge, otherwise null
	 */
	public Edge edgeTypeBetweenTwoNodes(int idNodeSource, int idNodeTarget, int edgeType) {
		for (Edge edge : listEdge) {
			if (edge.containVertices(idNodeSource, idNodeTarget) && edge.getType() == edgeType)
				return edge;
		}
		return null;
	}

	/**
	 * Method to return the adjacent list of edges of a node, grouped by edge
	 * type. The structure is: key:edgeType, value:numberAppears
	 * 
	 * @param idNode
	 */
	public static IntIntHashMap groupAdjacentEdgesByType(List<Edge> adjacentEdgeList) {
		// Grouping the adjacent edge list by type
		IntIntHashMap relationEdgeTypeNumAppears = new IntIntHashMap();
		for (Edge edge : adjacentEdgeList) {
			int edgeType = edge.getType();
			if (!relationEdgeTypeNumAppears.containsKey(edgeType)) {
				relationEdgeTypeNumAppears.put(edgeType, 1);
			} else {
				int numAppears = (int) relationEdgeTypeNumAppears.get(edgeType);
				relationEdgeTypeNumAppears.put(edgeType, numAppears + 1);
			}
		}
		return relationEdgeTypeNumAppears;
	}

	// ---------- VERTEX BEHAVOIR -------------

	/**
	 * 
	 * Method to get a node at specific coordinate
	 * 
	 * @param coordinate
	 * @return
	 */
	public Vertex getNodeByCoordinate(Coordinate coordinate) {
		double x = coordinate.x;
		double y = coordinate.y;

		for (Vertex node : listNode) {
			PositionShape position = node.getPosition();
			double xPosition = position.getX1();
			double yPosition = position.getY1();
			if (xPosition == x && yPosition == y) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Method to return a list of edges For every pair of nodes los K mayores
	 * 
	 * @param edges
	 */
	public static List<Edge> devolverK(List<Edge> edges, int K) {
		List<Edge> result = new ArrayList<Edge>();
		for (Edge edge : edges) {
			if (edge.containOneVertex(edge.getIdSource())) {

			}
		}
		return result;
	}

	/**
	 * @param topK
	 * @return
	 */
	public List<Edge> getTopKInternal(int topK) {
		List<Edge> resultTopK = new ArrayList<Edge>();
		List<Relationships> alreadySeenTmp = new ArrayList<Relationships>();

		for (Edge edge : listEdge) {

			int source = edge.getIdSource();
			int target = edge.getIdTarget();

			// For each Node Pair get the K
			Relationships relation = new Relationships(source, target);
			if (!alreadySeenTmp.contains(relation)) {
				alreadySeenTmp.add(relation);
				// get all edges between source and target
				List<Edge> edgesBetweenTwoNodes = edgesBetweenTwoNodes(source, target);
				// Descending sort by the weight edge
				Collections.sort(edgesBetweenTwoNodes, Collections.reverseOrder(Constants.EDGE_WEIGHT_COMPARATOR));

				// Get the top K
				for (int k = 2; k < topK; k++) {
					try {
						// In case the list does not have enough items
						Edge e = edgesBetweenTwoNodes.get(k);
						resultTopK.add(e);
					} catch (Exception e) {
					}
				}
			}
		}
		return resultTopK;
	}

	/**
	 * Method to get the geometry Points of the listNode
	 * 
	 * @return a Geometry object
	 */
	public Geometry getGeometry() {
		Coordinate[] coordinates = new Coordinate[listNode.size()];
		for (int i = 0; i < listNode.size(); i++) {
			Vertex v = listNode.get(i);
			coordinates[i] = new Coordinate(v.getPosition().getX1(), v.getPosition().getY1());
		}
		Geometry geometry = new GeometryFactory().createMultiPoint(coordinates);

		return geometry;
	}

	/**
	 * 
	 * Print geometry of nodes and edges
	 * 
	 */
	public void printGeometryWithBuffer() {

		logger.info("--------------------------------------");
		for (Vertex node : listNode) {
			Geometry geometryNode = new GeometryFactory()
					.createPoint(new Coordinate(node.getPosition().getX1(), node.getPosition().getY1()))
					.buffer(node.getDiameter() / 2 - 1.5);
			System.out.println(geometryNode);
			// logger.info(geometryNode);
		}

		for (Edge edge : listEdge) {
			Coordinate[] coordinatesEdge = new Coordinate[] {
					new Coordinate(edge.getPosition().getX1(), edge.getPosition().getY1()),
					new Coordinate(edge.getPosition().getX2(), edge.getPosition().getY2()) };
			Geometry geometryEdge = new GeometryFactory().createLineString(coordinatesEdge)
					.buffer(edge.getStroke().getStrokeWeight() / 2 - 2);
			System.out.println(geometryEdge);
			// logger.info(geometryEdge);
		}
	}

	/**
	 * Method to set a diameter to all listNode
	 * 
	 * @param diameter
	 */
	public void setVerticesDiameter(double diameter) {
		for (Vertex v : listNode) {
			v.setDiameter(diameter);
		}
	}

	/**
	 * Method to set a type to all listNode
	 * 
	 * @param type
	 */
	public void setVerticesType(int type) {
		for (Vertex v : listNode) {
			v.setType(type);
		}
	}

	/**
	 * Method to set a opacity value to all listNode
	 * 
	 * @param opacity
	 *            Opacity value
	 */
	public void setVerticesOpacity(double opacity) {
		for (Vertex v : listNode) {
			v.getFill().setFillOpacity(opacity);
		}
	}

	/**
	 * Method to set a opacity value to all listNodePattern
	 * 
	 * @param opacity
	 *            Opacity value
	 */
	public void setVerticesEmbeddingsOpacity(double opacity) {
		for (Vertex v : listNodePattern) {
			v.getFill().setFillOpacity(opacity);
		}
	}

	/**
	 * Method to set a color value to all listNode
	 * 
	 * @param color
	 */
	public void setVerticesColor(int color) {
		for (Vertex v : listNode) {
			v.getFill().setFillColor(color);
		}
	}

	/**
	 * Method to set a color value to all listNodePattern
	 * 
	 * @param color
	 */
	public void setVerticesEmbeddingsColor(int color) {
		for (Vertex v : listNodePattern) {
			v.getFill().setFillColor(color);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMbr() {
		return mbr;
	}

	public void setMbr(Double mbr) {
		this.mbr = mbr;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public double getxView() {
		return xView;
	}

	public void setxView(double xView) {
		this.xView = xView;
	}

	public double getyView() {
		return yView;
	}

	public void setyView(double yView) {
		this.yView = yView;
	}

	// public int getHeatMapRadius() {
	// return heatMapRadius;
	// }

	public int getNumberEmbedding() {
		return numOfEmbeddings;
	}

	public void setNumberEmbedding(int numberEmbedding) {
		this.numOfEmbeddings = numberEmbedding;
	}

	public List<Vertex> getListNodePattern() {
		return listNodePattern;
	}

	public void setListNodePattern(List<Vertex> listNodePattern) {
		this.listNodePattern = listNodePattern;
	}

	public FastList<int[]> getListEmbeddings() {
		return listEmbeddings;
	}

	public void setListEmbeddings(FastList<int[]> listEmbeddings) {
		this.listEmbeddings = listEmbeddings;
	}

	public void setShowEmbeddingLabels(boolean showEmbeddingLabels) {
		this.showEmbeddingLabels = showEmbeddingLabels;
	}

	public void setShowEmbeddingsStroke(boolean showEmbeddingsStroke) {
		this.showEmbeddingStroke = showEmbeddingsStroke;
	}

	public IntIntHashMap getListEmbeddingsNodeNumAppears() {
		return listEmbeddingsNodeNumAppears;
	}

	public int getMaxIdListNode() {
		if (listNode.size() == 0) {
			return 0;
		}
		int max = Integer.MIN_VALUE;
		for (Vertex node : listNode) {
			double id = node.getId();
			max = (int) Math.max(max, id);
		}
		return max + 1;
	}

	@Override
	public int hashCode() {
		double result = 0;
		for (Vertex v : listNode) {
			result = result + Math.pow(v.getId(), 2);
		}
		return (int) result;
	}

	// @Override
	public boolean equals(Object obj) {
		if (obj instanceof Graph) {
			Graph graphCompare = (Graph) obj;
			if (listNode.containsAll(graphCompare.getListNode())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param g
	 * @return
	 */
	public Graph fusion(Graph g) {
		IntObjectHashMap<Edge> listEdgeFusion = new IntObjectHashMap<Edge>();
		if (g instanceof Graph) {
			// edges of current graph
			for (Edge edge : listEdge) {
				if (!listEdgeFusion.containsValue(edge)) {
					int idNodeSource = edge.getIdSource();
					int idNodeTarget = edge.getIdTarget();
					int edgeType = edge.getType();

					Edge edgeFusion = g.edgeTypeBetweenTwoNodes(idNodeSource, idNodeTarget, edgeType);
					if (edgeFusion != null) {
						// Edge exist => fusion
						edge.getStroke().setStrokeWeight(
								edge.getStroke().getStrokeWeight() + edgeFusion.getStroke().getStrokeWeight());
					}
					listEdgeFusion.put(listEdgeFusion.size(), edge);
				}
			}

			// edges of other graph
			for (Edge edge : g.getListEdge()) {
				if (!listEdgeFusion.containsValue(edge)) {
					int idNodeSource = edge.getIdSource();
					int idNodeTarget = edge.getIdTarget();
					int edgeType = edge.getType();

					Edge edgeFusion = edgeTypeBetweenTwoNodes(idNodeSource, idNodeTarget, edgeType);
					if (edgeFusion != null) {
						// Edge exist => fusion
						edge.getStroke().setStrokeWeight(
								edge.getStroke().getStrokeWeight() + edgeFusion.getStroke().getStrokeWeight());
					}
					listEdgeFusion.put(listEdgeFusion.size(), edge);
				}
			}
		}

		List<Edge> listFusion = new ArrayList<Edge>();
		MutableIntSet mutableInt = listEdgeFusion.keySet();
		MutableIntIterator itrCurrent = mutableInt.intIterator();
		while (itrCurrent.hasNext()) {
			int key = itrCurrent.next();
			Edge edge = (Edge) listEdgeFusion.get(key);
			listFusion.add(edge);
		}

		Graph graphFusion = new Graph(g.getListNode(), listFusion);
		graphFusion.setMbr(getMbr());
		return graphFusion;

	}

	/**
	 * Method to set the visible state to all the listNode
	 * 
	 * @param visible
	 */
	public void setListNodeVisible(boolean isVisible) {
		for (Vertex node : listNode) {
			node.setVisible(isVisible);
		}
	}

	/**
	 * Method to sort the listNodePattern by a comparator
	 * 
	 * @param opt
	 *            0: ascending, 1:descending
	 * @param comparator
	 */
	public void sortListNodePattern(int opt, Comparator<Vertex> comparator) {
		if (opt == 0) {
			Collections.sort(getListNodePattern(), comparator);
		} else if (opt == 1) {
			Collections.sort(getListNodePattern(), Collections.reverseOrder(comparator));
		}
	}

	@Override
	public String toString() {
		return "Graph [name=" + name + ", listNode=" + listNode + ", listEdge=" + listEdge + "]";
	}

	public void setShowEmbeddingRectangleLabels(boolean showEmbeddingRectangleLabels) {
		this.showEmbeddingRectangleLabels = showEmbeddingRectangleLabels;
	}

	public List<Edge> getListDTEdge() {
		return listDTEdge;
	}

	public void setListDTEdge(List<Edge> listDTEdge) {
		this.listDTEdge = listDTEdge;
	}

	public void setBackgroundEmbeddingRectangle(int fillColor) {
		Fill backgroundFill = new Fill(backgroundEmbeddingRectangle.isFilled(), fillColor,
				backgroundEmbeddingRectangle.getFillOpacity());
		this.backgroundEmbeddingRectangle = backgroundFill;
	}

	public void setOpacityBackgroundEmbeddingRectangle(double fillOpacity) {
		Fill backgroundFill = new Fill(backgroundEmbeddingRectangle.isFilled(),
				backgroundEmbeddingRectangle.getFillColor(), fillOpacity);
		this.backgroundEmbeddingRectangle = backgroundFill;
	}

	public void setBackgroundEmbeddingPadding(double backgroundEmbeddingPadding) {
		this.backgroundEmbeddingPadding = backgroundEmbeddingPadding;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Vertex> getListEmbeddingLabels() {
		return listEmbeddingLabels;
	}

	public void setListEmbeddingLabels(List<Vertex> listEmbeddingLabels) {
		this.listEmbeddingLabels = listEmbeddingLabels;
	}

}