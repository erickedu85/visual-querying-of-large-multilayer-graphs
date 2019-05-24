package app.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import java.util.Set;

import com.gs.collections.api.iterator.MutableIntIterator;
import com.gs.collections.api.set.primitive.MutableIntSet;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;

import app.graph.structure.Edge;
import app.graph.structure.Graph;
import app.graph.structure.PositionShape;
import app.graph.structure.Vertex;
import app.gui.main.Constants;
import app.mst.EdgeMst;
import app.mst.EdgeWeightedGraph;
import app.mst.LazyPrimMST;
import app.overlap.VerticesOverlap;
import processing.core.PApplet;
import processing.core.PVector;

public class GraphUtil {

	/**
	 * Method to translate position of a point according to a factor (zoom)
	 * 
	 * @param adjacent
	 * @param opposite
	 * @param zoomFactor
	 *            factor
	 * @return PositionShape object
	 */
	public static PositionShape translatePosition(double adjacent, double opposite, double zoomFactor) {
		double hypotenuse = MathUtil.calculateHypotenuse(adjacent, opposite) * zoomFactor;
		double angleRadians = MathUtil.calculateTangent(opposite, adjacent);
		double coordinateY = Math.sin(angleRadians) * hypotenuse;
		double coordinateX = Math.cos(angleRadians) * hypotenuse;

		return new PositionShape(coordinateX, coordinateY);
	}

	/**
	 * Method to verify if 2 circles are near
	 * 
	 * @param xCenter1
	 *            X center coordinate of the circle 1
	 * @param yCenter1
	 *            Y center coordinate of the circle 1
	 * @param xCenter2
	 *            X center coordinate of the circle 2
	 * @param yCenter2
	 *            Y center coordinate of the circle 2
	 * @param tolerance
	 * @return true or false
	 */
	public static boolean isNearCircle(double xCenter1, double yCenter1, double xCenter2, double yCenter2,
			double tolerance) {
		// .DIST calculate the distance between 2 points
		if (PApplet.dist((float) xCenter1, (float) yCenter1, (float) xCenter2, (float) yCenter2) < tolerance) {
			return true;
		}
		return false;
	}

	/**
	 * PVector thePoint the point we will check if it is close to our line.
	 *
	 * PVector theLineEndPoint1 one end of the line.
	 *
	 * PVector theLineEndPoint2 the second end of the line.
	 *
	 * int theTolerance how close thePoint must be to our line to be recogized.
	 */
	public static boolean isNearLine(PVector thePoint, PVector theLineEndPoint1, PVector theLineEndPoint2,
			int theTolerance) {

		PVector dir = new PVector(theLineEndPoint2.x, theLineEndPoint2.y, theLineEndPoint2.z);
		dir.sub(theLineEndPoint1);
		PVector diff = new PVector(thePoint.x, thePoint.y, 0);
		diff.sub(theLineEndPoint1);

		// inside distance determines the weighting
		// between linePoint1 and linePoint2
		float insideDistance = diff.dot(dir) / dir.dot(dir);

		if (insideDistance > 0 && insideDistance < 1) {
			PVector closest = new PVector(theLineEndPoint1.x, theLineEndPoint1.y, theLineEndPoint1.z);
			dir.mult(insideDistance);
			closest.add(dir);
			PVector d = new PVector(thePoint.x, thePoint.y, 0);
			d.sub(closest);
			// println((insideDistance>0.5) ? "b":"a");
			float distsqr = d.dot(d);

			// check the distance of thePoint to the line against our tolerance.
			return (distsqr < Math.pow(theTolerance, 2));
		}
		return false;
	}

	public static double[] ojala(int numberOfEdges, double diameter) {

		double radius = diameter / 2;
		int numTimeDivDiameter = numberOfEdges + 1;
		double diameterDiv = diameter / numTimeDivDiameter;

		double[] result = new double[numberOfEdges];
		for (int i = 0; i < numberOfEdges; i++) {
			double newRadius = radius - (diameterDiv * (i + 1));
			// newRadius = 0 means that edge is in the center
			if (newRadius > 0) {
				result[i] = newRadius;
			}
		}
		return result;
	}

	@Deprecated
	public static double edgeLineFactorDiviser(double diameter, int divider, double x1, double y1, double x2,
			double y2) {
		double factorDiviser = 1.0;
		double opuesto = x2 - x1;
		double adyacente = y2 - y1;
		double alphaAngle = MathUtil.calculateTangent(opuesto, adyacente);
		double radius = diameter / 2;
		double catetoA = radius * (Math.sin(alphaAngle));
		double slope = GeoUtil.slopeBetweenTwoPoints(x1, y1, x2, y2);
		//
		double slopeTangent = (-1 / slope);
		double newCoordinateSourceY = (y1 - catetoA);
		double newCoordinateSourceX = MathUtil.calculateXEquation(slopeTangent, newCoordinateSourceY, x1, y1);
		double newCoordinateSourceY1 = (y1 + catetoA);
		double newCoordinateSourceX1 = MathUtil.calculateXEquation(slopeTangent, newCoordinateSourceY1, x1, y1);

		double a = (newCoordinateSourceX - newCoordinateSourceX1);
		factorDiviser = a / divider;

		return factorDiviser;
	}

	@Deprecated
	public static PositionShape edgePlace(double diameter, double factorDiviser, PositionShape position) {

		double opuesto = position.getX2() - position.getX1();
		double adyacente = position.getY2() - position.getY1();
		double alphaAngle = MathUtil.calculateTangent(opuesto, adyacente);
		double catetoA = (diameter / 2) * (Math.sin(alphaAngle));
		double slope = GeoUtil.slopeBetweenTwoPoints(position.getX1(), position.getY1(), position.getX2(),
				position.getY2());

		// Source
		double newSOURCEY = (position.getY1() - catetoA);
		double newSOURCEX = MathUtil.calculateXEquation(((-1) / slope), (newSOURCEY), position.getX1(),
				position.getY1());

		newSOURCEX = newSOURCEX - factorDiviser;
		newSOURCEY = MathUtil.calculateYEquation(((-1) / slope), newSOURCEX, position.getX1(), position.getY1());

		// Target
		double newTARGETY = (position.getY2() - catetoA);
		double newCoordinateTargetX = MathUtil.calculateXEquation(((-1) / slope), (newTARGETY), position.getX2(),
				position.getY2());

		newCoordinateTargetX = newCoordinateTargetX - factorDiviser;
		newTARGETY = MathUtil.calculateYEquation(((-1) / slope), newCoordinateTargetX, position.getX2(),
				position.getY2());

		return new PositionShape(newSOURCEX, newSOURCEY, newCoordinateTargetX, newTARGETY);
	}

	public static Map<Integer, Integer> minSpanningTreeParserEdges(List<Edge> edges) {
		Map<Integer, Integer> edgeSourceTarget = new HashMap<Integer, Integer>();
		for (Edge edge : edges) {
			if (!edgeSourceTarget.containsKey(edge.getIdSource())) {
				edgeSourceTarget.put(edge.getIdSource(), edgeSourceTarget.size());
			}
			if (!edgeSourceTarget.containsKey(edge.getIdTarget())) {
				edgeSourceTarget.put(edge.getIdTarget(), edgeSourceTarget.size());
			}
		}
		return edgeSourceTarget;
	}

	public static List<EdgeMst> getMinimumSpanningTree(Graph graph) {
		List<Edge> result = new ArrayList<Edge>();
		for (Edge edge : graph.getListEdge()) {
			int idNodeSource = edge.getIdSource();
			int idNodeTarget = edge.getIdTarget();

			Vertex vSource = GraphUtil.getNodeById(graph.getListNode(), idNodeSource);
			Vertex vTarget = GraphUtil.getNodeById(graph.getListNode(), idNodeTarget);

			if (edge.getDistance() == 0) {
				edge.setDistance(GeoAnalytic.euclideanDistanceBetweenTwoPoints(vSource.getPosition().getX1(),
						vSource.getPosition().getY1(), vTarget.getPosition().getX1(), vTarget.getPosition().getY1()));
			}
			result.add(edge);
		}
		Map<Integer, Integer> edgesParseados = minSpanningTreeParserEdges(result);

		// Prim MST
		EdgeWeightedGraph graphEdgeWeigt = new EdgeWeightedGraph(graph.getListNode().size());
		for (Edge edge : graph.getListEdge()) {
			int nodeSource = edgesParseados.get(edge.getIdSource());
			int nodeTarget = edgesParseados.get(edge.getIdTarget());
			graphEdgeWeigt.addEdge(
					new EdgeMst(nodeSource, nodeTarget, edge.getDistance(), edge.getIdSource(), edge.getIdTarget()));
		}

		LazyPrimMST mst = new LazyPrimMST(graphEdgeWeigt);
		List<EdgeMst> mstList = new ArrayList<EdgeMst>();
		for (EdgeMst e : mst.edges()) {
			mstList.add(e);
		}
		return mstList;
	}

	/**
	 * Get the points from a graph where a node intersect with the edges
	 * 
	 * @param graph
	 */
	public static Graph getGraphWithPointsIntersections(Graph graph) {

		// Fill the edges coordinates for the source and target
		for (Edge edge : graph.getListEdge()) {
			PositionShape sourcePosition = graph.getListNode().get(edge.getIdSource()).getPosition();
			PositionShape targetPosition = graph.getListNode().get(edge.getIdTarget()).getPosition();
			edge.setPosition(new PositionShape(sourcePosition, targetPosition));
		}

		// System.out.println("----");
		// g.printGeometryWithBuffer();

		List<Vertex> listNodeFicticios = new ArrayList<Vertex>();

		// Take account the diameter of the nodes
		// Take account the stroke of the edges
		// ----------
		// Loop nodes
		for (Vertex node : graph.getListNode()) {

			// if (node.getId() != 3) {

			// Loop the edges
			for (Edge edge : graph.getListEdge()) {
				// Edge does not have contains the current analyzed node
				if (!edge.containOneVertex(node.getId())) {

					List<Vertex> nodeReference = graph.getAdjacentNodesOfNode(node.getId());
					// As this node can intersect only in one point a this
					// edge
					Vertex ficticioNodeIntersection = JtsUtil.getPointIntersectionNodeToEdge(node, edge,
							nodeReference.get(0));
					if (ficticioNodeIntersection != null) {
						// Add the nodeIntersection to the graph
						// Change the idNode to make ascending ids
						ficticioNodeIntersection.setId(graph.getListNode().size() + listNodeFicticios.size());
						listNodeFicticios.add(ficticioNodeIntersection);
					}
				}
				// }
			}
		}

		// for (Vertex node : graph.getListNode()) {
		// System.out.println(node.getGeometry());
		// }
		//
		// for (Edge edge : graph.getListEdge()) {
		// System.out.println(edge.getGeometry());
		// }
		//
		// //
		// System.out.println("ficticios");
		// for (Vertex vertex : listNodeFicticios) {
		// System.out.println(vertex.getGeometry());
		// }
		// System.out.println("-----------------------------");

		graph.getListNode().addAll(listNodeFicticios);

		return graph;
	}

	/**
	 * 
	 * Get the node for a Node ID below in a any list of nodes
	 * 
	 * @param nodeList
	 *            Any list of nodes
	 * @param nodeId
	 *            nodeId to search
	 * @return Node in the list, otherwise null;
	 */
	public static Vertex getNodeById(List<Vertex> nodeList, int nodeId) {
		for (Vertex node : nodeList) {
			if (node.getId() == nodeId) {
				return node;
			}
		}
		return null;
	}

	/**
	 * @param map
	 * @param getValue
	 * @return return the key of the value, otherwise -1
	 */
	public static int getKeyOfValue(IntObjectHashMap<Graph> map, Graph getValue) {
		MutableIntSet mutableInt = map.keySet();
		MutableIntIterator itrCurrent = mutableInt.intIterator();
		while (itrCurrent.hasNext()) {
			int key = itrCurrent.next();
			Graph value = (Graph) map.get(key);
			if (value.equals(getValue)) {
				return key;
			}
		}
		return -1;
	}

	/**
	 * @param map
	 * @param getValue
	 * @return return the key of the value, otherwise -1
	 */
	public static int getKeyOfValue(IntIntHashMap map, int getValue) {
		MutableIntSet mutableInt = map.keySet();
		MutableIntIterator itrCurrent = mutableInt.intIterator();
		while (itrCurrent.hasNext()) {
			int key = itrCurrent.next();
			int value = (int) map.get(key);
			if (value == getValue) {
				return key;
			}
		}
		return -1;
	}

	/**
	 * Method to get the key of a value in a map
	 * 
	 * @param map
	 * @param value
	 * @return
	 */
	public static Integer getKeyOfValue(Map<Integer, Integer> map, Integer value) {
		Set set = map.entrySet();
		Iterator iter = set.iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (value == entry.getValue()) {
				return (Integer) entry.getKey();
			}
		}
		return null;
	}

	// /**
	// * Method to get a list of nodes without overlap
	// *
	// * @param nodes
	// * Nodes with overlapping
	// * @return A list of Nodes without overlapping
	// */
	// public static List<Vertex> nodesWithoutOverlap(List<Vertex> nodes) {
	// VerticesOverlap overlap = new VerticesOverlap(nodes, 1.1);
	// List<Vertex> nodesWithoutOverlap = overlap.getGraph().getListNode();
	//
	// return nodesWithoutOverlap;
	// }

	/**
	 * Method to get a list of nodes without overlap
	 * 
	 * 
	 * @param g
	 * @param showKelpLines
	 * @param checkOverlap
	 * @param nodes
	 *            Nodes with overlapping
	 * @return A list of Nodes without overlapping
	 */
	public static List<Vertex> nodesWithoutOverlap(Graph g, boolean checkOverlap) {
		VerticesOverlap overlap = new VerticesOverlap(g, Constants.KELP_OVERLAP_MAX_DAMPING_FACTOR, checkOverlap);
		List<Vertex> nodesWithoutOverlap = overlap.getGraph().getListNode();

		return nodesWithoutOverlap;
	}

	/**
	 * Method to sort a List<Edge> edges by weight (number of edges)
	 * 
	 * @param intObjectHashMap
	 * @param option
	 *            0=ascending sort, 1=descending sort
	 * @return LinkedHashMapp<Integer, Integer>
	 */
	public static LinkedHashMap<Integer, Integer> sortExternalK(List<Edge> edges, int option) {

		LinkedHashMap<Integer, Integer> sortedByValue = new LinkedHashMap<Integer, Integer>();

		IntObjectHashMap<Object> edgeGrouped = new IntObjectHashMap<Object>(Constants.VISUAL_GRAPH_QUERY_NUMBER_EDGE_TYPES);
		for (Edge edge : edges) {
			Double weight = edge.getWeight();
			edgeGrouped.put(edge.getType(), weight.intValue());
		}
		LinkedHashMap<Integer, Integer> edgeGroupedSorted = sortIntObjHashMap(edgeGrouped, option);

		int topK = 0;
		for (Map.Entry<Integer, Integer> entry : edgeGroupedSorted.entrySet()) {
			if (topK != Constants.GRAPH_QUERY_EDGE_GHOST_EXTERNAL_TOP_K) {
				topK++;
				sortedByValue.put(entry.getKey(), entry.getValue());
			} else {
				break;
			}
		}

		return sortedByValue;
	}

	/**
	 * Method to sort a IntObjectHashMap<Integer, Integer> by value
	 * 
	 * @param intObjectHashMap
	 * @param option
	 *            0=ascending sort, 1=descending sort
	 * @return LinkedHashMapp<Integer, Integer>
	 */
	public static LinkedHashMap<Integer, Integer> sortIntObjHashMap(IntObjectHashMap<Object> intObjectHashMap,
			int option) {

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		MutableIntSet mutableInt = intObjectHashMap.keySet();
		MutableIntIterator itr = mutableInt.intIterator();
		while (itr.hasNext()) {
			int key = itr.next();
			int value = (Integer) intObjectHashMap.get(key);
			map.put(key, value);
		}

		Set<Entry<Integer, Integer>> entries = map.entrySet();
		List<Entry<Integer, Integer>> listOfEntries = new ArrayList<Entry<Integer, Integer>>(entries);
		Comparator<Entry<Integer, Integer>> valueComparator = new Comparator<Entry<Integer, Integer>>() {
			public int compare(Entry<Integer, Integer> e1, Entry<Integer, Integer> e2) {
				Integer v1 = e1.getValue();
				Integer v2 = e2.getValue();
				return v1.compareTo(v2);
			}
		};
		if (option == 0) {
			// ascending
			Collections.sort(listOfEntries, valueComparator);
		} else if (option == 1) {
			// descending
			Collections.sort(listOfEntries, Collections.reverseOrder(valueComparator));
		}
		LinkedHashMap<Integer, Integer> sortedByValue = new LinkedHashMap<Integer, Integer>(listOfEntries.size());
		// copying entries from List to Map
		for (Entry<Integer, Integer> entry : listOfEntries) {
			sortedByValue.put(entry.getKey(), entry.getValue());
		}
		return sortedByValue;
	}

	/**
	 * Method to substrate elements of the arrayB from the arrayA
	 * 
	 * @param arrayA
	 * @param arrayB
	 * @return a new array of string
	 */
	public static String[] restArray(String[] arrayA, String[] arrayB) {

		List<String> result = new ArrayList<String>();
		for (String elementA : arrayA) {
			boolean found = false;
			for (String elementB : arrayB) {
				if (Integer.valueOf(elementA) == Integer.valueOf(elementB)) {
					found = true;
					break;
				}
			}
			if (!found) {
				result.add(elementA);
			}
		}

		String[] resultArray = new String[result.size()];
		resultArray = result.toArray(resultArray);

		return resultArray;
	}

	/**
	 * Method to get an array of the edge types of a listEdge
	 * 
	 * @param listEdge
	 * @return an array of the edge types
	 */
	public static String[] arrayOfEdgeType(List<Edge> listEdge) {
		String[] arrayEdgeTypes = new String[listEdge.size()];
		for (int i = 0; i < listEdge.size(); i++) {
			arrayEdgeTypes[i] = String.valueOf(listEdge.get(i).getType());
		}
		return arrayEdgeTypes;

	}

}
