package app.graph.structure;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import app.utils.GeoAnalytic;
import app.utils.GeoUtil;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * @author Erick Cuenca
 *
 */
public class Edge extends Shape {
	public static Logger logger = Logger.getLogger(Edge.class);
	public static PApplet parent;
	private int idSource;
	private int idTarget;
	private double distance = 0.0d;
	private double numberIntersection = 0.0d;
	private double angle = 0.0d;
	private double weight = 1.0d;
	private double overlapFactor = 0.0d; // overlap behavior
	private double desiredDistance = 0.0d; // overlap behavior
	private int stateGhost;

	/**
	 * @param id
	 * @param label
	 * @param type
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isLabelled
	 * @param isVisible
	 * @param source
	 * @param target
	 * @param distance
	 * @param weight
	 * @param stateGhost
	 */
	public Edge(int id, String label, int type, PositionShape position, Stroke stroke, Fill fill, boolean isLabelled,
			boolean isVisible, int source, int target, double distance, double weight, int stateGhost) {
		super(id, label, type, position, stroke, fill, isLabelled, isVisible);
		this.idSource = source;
		this.idTarget = target;
		this.distance = distance;
		this.weight = weight;
		this.stateGhost = stateGhost;
	}

	/**
	 * @param id
	 * @param label
	 * @param type
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isLabelled
	 * @param isVisible
	 * @param source
	 * @param target
	 * @param distance
	 */
	public Edge(int id, String label, int type, PositionShape position, Stroke stroke, Fill fill, boolean isLabelled,
			boolean isVisible, int source, int target, double distance) {
		super(id, label, type, position, stroke, fill, isLabelled, isVisible);
		this.idSource = source;
		this.idTarget = target;
		this.distance = distance;
	}

	public Edge(Edge e) {
		super(e.getId(), e.getLabel(), e.getType(), e.getPosition(), new Stroke(e.getStroke()), new Fill(e.getFill()),
				e.isLabelled(), e.isVisible());
		this.idSource = e.getIdSource();
		this.idTarget = e.getIdTarget();
		this.distance = e.getDistance();
		this.numberIntersection = e.getNumberIntersection();
		this.angle = e.getAngle();
		this.weight = e.getWeight();
		this.desiredDistance = e.getDesiredDistance();
		this.stateGhost = e.getStateGhost();
	}

	public void display() {
		if (isVisible()) {
			// THERE IS NOT A FILL IN A LINE IN PROCESSING
			if (getStroke().isStroked()) {
				parent.stroke(getStroke().getStrokeColor(), (float) getStroke().getStrokeOpacity());
				parent.strokeWeight((float) getStroke().getStrokeWeight());
			} else {
				parent.noStroke();
			}

			parent.line((float) getPosition().getX1(), (float) getPosition().getY1(), (float) getPosition().getX2(),
					(float) getPosition().getY2());
		}
	}

//	public void display(PGraphics pg) {
//		if (isVisible()) {
//			// THERE IS NOT A FILL IN A LINE IN PROCESSING
//			if (getStroke().isStroked()) {
//				pg.stroke(getStroke().getStrokeColor(), (float) getStroke().getStrokeOpacity());
//				pg.strokeWeight((float) getStroke().getStrokeWeight());
//			} else {
//				pg.noStroke();
//			}
//
//			pg.line((float) getPosition().getX1(), (float) getPosition().getY1(), (float) getPosition().getX2(),
//					(float) getPosition().getY2());
//
//			if (isLabelled()) {
//				pg.textSize(15);
//				pg.fill(ColorShape.getHSB_Black());
//				pg.textAlign(PConstants.CENTER, PConstants.CENTER);
//
//				double labelPositionX = (getPosition().getX1() + getPosition().getX2()) / 2;
//				double labelPositionY = (getPosition().getY1() + getPosition().getY2()) / 2;
//				pg.text("E" + getType(), (float) labelPositionX, (float) labelPositionY);
//			}
//		}
//	}
	
	public Geometry getGeometry() {
		Coordinate[] coordinatesEdge = new Coordinate[] {
				new Coordinate(getPosition().getX1(), getPosition().getY1()),
				new Coordinate(getPosition().getX2(), getPosition().getY2()) };
		Geometry geometry = new GeometryFactory().createLineString(coordinatesEdge)
				.buffer(getStroke().getStrokeWeight() / 2 - 1.5); //2
		return geometry;
	}

	/**
	 * Method to know if the edge contains the BOTH idVertex1 and idVertex2 as
	 * source OR target
	 * 
	 * @param idVertex1
	 *            Vertex 1
	 * @param idVertex2
	 *            Vertex 2
	 * @return True if the edge contains the both V1 and V2 as source or target
	 */
	public boolean containVertices(int idVertex1, int idVertex2) {
		if (containOneVertex(idVertex1) && containOneVertex(idVertex2)) {
			return true;
		}
		return false;
	}

	/**
	 * Method to know if the edge contains AT LEAST Vertex1
	 * 
	 * @param idVertex
	 * @return True if the edge contains at least this vertex
	 */
	public boolean containOneVertex(int idVertex) {
		if ((idSource == idVertex) || (idTarget == idVertex)) {
			return true;
		}
		return false;
	}

	/**
	 * Method to get the adjacent vertex of the idVertex
	 * 
	 * @param idVertex
	 * @return Adjacent vertex id
	 */
	public int adjacentVertex(int idVertex) {
		if (idSource == idVertex) {
			return idTarget;
		} else {
			return idSource;
		}
	}

	public double getEuclideanDistance() {
		return GeoAnalytic.euclideanDistanceBetweenTwoPoints(getPosition().getX1(), getPosition().getY1(),
				getPosition().getX2(), getPosition().getY2());
	}

	public int getIdSource() {
		return idSource;
	}

	public void setIdSource(int idSource) {
		this.idSource = idSource;
	}

	public int getIdTarget() {
		return idTarget;
	}

	public void setIdTarget(int idTarget) {
		this.idTarget = idTarget;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getNumberIntersection() {
		return numberIntersection;
	}

	public void setNumberIntersection(double numberIntersection) {
		this.numberIntersection = numberIntersection;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getOverlapFactor() {
		return overlapFactor;
	}

	public void setOverlapFactor(double overlapFactor) {
		this.overlapFactor = overlapFactor;
	}

	public double getDesiredDistance() {
		return desiredDistance;
	}

	public void setDesiredDistance(double desiredDistance) {
		this.desiredDistance = desiredDistance;
	}

	public int getStateGhost() {
		return stateGhost;
	}

	public void setStateGhost(int stateGhost) {
		this.stateGhost = stateGhost;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (!this.containVertices(other.idSource, other.idTarget))
			return false;
		if (getType() != other.getType()) // Type
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Edge [source=" + idSource + ", target=" + idTarget + ", type=" + getType() + ", stroke="
				+ getStroke().getStrokeWeight() + "]";
	}

}
