package app.gui.histogram;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.PositionShape;
import app.graph.structure.Shape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;

public class HistogramItem extends Shape {

	public static Logger logger = Logger.getLogger(HistogramItem.class);
	private List<Graph> listGraph;
	private double width;
	private double height;
	private double intervalBegin;
	private double intervalEnd;
	private boolean isSelected;

	/**
	 * @param id
	 * @param label
	 * @param type
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isLabelled
	 * @param listGraph
	 * @param width
	 * @param height
	 * @param intervalBegin
	 * @param intervalEnd
	 * @param isSelected
	 */
	public HistogramItem(int id, String label, int type, PositionShape position, Stroke stroke, Fill fill,
			boolean isLabelled, boolean isVisible, List<Graph> listGraph, double width, double height,
			double intervalBegin, double intervalEnd, boolean isSelected) {
		super(id, label, type, position, stroke, fill, isLabelled, isVisible);
		this.listGraph = listGraph;
		this.width = width;
		this.height = height;
		this.intervalBegin = intervalBegin;
		this.intervalEnd = intervalEnd;
		this.isSelected = isSelected;
	}

	public List<Vertex> getAllVertexOfListGraph() {
		List<Vertex> result = new ArrayList<Vertex>();
		for (Graph g : listGraph) {
			result.addAll(g.getListNode());
		}
		return result;
	}

	public List<Graph> getListGraph() {
		return listGraph;
	}

	public void setListGraph(List<Graph> listGraph) {
		this.listGraph = listGraph;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getIntervalBegin() {
		return intervalBegin;
	}

	public void setIntervalBegin(double intervalBegin) {
		this.intervalBegin = intervalBegin;
	}

	public double getIntervalEnd() {
		return intervalEnd;
	}

	public void setIntervalEnd(double intervalEnd) {
		this.intervalEnd = intervalEnd;
	}

	@Override
	public String toString() {
		return "HistogramItem [getId()=" + getId() + ", intervalBegin=" + intervalBegin + ", intervalEnd=" + intervalEnd
				+ "]";
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
