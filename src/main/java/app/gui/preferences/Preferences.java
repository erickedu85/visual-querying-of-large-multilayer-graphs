package app.gui.preferences;

import org.apache.log4j.Logger;

import app.graph.structure.ColorShape;
import app.gui.database.GraphDBView;
import app.gui.main.Constants;
import app.gui.main.MainSplitPanel;
import app.gui.query.ComponentCreator;
import app.gui.query.GraphQuerying;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Preferences {

	public final static Logger logger = Logger.getLogger(Preferences.class);
	public static GraphDBView processingApp; // handle action

	public static MainSplitPanel parent;
	private Scene scene;

	private static final String styleClassSubTitle = "subTitle";
	private static final String styleClassItem = "item";

	// TITLED PANE
	private TitledPane tpGraphView = new TitledPane();
	private TitledPane tpEmbeddings = new TitledPane();
	private TitledPane tpKelps = new TitledPane();
	private TitledPane tpSumgra = new TitledPane();

	private final Accordion accordion = new Accordion();

	public Preferences() {

		Group root = new Group();
		scene = new Scene(root, Constants.LAYOUT_QUERY_VIEW_WIDTH, Constants.PREF_HEIGHT, Color.WHITE);
		scene.getStylesheets().add(Constants.JAVAFX_STYLE_FILE);

		// ------- GRAPH VIEW -------
		//
		Slider slVerticesDiameter = ComponentCreator.makeSlider(1, 50, Constants.GRAPH_DB_NODE_DIAMETER, 10, 1, 1, true,
				true);
		slVerticesDiameter.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.graph.setVerticesDiameter(new_val.doubleValue());
			}
		});
		//
		Slider slVerticesOpacity = ComponentCreator.makeSlider(5, 100, Constants.GRAPH_DB_NODE_FILL.getFillOpacity(),
				50, 5, 1, true, true);
		slVerticesOpacity.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.graph.setVerticesOpacity(new_val.doubleValue());
			}
		});
		//
		final ColorPicker cpVerticesColor = new ColorPicker(
				ColorShape.parserColorProcessingToJavafx(Constants.GRAPH_DB_NODE_FILL.getFillColor()));
		cpVerticesColor.valueProperty().addListener(new ChangeListener<Color>() {
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				processingApp.setAppBlocked(false);
				processingApp.graph.setVerticesColor(ColorShape.parserColorJavafxToProcessingHsbOLD(newValue));
			}
		});
		//
		final CheckBox chkVerticesVisible = ComponentCreator.makeCheckBox("", true);
		chkVerticesVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.setShowVerticesGraphDB(chkVerticesVisible.isSelected());
			};
		});
		//
		final CheckBox chkLabelGDBVisible = ComponentCreator.makeCheckBox("", true);
		chkLabelGDBVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				// processingApp.setShowLabelGraphDB(chkLabelGDBVisible.isSelected());
			};
		});
		//
		CheckBox chkEdgesVisible = new CheckBox();
		//
		Slider slLevelTransitionHeatMap = ComponentCreator.makeSlider(1, 30, Constants.HEATMAP_INIT_TRANSITION_ZOOM, 5,
				1, 1, true, true);
		slLevelTransitionHeatMap.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.setHeatMapTransitionZoom(new_val.intValue());
			}
		});
		//
		Slider slRadiusHeatMap = ComponentCreator.makeSlider(10, 50, Constants.HEATMAP_INIT_RADIUS, 10, 1, 1, true,
				true);
		slRadiusHeatMap.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.setHeatMapRadius(new_val.intValue());
			}
		});

		//
		Label lbNode = ComponentCreator.makeLabel("Nodes", styleClassSubTitle);
		Label lbDiameter = ComponentCreator.makeLabel("Diameter: ", styleClassItem);
		Label lbColor = ComponentCreator.makeLabel("Color: ", styleClassItem);
		Label lbOpacity = ComponentCreator.makeLabel("Opacity: ", styleClassItem);
		Label lbNodeVisible = ComponentCreator.makeLabel("Visible: ", styleClassItem);
		// Label lbNodeLabel = ComponentCreator.makeLabel("Label: ",
		// styleClassItem);
		// Label lbEdge = ComponentCreator.makeLabel("Edges",
		// styleClassSubTitle);
		// Label lbEdgeVisible = ComponentCreator.makeLabel("Visible: ",
		// styleClassItem);
		Label lbHeatMap = ComponentCreator.makeLabel("Heatmap Appearance", styleClassSubTitle);
		Label lbTransitionsHeatMap = ComponentCreator.makeLabel("Transition level: ", styleClassItem);
		Label lbRadiusHeatMap = ComponentCreator.makeLabel("Radius: ", styleClassItem);

		GridPane gridGraphDBView = new GridPane();
		gridGraphDBView.setGridLinesVisible(false);
		gridGraphDBView.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA1));
		gridGraphDBView.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA2));
		gridGraphDBView.setVgap(Constants.PREF_VERTICAL_GAP);
		gridGraphDBView.setHgap(Constants.PREF_HORIZONTAL_GAP);
		gridGraphDBView.setPadding(Constants.PREF_INSETS);
		gridGraphDBView.add(lbNode, 0, 0);
		gridGraphDBView.add(lbDiameter, 0, 1);
		gridGraphDBView.add(slVerticesDiameter, 1, 1);
		gridGraphDBView.add(lbColor, 0, 2);
		gridGraphDBView.add(cpVerticesColor, 1, 2);
		gridGraphDBView.add(lbOpacity, 0, 3);
		gridGraphDBView.add(slVerticesOpacity, 1, 3);
		gridGraphDBView.add(lbNodeVisible, 0, 4);
		gridGraphDBView.add(chkVerticesVisible, 1, 4);
		// gridGraphDBView.add(lbNodeLabel, 0, 5);
		// gridGraphDBView.add(chkLabelGDBVisible, 1, 5);
		// gridGraphDBView.add(lbEdge, 0, 6);
		// gridGraphDBView.add(lbEdgeVisible, 0, 7);
		// gridGraphDBView.add(chkEdgesVisible, 1, 7);
		gridGraphDBView.add(lbHeatMap, 0, 8, 2, 1);
		gridGraphDBView.add(lbTransitionsHeatMap, 0, 9);
		gridGraphDBView.add(slLevelTransitionHeatMap, 1, 9);
		gridGraphDBView.add(lbRadiusHeatMap, 0, 10);
		gridGraphDBView.add(slRadiusHeatMap, 1, 10);
		//
		tpGraphView = new TitledPane();
		tpGraphView.setText(Constants.PREF_PANE_GRAPH_DATABASE);
		gridGraphDBView.getStyleClass().add("preferences");
		tpGraphView.setContent(gridGraphDBView);

		//
		//
		// ------- EMBEDDINGS VIEW -------
		//
		Slider slEmbeddingOpacity = ComponentCreator.makeSlider(5, 100,
				Constants.GRAPH_DB_NODE_PATTERN_FILL.getFillOpacity(), 50, 5, 1, true, true);
		slEmbeddingOpacity.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.graph.setVerticesEmbeddingsOpacity(new_val.doubleValue());
			}
		});
		//
		final ColorPicker cpEmbeddingColor = new ColorPicker(
				ColorShape.parserColorProcessingToJavafx(Constants.GRAPH_DB_NODE_PATTERN_FILL.getFillColor()));
		cpEmbeddingColor.valueProperty().addListener(new ChangeListener<Color>() {
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				processingApp.setAppBlocked(false);
				processingApp.graph
						.setVerticesEmbeddingsColor(ColorShape.parserColorJavafxToProcessingHsbOLD(newValue));
			}
		});
		//
		final CheckBox chkEmbeddingVisible = ComponentCreator.makeCheckBox("", true);
		chkEmbeddingVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.setShowEmbeddingsGraphDB(chkEmbeddingVisible.isSelected());
			};
		});
		//
		//
		final CheckBox chkEmbeddingStroke = ComponentCreator.makeCheckBox("", true);
		chkEmbeddingStroke.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.graph.setShowEmbeddingsStroke(chkEmbeddingStroke.isSelected());
			};
		});
		//
		final CheckBox chkEmbeddingLabel = ComponentCreator.makeCheckBox("", true);
		chkEmbeddingLabel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.graph.setShowEmbeddingLabels(chkEmbeddingLabel.isSelected());
			};
		});
		//
		// final CheckBox chkEmbeddingRelativeLabel =
		// ComponentCreator.makeCheckBox("", true);
		// chkEmbeddingRelativeLabel.setOnAction(new EventHandler<ActionEvent>()
		// {
		// public void handle(ActionEvent event) {
		// processingApp.setAppBlocked(false);
		// processingApp.graph.setEmbeddingLabelsRelative(chkEmbeddingRelativeLabel.isSelected());
		// };
		// });
		//

		//
		Slider slEmbeddingRectLabelPadding = ComponentCreator.makeSlider(0, 20,
				Constants.GRAPH_DB_NODE_PATTERN_RECT_PADDING, 10, 5, 1, true, true);
		slEmbeddingRectLabelPadding.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.graph.setBackgroundEmbeddingPadding(new_val.doubleValue());
			}
		});
		//
		//
		//
		final CheckBox chkEmbeddingRectangleVisible = ComponentCreator.makeCheckBox("", false);
		chkEmbeddingRectangleVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.graph.setShowEmbeddingRectangleLabels(chkEmbeddingRectangleVisible.isSelected());
			};
		});
		//
		final ColorPicker cpEmbeddingRectLabelColor = new ColorPicker(ColorShape
				.parserColorProcessingToJavafx(Constants.GRAPH_DB_NODE_PATTERN_RECT_BACKGROUND_FILL.getFillColor()));
		cpEmbeddingRectLabelColor.valueProperty().addListener(new ChangeListener<Color>() {
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				processingApp.setAppBlocked(false);
				processingApp.graph
						.setBackgroundEmbeddingRectangle(ColorShape.parserColorJavafxToProcessingHsbOLD(newValue));
			}
		});
		//
		//
		Slider slEmbeddingRectLabelOpacity = ComponentCreator.makeSlider(5, 100,
				Constants.GRAPH_DB_NODE_PATTERN_RECT_BACKGROUND_FILL.getFillOpacity(), 50, 5, 1, true, true);
		slEmbeddingRectLabelOpacity.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.graph.setOpacityBackgroundEmbeddingRectangle(new_val.doubleValue());
			}
		});

		Label lbEmbedNode = ComponentCreator.makeLabel("Nodes", styleClassSubTitle);
		Label lbEmbedNodeColor = ComponentCreator.makeLabel("Color: ", styleClassItem);
		Label lbEmbedNodeOpacity = ComponentCreator.makeLabel("Opacity: ", styleClassItem);
		Label lbEmbedNodeVisible = ComponentCreator.makeLabel("Visible: ", styleClassItem);
		Label lbEmbedNodeStroke = ComponentCreator.makeLabel("Stroke: ", styleClassItem);
		// Label lbEmbedNodeRelativeLabel = ComponentCreator.makeLabel("Text
		// height relative: ", styleClassItem);

		Label lbEmbeLabel = ComponentCreator.makeLabel("Label: ", styleClassSubTitle);
		Label lbEmbedNodeLabel = ComponentCreator.makeLabel("Visible: ", styleClassItem);
		Label lbEmbedRectVisible = ComponentCreator.makeLabel("Background: ", styleClassItem);
		Label lbEmbedRectPadding = ComponentCreator.makeLabel("Padding: ", styleClassItem);
		Label lbEmbedRectLabelColor = ComponentCreator.makeLabel("Color: ", styleClassItem);
		Label lbEmbedRectLabelOpacity = ComponentCreator.makeLabel("Opacity: ", styleClassItem);

		GridPane gridEmbeddings = new GridPane();
		gridEmbeddings.setGridLinesVisible(false);
		gridEmbeddings.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA1));
		gridEmbeddings.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA2));
		gridEmbeddings.setVgap(Constants.PREF_VERTICAL_GAP);
		gridEmbeddings.setHgap(Constants.PREF_HORIZONTAL_GAP);
		gridEmbeddings.setPadding(Constants.PREF_INSETS);
		gridEmbeddings.add(lbEmbedNode, 0, 0);
		gridEmbeddings.add(lbEmbedNodeColor, 0, 1);
		gridEmbeddings.add(cpEmbeddingColor, 1, 1);
		gridEmbeddings.add(lbEmbedNodeOpacity, 0, 2);
		gridEmbeddings.add(slEmbeddingOpacity, 1, 2);
		gridEmbeddings.add(lbEmbedNodeVisible, 0, 3);
		gridEmbeddings.add(chkEmbeddingVisible, 1, 3);
		gridEmbeddings.add(lbEmbedNodeStroke, 0, 4);
		gridEmbeddings.add(chkEmbeddingStroke, 1, 4);

		gridEmbeddings.add(lbEmbeLabel, 0, 5);
		gridEmbeddings.add(lbEmbedNodeLabel, 0, 6);
		gridEmbeddings.add(chkEmbeddingLabel, 1, 6);
		gridEmbeddings.add(lbEmbedRectVisible, 0, 7);
		gridEmbeddings.add(chkEmbeddingRectangleVisible, 1, 7);
		gridEmbeddings.add(lbEmbedRectPadding, 0, 8);
		gridEmbeddings.add(slEmbeddingRectLabelPadding, 1, 8);

		gridEmbeddings.add(lbEmbedRectLabelColor, 0, 9);
		gridEmbeddings.add(cpEmbeddingRectLabelColor, 1, 9);
		gridEmbeddings.add(lbEmbedRectLabelOpacity, 0, 10);
		gridEmbeddings.add(slEmbeddingRectLabelOpacity, 1, 10);

		//
		tpEmbeddings = new TitledPane();
		tpEmbeddings.setText(Constants.PREF_PANE_GRAPH_EMBEDDINGS);
		gridEmbeddings.getStyleClass().add("preferences");
		tpEmbeddings.setContent(gridEmbeddings);

		// ------- TITLEDPANE KELPS -------
		//
		//
		GridPane gridKelps = new GridPane();
		gridKelps.setGridLinesVisible(false);
		gridKelps.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA1));
		gridKelps.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA2));
		gridKelps.setVgap(Constants.PREF_VERTICAL_GAP);
		gridKelps.setHgap(Constants.PREF_HORIZONTAL_GAP);
		gridKelps.setPadding(Constants.PREF_INSETS);

		//
		Slider slKelpFacNode = ComponentCreator.makeSlider(10, 80, Constants.KELP_NODE_FACTOR, 10, 1, 1, true, true);
		slKelpFacNode.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.setKelpFacteurNode(new_val.intValue());
			}
		});
		//
		Slider slKelpFacEdge = ComponentCreator.makeSlider(10, 50, Constants.KELP_EDGE_FACTOR, 10, 1, 1, true, true);
		slKelpFacEdge.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.setKelpFacteurEdge(new_val.intValue());
			}
		});

		//
		final CheckBox chkKelpShowEdge = ComponentCreator.makeCheckBox("", Constants.KELP_EDGE_VISIBLE);
		chkKelpShowEdge.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.setShowKelpLines(chkKelpShowEdge.isSelected());
			};
		});

		//
		final CheckBox chkKelpOverlap = ComponentCreator.makeCheckBox("", Constants.KELP_CHECK_OVERLAP_EDGE_NODE);
		chkKelpOverlap.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.setKelpOverlapLines(chkKelpOverlap.isSelected());
			};
		});
		//
		//
		Slider slKelpTextSize = ComponentCreator.makeSlider(5, 30, Constants.KELP_TEXT_SIZE, 10, 5, 1, true, true);
		slKelpTextSize.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.hypergraph.setTextSize(new_val.doubleValue());
			}
		});
		//
		Slider slKelpPadding = ComponentCreator.makeSlider(0, 20, Constants.KELP_TEXT_BACKGROUND_PADDING, 10, 5, 1,
				true, true);
		slKelpPadding.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.hypergraph.setBackgroundTextPadding(new_val.doubleValue());
			}
		});

		//
		final ColorPicker cKelpLabelColor = new ColorPicker(
				ColorShape.parserColorProcessingToJavafx(Constants.KELP_TEXT_BACKGROUND_FILL.getFillColor()));
		cKelpLabelColor.valueProperty().addListener(new ChangeListener<Color>() {
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				processingApp.setAppBlocked(false);
				processingApp.hypergraph.setBackgroundText(ColorShape.parserColorJavafxToProcessingHsbOLD(newValue));
			}
		});
		//
		//
		Slider slKelpLabelOpacity = ComponentCreator.makeSlider(5, 100,
				Constants.KELP_TEXT_BACKGROUND_FILL.getFillOpacity(), 50, 5, 1, true, true);
		slKelpLabelOpacity.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.hypergraph.setOpacityBackgroundTextPadding(new_val.doubleValue());
			}
		});

		Label lbEmbedKelp = ComponentCreator.makeLabel("Set Relations (Kelp-Diagrams)", styleClassSubTitle);
		Label lbKelpFacNode = ComponentCreator.makeLabel("Node facteur: ", styleClassItem);
		Label lbKelpFacEdge = ComponentCreator.makeLabel("Edge facteur: ", styleClassItem);

		Label lbKelpShowEdge = ComponentCreator.makeLabel("Show edge: ", styleClassItem);
		Label lbKelpOverlapEdge = ComponentCreator.makeLabel("Check overlap: ", styleClassItem);

		Label lbKelpLabel = ComponentCreator.makeLabel("Label", styleClassSubTitle);
		Label lbKelpLabelSize = ComponentCreator.makeLabel("Text size: ", styleClassItem);
		Label lbKelpLabelPadding = ComponentCreator.makeLabel("Padding: ", styleClassItem);
		Label lbKelpLabelColor = ComponentCreator.makeLabel("Color: ", styleClassItem);
		Label lbKelpLabelOpacity = ComponentCreator.makeLabel("Opacity: ", styleClassItem);

		gridKelps.add(lbEmbedKelp, 0, 0, 2, 1);
		gridKelps.add(lbKelpFacNode, 0, 1);
		gridKelps.add(slKelpFacNode, 1, 1);
		gridKelps.add(lbKelpFacEdge, 0, 2);
		gridKelps.add(slKelpFacEdge, 1, 2);
		
		gridKelps.add(lbKelpShowEdge, 0, 3);
		gridKelps.add(chkKelpShowEdge, 1, 3);
		
		gridKelps.add(lbKelpOverlapEdge, 0, 4);
		gridKelps.add(chkKelpOverlap, 1, 4);

		gridKelps.add(lbKelpLabel, 0, 7);

		gridKelps.add(lbKelpLabelSize, 0, 8);
		gridKelps.add(slKelpTextSize, 1, 8);
		gridKelps.add(lbKelpLabelPadding, 0, 9);
		gridKelps.add(slKelpPadding, 1, 9);
		gridKelps.add(lbKelpLabelColor, 0, 10);
		gridKelps.add(cKelpLabelColor, 1, 10);
		gridKelps.add(lbKelpLabelOpacity, 0, 11);
		gridKelps.add(slKelpLabelOpacity, 1, 11);

		tpKelps = new TitledPane();
		tpKelps.setText(Constants.PREF_PANE_KELP_DIAGRAMS);
		gridKelps.getStyleClass().add("preferences");
		tpKelps.setContent(gridKelps);

		//
		//
		//
		//

		Slider slSumgraBufferThreshold = ComponentCreator.makeSlider(1, 200,
				Constants.SUMGRA_THRESHOLD_BUFFER_BACKTRAKING, 200, 1, 1, true, true);
		slSumgraBufferThreshold.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				GraphQuerying.sumgraThresholdBufferBacktraking = new_val.intValue();
				// processingApp.hypergraph.setOpacityBackgroundTextPadding(new_val.doubleValue());
			}
		});

		Slider slSumgraSleepThread = ComponentCreator.makeSlider(0, 10, Constants.SUMGRA_SLEEP_BACKTRAKING, 200, 1, 1,
				true, true);
		slSumgraSleepThread.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				GraphQuerying.sumgraSleepBacktraking = new_val.intValue();
				// processingApp.hypergraph.setOpacityBackgroundTextPadding(new_val.doubleValue());
			}
		});

		Label lbSumgraBufferSize = ComponentCreator.makeLabel("Buffer threshold:", styleClassItem);
		Label lbSumgraSleepThread = ComponentCreator.makeLabel("Sleep thread (ms): ", styleClassItem);

		GridPane gridSumgra = new GridPane();
		gridSumgra.setGridLinesVisible(false);
		gridSumgra.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA1));
		gridSumgra.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA2));
		gridSumgra.setVgap(Constants.PREF_VERTICAL_GAP);
		gridSumgra.setHgap(Constants.PREF_HORIZONTAL_GAP);
		gridSumgra.setPadding(Constants.PREF_INSETS);
		gridSumgra.add(lbSumgraBufferSize, 0, 0);
		gridSumgra.add(slSumgraBufferThreshold, 1, 0);
		gridSumgra.add(lbSumgraSleepThread, 0, 1);
		gridSumgra.add(slSumgraSleepThread, 1, 1);

		//
		tpSumgra = new TitledPane();
		tpSumgra.setText(Constants.PREF_PANE_GRAPH_ENGINE);
		gridSumgra.getStyleClass().add("preferences");
		tpSumgra.setContent(gridSumgra);

		//
		//

		// ------- TITLEDPANE GRAPH QUERY -------
		//
		// tpGraphQuery = new TitledPane();
		// tpGraphQuery.setText(Constants.PREF_PANE_GRAPH_QUERY);
		// GridPane gridGraphQuery = new GridPane();
		// gridGraphQuery.setVgap(Constants.PREF_VERTICAL_GAP);
		// gridGraphQuery.setPadding(Constants.PREF_INSETS);
		// tpGraphQuery.setContent(gridGraphQuery);

		// ------- TITLEDPANE GRAPH QUERY ENGINE -------
		//
		// tpGraphQueryEngine = new TitledPane();
		// tpGraphQueryEngine.setText(Constants.PREF_PANE_GRAPH_QUERY_ENGINE);
		// GridPane gridGraphQueryEngine = new GridPane();
		// gridGraphQueryEngine.setVgap(Constants.PREF_VERTICAL_GAP);
		// gridGraphQueryEngine.setHgap(Constants.PREF_HORIZONTAL_GAP);
		// gridGraphQueryEngine.setPadding(Constants.PREF_INSETS);
		// tpGraphQueryEngine.setContent(gridGraphQueryEngine);

		// ADDING TO THE ACCORDION
		accordion.getPanes().addAll(tpGraphView, tpEmbeddings, tpKelps, tpSumgra);
		accordion.setExpandedPane(tpGraphView);

		root.getChildren().add(accordion);
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

}
