package app.graph.structure;

import java.util.List;

public class EdgeFusion {

	private List<Edge> listEdge;
	private int numEdgeFusion;


	public EdgeFusion(List<Edge> listEdge, int numEdgeFusion) {
		super();
		this.listEdge = listEdge;
		this.numEdgeFusion = numEdgeFusion;
	}

	public List<Edge> getListEdge() {
		return listEdge;
	}

	public void setListEdge(List<Edge> listEdge) {
		this.listEdge = listEdge;
	}

	public int getNumEdgeFusion() {
		return numEdgeFusion;
	}

	public void setNumEdgeFusion(int numEdgeFusion) {
		this.numEdgeFusion = numEdgeFusion;
	}

}
