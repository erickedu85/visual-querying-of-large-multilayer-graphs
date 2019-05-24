package app.gui.query;

import javafx.scene.paint.Color;

public class EdgeType {

	private int idEdgeType;
	private Color color;
	private String label;
	private String relativePath;
	private String cssColorStyle;
	private boolean isGhost;

	/**
	 * @param idEdgeType
	 * @param color
	 *            JavaFX Color
	 * @param label
	 * @param relativePath
	 * @param cssColorStyle
	 */
	public EdgeType(int idEdgeType, Color color, String label, String relativePath, String cssColorStyle) {
		this.idEdgeType = idEdgeType;
		this.color = color;
		this.label = label;
		this.relativePath = relativePath;
		this.cssColorStyle = cssColorStyle;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getCssColorStyle() {
		return cssColorStyle;
	}

	public void setCssColor(String cssColorStyle) {
		this.cssColorStyle = cssColorStyle;
	}

	public int getIdEdgeType() {
		return idEdgeType;
	}

	public void setIdEdgeType(int idEdgeType) {
		this.idEdgeType = idEdgeType;
	}

	public void setCssColorStyle(String cssColorStyle) {
		this.cssColorStyle = cssColorStyle;
	}

	public boolean isGhost() {
		return isGhost;
	}

	public void setGhost(boolean isGhost) {
		this.isGhost = isGhost;
	}

	@Override
	public String toString() {
		return "EdgeType [idEdgeType=" + idEdgeType + ", color=" + color + ", label=" + label + ", isGhost=" + isGhost
				+ "]";
	}

}
