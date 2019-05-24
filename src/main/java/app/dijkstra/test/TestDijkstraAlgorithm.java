package app.dijkstra.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import app.dijkstra.Dijkstra;
import app.graph.structure.Edge;
import app.graph.structure.Graph;
import app.graph.structure.Vertex;

public class TestDijkstraAlgorithm {
	private List<Vertex> nodes;
	private List<Edge> edges;

	@Test
	public void testExcute() {

		// Example from :
		// https://fr.wikipedia.org/wiki/Algorithme_de_Dijkstra
		nodes = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();

		// Adding Vertex
		nodes.add(new Vertex(0, "A", 66, null, null, null, false, true, 20.0));
		nodes.add(new Vertex(1, "B", 66, null, null, null, false, true, 20.0));
		nodes.add(new Vertex(2, "C", 66, null, null, null, false, true, 20.0));
		nodes.add(new Vertex(3, "D", 66, null, null, null, false, true, 20.0));
		nodes.add(new Vertex(4, "E", 66, null, null, null, false, true, 20.0));
		nodes.add(new Vertex(5, "F", 66, null, null, null, false, true, 20.0));
		nodes.add(new Vertex(6, "G", 66, null, null, null, false, true, 20.0));
		nodes.add(new Vertex(7, "H", 66, null, null, null, false, true, 20.0));
		nodes.add(new Vertex(8, "I", 66, null, null, null, false, true, 20.0));
		nodes.add(new Vertex(9, "J", 66, null, null, null, false, true, 20.0));

		// Adding edges
		addLane(0, 1, 0, 85);
		addLane(1, 2, 0, 217);
		addLane(2, 4, 0, 173);
		addLane(3, 1, 5, 80);
		addLane(4, 6, 2, 186);
		addLane(5, 7, 2, 103);
		addLane(6, 7, 3, 183);
		addLane(7, 8, 5, 250);
		addLane(8, 9, 8, 84);
		addLane(9, 9, 7, 167);
		addLane(10, 9, 4, 502);

		Graph graph = new Graph(nodes, edges);
		Dijkstra dijkstra = new Dijkstra(graph);

		System.out.println(dijkstra.getVertexPath(nodes.get(0), nodes.get(9)));

	}

	private void addLane(int id, int vertexSource, int vertexTarget, double distance) {
		Edge lane = new Edge(id, "Edge", 0, null, null, null, false, false, vertexSource, vertexTarget, distance);
		edges.add(lane);
	}
}
