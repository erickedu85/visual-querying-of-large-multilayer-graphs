package app.graph.structure;

public abstract class Shape {

	private int id;
	private String label = "";
	private int type;
	private PositionShape position;
	private Stroke stroke;
	private Fill fill;
	private boolean isLabelled = false;
	private boolean isVisible = true;

	public Shape(int id, String label, int type, PositionShape position, Stroke stroke, Fill fill, boolean isLabelled, boolean isVisible) {
		this.id = id;
		this.label = label;
		this.type = type;
		this.position = position;
		this.stroke = stroke;
		this.fill = fill;
		this.isLabelled = isLabelled;
		this.isVisible = isVisible;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public PositionShape getPosition() {
		return position;
	}

	public void setPosition(PositionShape position) {
		this.position = position;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	public Fill getFill() {
		return fill;
	}

	public void setFill(Fill fill) {
		this.fill = fill;
	}

	public boolean isLabelled() {
		return isLabelled;
	}

	public void setLabelled(boolean isLabelled) {
		this.isLabelled = isLabelled;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	@Override
	public String toString() {
		return "Shape [id=" + id + ", label=" + label + ", type=" + type + ", position=" + position + ", stroke="
				+ stroke + ", fill=" + fill + ", isLabelled=" + isLabelled + ", isVisible=" + isVisible + "]";
	}

}
