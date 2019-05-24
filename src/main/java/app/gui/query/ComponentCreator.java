package app.gui.query;

import java.io.File;
import java.util.List;

import org.controlsfx.control.textfield.TextFields;

import app.graph.structure.ColorShape;
import app.graph.structure.Vertex;
import app.gui.main.Constants;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ComponentCreator {
	/**
	 * @param text
	 * @param graphic
	 * @param contentDisplay
	 * @param width
	 * @param height
	 * @param translateX
	 * @param translateY
	 * @param obj
	 * @param tooltipText
	 * @return
	 */
	public static Button makeButton(String text, String graphic, ContentDisplay contentDisplay, int width, int height,
			int translateX, int translateY, Class<?> obj, String tooltipText) {

		Button btn = new Button();

		btn.setText(text);
		if (graphic != "") {
			btn.setGraphic(new ImageView(new Image(
					obj.getClassLoader().getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat(graphic)))));
			// this.getClass().getClassLoader()......
		}

		btn.setMinWidth(width);
		btn.setMaxWidth(width);
		btn.setMinHeight(height);
		btn.setMaxHeight(height);
		btn.setPrefSize(width, height);

		btn.setContentDisplay(contentDisplay);
		btn.setTranslateX(translateX);
		btn.setTranslateY(translateY);
		btn.setTooltip(new Tooltip(tooltipText));

		return btn;
	}

	/**
	 * @param text
	 * @param graphic
	 * @param toogleGroup
	 * @param contentDisplay
	 * @param width
	 * @param height
	 * @param translateX
	 * @param translateY
	 * @param obj
	 * @param tooltipText
	 * @return
	 */
	public static ToggleButton makeToggleButton(String text, String graphic, ToggleGroup toogleGroup,
			ContentDisplay contentDisplay, int width, int height, int translateX, int translateY, Class<?> obj,
			String tooltipText) {

		ToggleButton tgb = new ToggleButton(text, new ImageView(
				new Image(obj.getClassLoader().getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat(graphic)))));

		tgb.setToggleGroup(toogleGroup);

		tgb.setMinWidth(width);
		tgb.setMaxWidth(width);
		tgb.setMinHeight(height);
		tgb.setMaxHeight(height);
		tgb.setPrefSize(width, height);

		tgb.setContentDisplay(contentDisplay);
		tgb.setTranslateX(translateX);
		tgb.setTranslateY(translateY);
		tgb.setTooltip(new Tooltip(tooltipText));

		return tgb;
	}

	/**
	 * @param min
	 * @param max
	 * @param value
	 * @param majorTickUnit
	 * @param minorTickCount
	 * @param blockIncrement
	 * @param showTickLabels
	 * @param showTickMarks
	 * @return
	 */
	public static Slider makeSlider(double min, double max, double value, double majorTickUnit, int minorTickCount,
			double blockIncrement, boolean showTickLabels, boolean showTickMarks) {
		Slider slider = new Slider(min, max, value);
		slider.setMajorTickUnit(majorTickUnit);
		slider.setMinorTickCount(minorTickCount);
		slider.setBlockIncrement(blockIncrement);
		slider.setShowTickLabels(showTickLabels);
		slider.setShowTickMarks(showTickMarks);

		return slider;
	}

	/**
	 * @param text
	 * @param styleClass
	 * @return
	 */
	public static Label makeLabel(String text, String styleClass) {
		Label label = new Label();
		label.setText(text);
		label.getStyleClass().add(styleClass);
		return label;

	}

	/**
	 * @param text
	 * @param isSelected
	 * @return
	 */
	public static CheckBox makeCheckBox(String text, boolean isSelected) {
		CheckBox checkBox = new CheckBox();
		checkBox.setText(text);
		checkBox.setSelected(isSelected);
		return checkBox;
	}

	/**
	 * Method to fill the combobox label nodes from a list of nodes
	 * 
	 * @param listNodes
	 */
	public static void fillCmbLabel(ComboBox<String> cmbLabel, ObservableList<String> observableList) {
		// Setting the observable list to the combobox label nodes
		cmbLabel.setItems(observableList);
		// Enabling auto completion
		TextFields.bindAutoCompletion(cmbLabel.getEditor(), cmbLabel.getItems());
	}

	/**
	 * Method to make an EdgeType, used in Visual Graph Queryin
	 * 
	 * @param id
	 * @param label
	 * @return
	 */
	public static EdgeType makeEdgeType(int id, String label) {
		EdgeType edgeType = new EdgeType(id,
				ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_ColorCategory(id)), label,
				"type_".concat(String.valueOf(id)).concat(".png"), ColorShape.getHSBGoogle_CSSStyle_ColorCategory(id));

		return edgeType;
	}

	/**
	 * @param extensions
	 * @return
	 */
	public static FileChooser makeFileChooser(String[] extensions) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(Constants.GUI_TITLE_MSG_DIALOG);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		for (String ext : extensions) {
			String description = ext.toUpperCase() + " files (*." + ext + ")";
			String extension = "*." + ext;
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));
		}

		return fileChooser;
	}
}
