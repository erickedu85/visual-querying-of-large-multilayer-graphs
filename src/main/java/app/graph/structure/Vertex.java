package app.graph.structure;

import java.util.Comparator;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import app.gui.main.Constants;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class Vertex extends Shape {

	public static Logger logger = Logger.getLogger(Vertex.class);
	public static PApplet parent;
	private double diameter;
	private double weight; // number of publication in DBLP
	private int attachedPin = -1; // -1 by default

	/**
	 * @param id
	 * @param label
	 * @param type
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param diameter
	 */
	public Vertex(int id, String label, int type, PositionShape position, Stroke stroke, Fill fill, boolean isLabelled,
			boolean isVisible, double diameter) {
		super(id, label, type, position, stroke, fill, isLabelled, isVisible);
		this.diameter = diameter;
	}

	/**
	 * @param id
	 * @param label
	 * @param type
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isLabelled
	 * @param diameter
	 * @param weight
	 */
	public Vertex(int id, String label, int type, PositionShape position, Stroke stroke, Fill fill, boolean isLabelled,
			boolean isVisible, double diameter, double weight) {
		super(id, label, type, position, stroke, fill, isLabelled, isVisible);
		this.diameter = diameter;
		this.weight = weight;
	}

	/**
	 * Method to create a vertex from another vertex
	 * 
	 * @param v
	 *            Vertex
	 */
	public Vertex(Vertex v) {
		super(v.getId(), v.getLabel(), v.getType(), new PositionShape(v.getPosition()), new Stroke(v.getStroke()),
				new Fill(v.getFill()), v.isLabelled(), v.isVisible());
		this.diameter = v.getDiameter();
		this.weight = v.getWeight();
	}

	/**
	 * @param startAngle
	 * @param strokeWeight
	 * @param diameter
	 */
	public void displayArc(double startAngle) {
		parent.noFill();
		parent.stroke(Constants.GRAPH_DB_NODE_ARC_STROKE.getStrokeColor());
		parent.strokeWeight((float) Constants.GRAPH_DB_NODE_ARC_STROKE.getStrokeWeight());
		parent.arc((float) getPosition().getX1(), (float) getPosition().getY1(),
				(float) Constants.GRAPH_DB_NODE_ARC_DIAMETER, (float) Constants.GRAPH_DB_NODE_ARC_DIAMETER,
				(float) startAngle, (float) (Math.PI + startAngle));
	}

	// https://processing.org/tutorials/drawing/
	// Ellipse width and height equals to diameter because the
	// ellipseMode(Center)
	/**
	 * Method to display a vertex according to these attributes
	 */
	public void display() {
		if (isVisible()) {
			if (getFill().isFilled()) {
				parent.fill(getFill().getFillColor(), (float) getFill().getFillOpacity());
			} else {
				parent.noFill();
			}

			if (getStroke().isStroked()) {
				parent.stroke(getStroke().getStrokeColor(), (float) getStroke().getStrokeOpacity());
				parent.strokeWeight((float) getStroke().getStrokeWeight());
			} else {
				parent.noStroke();
			}
			parent.ellipse((float) getPosition().getX1(), (float) getPosition().getY1(), (float) diameter,
					(float) diameter);
		}

	}

	public void displayLabel(Double textHeight, boolean showRectangle, Fill fillBackgroundRectangle, double padding) {
		if (isVisible()) {

			parent.textSize(textHeight.floatValue());

			float x1 = (float) (getPosition().getX1());
			float y1 = (float) (getPosition().getY1());

			// Show rectangle border of labels
			if (showRectangle) {
				// radius value for all four corners
				double radiusBorder = 6.0d;
				Double textWidth = (double) parent.textWidth(getLabel());
				parent.fill(fillBackgroundRectangle.getFillColor(), (float) fillBackgroundRectangle.getFillOpacity());
				parent.noStroke();
				parent.rectMode(PConstants.CENTER);
				parent.rect(x1, y1, (float) (textWidth.floatValue() + padding),
						(float) (textHeight.floatValue() + padding), (float) radiusBorder);
			}

			// Show label
			parent.fill(ColorShape.getHSB_Black());
			parent.textAlign(PConstants.CENTER, PConstants.CENTER);
			parent.text(getLabel(), x1, y1);
		}
	}

	public void display(PGraphics pg) {

		if (isVisible()) {

			if (getFill().isFilled()) {
				pg.fill(getFill().getFillColor(), (float) getFill().getFillOpacity());
			} else {
				pg.noFill();
			}

			if (getStroke().isStroked()) {
				pg.stroke(getStroke().getStrokeColor(), (float) getStroke().getStrokeOpacity());
				pg.strokeWeight((float) getStroke().getStrokeWeight());
			} else {
				pg.noStroke();
			}

			pg.ellipse((float) getPosition().getX1(), (float) getPosition().getY1(), (float) diameter,
					(float) diameter);

			if (isLabelled()) {
				pg.textSize((float) (diameter / 2));
				pg.fill(ColorShape.getHSB_Black());
				pg.textAlign(PConstants.CENTER, PConstants.CENTER);
				pg.text(getId(), (float) getPosition().getX1(), (float) getPosition().getY1());
			}
		}

	}
	
	public Geometry getGeometry() {
		Geometry geometry = new GeometryFactory()
				.createPoint(new Coordinate(getPosition().getX1(), getPosition().getY1()))
				.buffer(getDiameter() / 2 - 1.5); //1.5
		return geometry;
	}

	public double getDiameter() {
		return diameter;
	}

	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}

	public double getRadius() {
		return (diameter / 2);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof Vertex) {
			Vertex element = (Vertex) obj;
			if (element.getId() == this.getId()) {
				return true;
			}
		}
		return false;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getAttachedPin() {
		return attachedPin;
	}

	public void setAttachedPin(int attachedPin) {
		this.attachedPin = attachedPin;
	}

	@Override
	public String toString() {
		return "Vertex [getId()=" + getId() + ", getLabel()=" + getLabel() + ", getDiameter()=" + getDiameter() + "]";
	}

}
