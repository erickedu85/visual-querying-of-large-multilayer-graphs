package app.graph.structure;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import app.gui.main.Constants;
import processing.core.PApplet;

public class Hyperedge {

	public static Logger logger = Logger.getLogger(Hyperedge.class);
	public static PApplet parent;
	// (NO ORDERED)
	private List<Vertex> listNode;
	// for displaying lines of edges
	private List<Edge> listEdge;
	private int orderToDraw = 0;

	public Hyperedge() {
		listNode = new CopyOnWriteArrayList<Vertex>();
		listEdge = new CopyOnWriteArrayList<Edge>();
	}

	/**
	 * Add a node if the list of nodes does not contains
	 * 
	 * @param node
	 */
	public void addNode(Vertex node) {
		if (!listNode.contains(node)) {
			listNode.add(new Vertex(node));
		}
	}

	/**
	 * Add an edge
	 * 
	 * @param edge
	 */
	public void addEdge(Edge edge) {
		listEdge.add(new Edge(edge));
	}

	/**
	 * Method to display the edges
	 */
	public void displayEdges() {
		for (Edge edge : listEdge) {
			if (edge.getStroke().isStroked()) {
				edge.setVisible(true);
				edge.display();
			}
		}
	}

	/**
	 * Method to display the nodes
	 */
	public void displayNodes() {
		for (Vertex node : listNode) {
			node.setVisible(true);
			node.getStroke().setStroked(false);
			node.display();
		}
	}

	public int getOrderToDraw() {
		return orderToDraw;
	}

	public void setOrderToDraw(int orderToDraw) {
		this.orderToDraw = orderToDraw;
	}

	public List<Vertex> getListNode() {
		return listNode;
	}

	public void setListNode(List<Vertex> listNode) {
		this.listNode = listNode;
	}

	public List<Edge> getListEdge() {
		return listEdge;
	}

	public void setListEdge(List<Edge> listEdge) {
		this.listEdge = listEdge;
	}

}
