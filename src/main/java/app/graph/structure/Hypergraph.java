package app.graph.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import app.gui.database.GraphDBView;
import app.gui.main.Constants;
import app.utils.GraphUtil;
import processing.core.PApplet;

public class Hypergraph {

	public static PApplet parent;
	public static Logger logger = Logger.getLogger(Hypergraph.class);

	// List nodes of the hypergraph ORDER
	private List<Vertex> listNode;

	// List hyperedge
	private List<Hyperedge> listHyperedge;
	private double zoom = 1.0d;

	private double textSize = Constants.KELP_TEXT_SIZE;
	private Fill backgroundText = Constants.KELP_TEXT_BACKGROUND_FILL;
	private double backgroundTextPadding = Constants.KELP_TEXT_BACKGROUND_PADDING;

	public Hypergraph() {
		this.listNode = new CopyOnWriteArrayList<Vertex>();
		this.listHyperedge = new CopyOnWriteArrayList<Hyperedge>();
	}

	public void addNode(Vertex node) {
		if (!listNode.contains(node)) {
			listNode.add(new Vertex(node));
		}
	}

	private double getMaxDiameterByNode(Vertex node) {
		double maxDiameter = -Float.MAX_VALUE;
		for (Hyperedge hyperedge : listHyperedge) {
			for (Vertex n : hyperedge.getListNode()) {
				if (n.equals(node)) {
					maxDiameter = Math.max(maxDiameter, n.getDiameter());
				}
			}
		}
		return maxDiameter;
	}

	private double getMaxStrokeByEdge(Edge edge) {
		double maxStroke = -Float.MAX_VALUE;
		for (Hyperedge hyperedge : listHyperedge) {
			for (Edge e : hyperedge.getListEdge()) {
				if (e.containVertices(edge.getIdSource(), edge.getIdTarget())) {
					maxStroke = Math.max(maxStroke, e.getStroke().getStrokeWeight());
				}
			}
		}
		return maxStroke;
	}

	/**
	 * Method to get a new graph topology from the Hypergraph withe the max
	 * radius and stroke for nodes and edges
	 */
	private Graph getGraphTopologyByHyperedges() {
		Graph g = new Graph();

		for (Vertex node : listNode) {
			node.setDiameter(getMaxDiameterByNode(node));
			g.addNode(new Vertex(node));
		}

		for (Hyperedge hyperedge : listHyperedge) {
			for (Edge edge : hyperedge.getListEdge()) {
				if (!g.getListEdge().contains(edge)) {
					edge.getStroke().setStrokeWeight(getMaxStrokeByEdge(edge));
					g.addEdge(new Edge(edge));
				}
			}
		}

		return g;
	}

	public void displayHypers(boolean showKelpLines, boolean kelpOverlapLines) {

		// logger.info("COMIENZA DISPLAY FUNCION KELPS");

		// Save the node original positions before zoom
		List<Vertex> listNodePositionsBeforeZoom = new CopyOnWriteArrayList<Vertex>();
		for (Vertex vertex : listNode) {
			listNodePositionsBeforeZoom.add(new Vertex(vertex));
		}

		// Performs a Zoom on the listNode Position
		for (Vertex vertex : listNode) {
			PositionShape newCoordinates = GraphUtil.translatePosition(vertex.getPosition().getX1(),
					vertex.getPosition().getY1(), zoom);
			vertex.setPosition(newCoordinates);
		}

		// After performs a zoom we get the graph topology from the Hypergraph
		// with the max radius in Nodes and max stroke in Edges
		Graph graphTopo = getGraphTopologyByHyperedges();

//		//
//		if (kelpOverlapLines && showKelpLines) {
//			//graphTopo = GraphUtil.getGraphWithPointsIntersections(graphTopo);
//		}
//		//

		List<Vertex> nodesWithoutOverlap = GraphUtil.nodesWithoutOverlap(graphTopo, kelpOverlapLines);

		// Setting the position without overlap to the nodes and edges in
		// hyperedge
		for (Hyperedge hyperedge : listHyperedge) {
			for (Vertex vertex : hyperedge.getListNode()) {
				vertex.setPosition(nodesWithoutOverlap.get(vertex.getId()).getPosition());
			}
			for (Edge edge : hyperedge.getListEdge()) {
				PositionShape source = nodesWithoutOverlap.get(edge.getIdSource()).getPosition();
				PositionShape target = nodesWithoutOverlap.get(edge.getIdTarget()).getPosition();
				edge.setPosition(new PositionShape(source, target));
			}
		}

		// Display every HyperEdge
		for (Hyperedge hyperedge : listHyperedge) {
			if (showKelpLines) {
				hyperedge.displayEdges();
			}
			hyperedge.displayNodes();
		}

		// Display a little vertex at the top of unique node in the listHyperede
		for (Vertex node : getUniqueVertex()) {
			Vertex littleNode = new Vertex(node);
			// littleNode.setDiameter(10);
			// littleNode.setFill(Constants.KELP_NODE_FILL);
			// littleNode.display();
			littleNode.displayLabel(textSize, true, backgroundText, backgroundTextPadding);
		}

		// Restoring the original coordinates
		for (Vertex vertex : listNode) {
			vertex.setPosition(listNodePositionsBeforeZoom.get(vertex.getId()).getPosition());
		}

	}

	/**
	 * @param showKelpLines
	 */
//	public void displayHypersWorks(boolean showKelpLines, boolean kelpOverlapLines) {
//
//		// Getting Graph topology to run overlap
//		Graph graphTopology = new Graph();
//
//		// Node positions before zoom
//		List<Vertex> listNodePositionsBeforeZoom = new CopyOnWriteArrayList<Vertex>();
//
//		// In the sort listHyperEdge adding the Nodes and Edge in graph Topology
//		for (Hyperedge hyperedge : listHyperedge) {
//			for (Vertex vertex : hyperedge.getListNode()) {
//				if (!graphTopology.getListNode().contains(vertex)) {
//					graphTopology.getListNode().add(new Vertex(vertex));
//					// Saving coordinates before zoom and after changing
//					// coordinates by zoom
//					listNodePositionsBeforeZoom.add(new Vertex(vertex));
//				}
//			}
//			for (Edge edge : hyperedge.getListEdge()) {
//				if (!graphTopology.getListEdge().contains(edge)) {
//					graphTopology.getListEdge().add(new Edge(edge));
//				}
//			}
//		}
//
//		// Applying the zoom and translate position
//		for (Vertex vertex : graphTopology.getListNode()) {
//			PositionShape newCoordinates = GraphUtil.translatePosition(vertex.getPosition().getX1(),
//					vertex.getPosition().getY1(), zoom);
//			vertex.setPosition(newCoordinates);
//		}
//
//		if (kelpOverlapLines) {
//			// Aqui tomar abstraction after changing the zoom
//			// graphTopology =
//			// GraphUtil.getGraphWithPointsIntersections(graphTopology);
//		}
//
//		// Getting new coordinates without overlapping
//		List<Vertex> nodesWithoutOverlap = GraphUtil.nodesWithoutOverlap(graphTopology);
//		// List<Vertex> nodesWithoutOverlap =
//		// GraphUtil.nodesWithoutOverlap(graphTopology.getListNode());
//
//		// Setting the new Nodes coordinates from the list of nodes without
//		// overlap
//		for (Hyperedge hyperedge : listHyperedge) {
//			// Setting the new Nodes coordinates
//			for (Vertex vertex : hyperedge.getListNode()) {
//				try {
//					vertex.setPosition(new PositionShape(nodesWithoutOverlap.get(vertex.getId()).getPosition()));
//				} catch (Exception e) {
//					logger.info(
//							"ERROR setting nodes without overlap: " + e.getMessage() + " not found " + vertex.getId());
//				}
//			}
//
//			// Setting the new Edges coordinates
//			for (Edge edge : hyperedge.getListEdge()) {
//				try {
//					PositionShape nodeSourcePosition = nodesWithoutOverlap.get(edge.getIdSource()).getPosition();
//					PositionShape nodeTargetPosition = nodesWithoutOverlap.get(edge.getIdTarget()).getPosition();
//
//					edge.setPosition(new PositionShape(nodeSourcePosition, nodeTargetPosition));
//				} catch (Exception e) {
//					logger.info("ERROR setting edge without overlap: " + e.getMessage() + " not found "
//							+ edge.getIdSource());
//				}
//			}
//
//		}
//
//		// Display every hyperedge
//		for (Hyperedge hyperedge : listHyperedge) {
//			if (showKelpLines) {
//				hyperedge.displayEdges();
//			}
//			hyperedge.displayNodes();
//		}
//
//		// Display a little vertex at the top of unique node in the listHyperede
//		// for (Vertex node : getUniqueVertex()) {
//		// Vertex littleNode = new Vertex(node);
//		// littleNode.setDiameter(0);
//		// littleNode.setFill(Constants.KELP_NODE_FILL);
//		// littleNode.display();
//		// littleNode.displayLabel(16.0, true,
//		// Constants.GRAPH_DB_NODE_PATTERN_RECT_BACKGROUND_FILL, 1);
//		// }
//
//		// Restoring the original coordinates
//		for (Hyperedge hyperedge : listHyperedge) {
//			for (Vertex vertex : hyperedge.getListNode()) {
//				try {
//					vertex.setPosition(
//							new PositionShape(listNodePositionsBeforeZoom.get(vertex.getId()).getPosition()));
//				} catch (Exception e) {
//					logger.info(
//							"ERROR restoring the node position: " + e.getMessage() + " not found " + vertex.getId());
//				}
//			}
//		}
//
//	}

	/**
	 * The number of time a Vertex appears in all the hyperedges of the
	 * hypergraph que han sido anadidos hasta ese momento
	 * 
	 * @param vertex
	 * @return the number the
	 */
	public int numAppearsANodeInHyperedges(Vertex vertex) {
		int nodeId = vertex.getId();
		int numAppears = 0;
		for (Hyperedge hyperedge : listHyperedge) {
			for (Vertex node : hyperedge.getListNode()) {
				if (node.getId() == nodeId) {
					numAppears++;
				}
			}
		}
		return numAppears;
	}

	/**
	 * The number of time an edge appears in all the hyperedges of the
	 * hypergraph
	 * 
	 * @param edge
	 * @return
	 */
	public int numAppearsAnEdgeInHyperedges(Edge edge) {
		int nodeSourceId = edge.getIdSource();
		int nodeTargetId = edge.getIdTarget();
		int numAppears = 0;
		for (Hyperedge hyperedge : listHyperedge) {
			for (Edge e : hyperedge.getListEdge()) {
				if (e.containVertices(nodeSourceId, nodeTargetId)) {
					numAppears++;
				}
			}
		}
		return numAppears;
	}

	/**
	 * 
	 * Method to get the uniques vertex in the hypergraph
	 * 
	 * @return
	 */
	public List<Vertex> getUniqueVertex() {
		List<Vertex> results = new ArrayList<Vertex>();
		for (Hyperedge hyperedge : listHyperedge) {
			for (Vertex node : hyperedge.getListNode()) {
				if (!results.contains(node)) {
					results.add(node);
				}
			}
		}
		return results;
	}

	/**
	 * 
	 */
	public void abstractionBloquedVertex(Graph gAbrastraction) {

		Graph abstractGraphIntersection = GraphUtil.getGraphWithPointsIntersections(gAbrastraction);

	}

	public void addHyperedge(Hyperedge hyperedge) {
		listHyperedge.add(hyperedge);
	}

	public List<Hyperedge> getListHyperedge() {
		return listHyperedge;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public List<Vertex> getListNode() {
		return listNode;
	}

	public void setListNode(List<Vertex> listNode) {
		this.listNode = listNode;
	}

	public void setTextSize(double textSize) {
		this.textSize = textSize;
	}

	public void setBackgroundTextPadding(double backgroundTextPadding) {
		this.backgroundTextPadding = backgroundTextPadding;
	}

	public void setBackgroundText(int fillColor) {
		Fill backgroundFill = new Fill(backgroundText.isFilled(), fillColor, backgroundText.getFillOpacity());
		this.backgroundText = backgroundFill;
	}

	public void setOpacityBackgroundTextPadding(double fillOpacity) {
		Fill backgroundFill = new Fill(backgroundText.isFilled(), backgroundText.getFillColor(), fillOpacity);
		this.backgroundText = backgroundFill;
	}

}
