package app.gui.ghost;

import app.gui.query.EdgeType;

public class GhostSliceItem {

	private int idNodeParent;
	private EdgeType edgeType;
	private int numAppears;
	private double angleAccumulate;

	public GhostSliceItem(int idNodeParent, EdgeType edgeType, int numAppears) {
		super();
		this.idNodeParent = idNodeParent;
		this.edgeType = edgeType;
		this.numAppears = numAppears;
	}

	public EdgeType getEdgeType() {
		return edgeType;
	}

	public void setEdgeType(EdgeType edgeType) {
		this.edgeType = edgeType;
	}

	public int getNumAppears() {
		return numAppears;
	}

	public void setNumAppears(int numAppears) {
		this.numAppears = numAppears;
	}

	public int getIdNodeParent() {
		return idNodeParent;
	}

	public void setIdNodeParent(int idNodeParent) {
		this.idNodeParent = idNodeParent;
	}

	@Override
	public String toString() {
		return "GhostSliceItem [idNodeParent=" + idNodeParent + ", edgeType=" + edgeType + ", numAppears=" + numAppears
				+ "]";
	}

	public double getAngleAccumulate() {
		return angleAccumulate;
	}

	public void setAngleAccumulate(double angleAccumulate) {
		this.angleAccumulate = angleAccumulate;
	}

}
