package app.graph.structure;

import javafx.scene.paint.Color;
import processing.core.PApplet;
import processing.core.PConstants;

public class ColorShape {

	public static PApplet parent;

	public ColorShape() {
	}

	/**
	 * @param colorOriginal
	 * @param divSaturationFacteur
	 * @return
	 */
	public static int lowColorSaturationHSB(int colorOriginal, float divSaturationFacteur) {
		float nuevoColorHue = parent.hue(colorOriginal);
		float nuevoColorSaturation = (parent.saturation(colorOriginal) / divSaturationFacteur);
		float nuevoColorBrightness = parent.brightness(colorOriginal);
		int nuevoColorMenosSaturado = parent.color(nuevoColorHue, nuevoColorSaturation, nuevoColorBrightness);
		return nuevoColorMenosSaturado;
	}

	/**
	 * @param colorProcessing
	 * @return
	 */
	public static Color parserColorProcessingToJavafx(int colorProcessing) {
		parent.colorMode(PConstants.HSB, 360, 100, 100, 100);
		// Color color = Color.web(parent.hex(colorProcessing).substring(2), 1);
		Color color = Color.web(PApplet.hex(colorProcessing).substring(2), 1);
		return color;
	}

	/**
	 * @param colorProcessing
	 * @param opacity
	 *            0 - 1
	 * @return
	 */
	public static Color parserColorProcessingToJavafx(int colorProcessing, double opacity) {
		Color color = Color.web(PApplet.hex(colorProcessing).substring(2), opacity);
		return color;
	}

	public static int parserColorJavafxToProcessingHsbOLD(Color colorJavafx) {
		double hue = colorJavafx.getHue(); // 0 - 360
		double saturation = colorJavafx.getSaturation(); // 0 - 1
		double brightness = colorJavafx.getBrightness(); // 0 - 1
		double opacity = colorJavafx.getOpacity();
		// System.out.println(colorJavafx + " - hue: " + hue + ", saturation:" +
		// saturation + ", brightness: " + brightness + ", opacity: " +
		// opacity);
		parent.colorMode(PConstants.HSB, 360, 1, 1, 1);
		int color = parent.color((float) hue, (float) saturation, (float) brightness, (float) opacity);
		parent.colorMode(PConstants.HSB, 360, 100, 100, 100);
		return color;
	}

	public static Color getJavaFx_Red() {
		// #f00
		return Color.RED;
	}

	public static Color getJavaFx_Green() {
		// #0f0
		return Color.GREEN;
	}

	public static Color getJavaFx_Yellow() {

		return Color.YELLOW;
	}

	public static Color getJavaFx_Blue() {
		// #00f
		return Color.BLUE;
	}

	public static Color getJavaFx_Black() {
		return Color.BLACK;
	}

	public static Color getJavaFx_White() {
		return Color.WHITE;
	}

	public static Color getJavaFx_Pink() {
		// Dark Pink
		return Color.PINK;
	}

	public static Color getJavaFx_Cyan() {
		return Color.CYAN;
	}

	public static Color getJavaFx_Gray() {
		return Color.GRAY;
	}

	public static Color getJavaFx_Orange() {
		return Color.ORANGE;
	}

	public static Color getJavaFx_Brown() {
		return Color.BROWN;
	}

	public static Color getJavaFx_Purple() {
		// #FF00FF
		return Color.PURPLE;
	}

	public static int getHSB_FromLerp() {
		// #fee0d2 red
		// #c7e9c0 green
		// return parent.color(19, 17, 99); //RED
		return parent.color(110, 18, 91); // Green
	}

	public static int getHSB_ToLerp() {
		// #a50f15 red
		// #006d2c green
		// return parent.color(0, 100, 100); //RED
		return parent.color(144, 100, 43); // Green
	}

	public static int getHSB_NodePattern() {
		// #a1d99b
		return parent.color(114, 29, 85);
	}

	public static int getHSB_NodePatternStroke() {
		return parent.color(155, 99, 44);
	}

	public static int getHSB_NodePatternHover() {
		// #31a354
		return parent.color(138, 70, 64);
	}

	public static int getHSB_NodePatternHoverStroke() {
		return getHSB_NodePatternStroke();
	}

	public static int getHSB_NodePatternSelected() {
		return parent.color(358, 85, 90);
	}

	public static int getHSB_NodePatternSelectedStroke() {
		return parent.color(358, 80, 89);
	}

	public static int getHSB_NodePatternSelectedHover() {
		return parent.color(358, 79, 63);
	}

	public static int getHSB_NodePatternSelectedHoverStroke() {
		return getHSB_NodePatternSelectedStroke();
	}

	public static int getHSB_KelpTextBackground() {
		return parent.color(224, 40, 100);
	}

	public static int getHSB_Red() {
		// #f00
		return parent.color(0, 100, 100);
	}

	public static int getHSB_Green() {
		// #0f0
		return parent.color(120, 100, 100);
	}

	public static int getHSB_Yellow() {

		return parent.color(60, 100, (float) 96.08);
	}

	public static int getHSB_Blue() {
		// #00f
		return parent.color(240, 100, 100);
	}

	public static int getHSB_Black() {
		return parent.color(0, 0, 0);
	}

	public static int getHSB_White() {
		// parent.colorMode(PConstants.HSB, 360, 100, 100, 100); //
		// Hue-Saturation-Brightness-Alpha
		return parent.color(0, 0, 100);
	}

	public static int getHSB_Pink() {
		// Dark Pink
		return parent.color((float) 342.04, (float) 63.64, (float) 90.59);
	}

	public static int getHSB_Cyan() {
		return parent.color(180, 100, 100);
	}

	public static int getHSB_Gray() {
		return parent.color(0, 0, 100);
	}

	public static int getHSB_Orange() {
		return parent.color((float) 29.88, 100, 100);
	}

	public static int getHSB_Brown() {
		// #964b00
		return parent.color(30, 100, (float) 58.82);
	}

	public static int getHSB_Purple() {
		// #FF00FF
		return parent.color(300, 100, 100);
	}

	public static int getHSB_EdgeType_1() {
		// #1f77b4
		return parent.color((float) 204.56, (float) 82.78, (float) 70.59);
	}

	public static int getHSB_EdgeType_2() {
		// #ff7f0e
		return parent.color((float) 28.13, (float) 94.51, (float) 100);
	}

	public static int getHSB_EdgeType_3() {
		// #2ca02c
		return parent.color((float) 120, (float) 72.5, (float) 62.75);
	}

	public static int getHSB_EdgeType_4() {
		// #d62728
		return parent.color((float) 359.66, (float) 81.78, (float) 83.92);
	}

	public static int getHSB_EdgeType_5() {
		// #9467bd
		return parent.color((float) 271.4, (float) 45.5, (float) 74.12);
	}

	public static int getHSB_EdgeType_6() {
		// #8c564b
		return parent.color((float) 10.15, (float) 46.43, (float) 54.9);
	}

	public static int getHSB_EdgeType_7() {
		// #e377c2
		return parent.color((float) 318.33, (float) 47.58, (float) 89.02);
	}

	// HSB GOOGLE CATEGORICAL COLORS
	public static int getHSBGoogle_Cat0() {
		return parent.color((float) 220, (float) 75, (float) 80);
	}// #3366cc

	public static String getHSBGoogle_CSSStyle_Cat0() {
		return "#3366cc";
	}

	public static int getHSBGoogle_Cat1() {
		return parent.color((float) 11.58, (float) 91.82, (float) 86.27);
	}// #dc3912

	public static String getHSBGoogle_CSSStyle_Cat1() {
		return "#dc3912";
	}

	public static int getHSBGoogle_Cat2() {
		return parent.color((float) 36, (float) 100, (float) 100);
	}// #ff9900

	public static String getHSBGoogle_CSSStyle_Cat2() {
		return "#ff9900";
	}

	public static int getHSBGoogle_Cat3() {
		return parent.color((float) 123.58, (float) 89.33, (float) 58.82);
	}// #109618

	public static String getHSBGoogle_CSSStyle_Cat3() {
		return "#109618";
	}

	public static int getHSBGoogle_Cat4() {
		return parent.color((float) 300, (float) 100, (float) 60);
	}// #990099

	public static String getHSBGoogle_CSSStyle_Cat4() {
		return "#990099";
	}

	public static int getHSBGoogle_Cat5() {
		return parent.color((float) 193.64, (float) 100, (float) 77.65);
	}// #0099c6

	public static String getHSBGoogle_CSSStyle_Cat5() {
		return "#0099c6";
	}

	public static int getHSBGoogle_Cat6() {
		return parent.color((float) 340, (float) 69.23, (float) 86.67);
	}// #dd4477

	public static String getHSBGoogle_CSSStyle_Cat6() {
		return "#dd4477";
	}

	public static int getHSBGoogle_Cat7() {
		return parent.color((float) 84, (float) 100, (float) 66.67);
	}// #66aa00

	public static String getHSBGoogle_CSSStyle_Cat7() {
		return "#66aa00";
	}

	public static int getHSBGoogle_Cat8() {
		return parent.color((float) 0, (float) 75, (float) 72.16);
	}// #b82e2e

	public static String getHSBGoogle_CSSStyle_Cat8() {
		return "#b82e2e";
	}

	public static int getHSBGoogle_Cat9() {
		return parent.color((float) 210, (float) 67.11, (float) 58.43);
	}// #316395

	public static String getHSBGoogle_CSSStyle_Cat9() {
		return "#316395";
	}

	public static int getHSBGoogle_Cat10() {
		return parent.color((float) 300, (float) 55.56, (float) 60);
	}// #994499

	public static String getHSBGoogle_CSSStyle_Cat10() {
		return "#994499";
	}

	public static int getHSBGoogle_Cat11() {
		return parent.color((float) 172.5, (float) 80, (float) 66.67);
	}// #22aa99

	public static String getHSBGoogle_CSSStyle_Cat11() {
		return "#22aa99";
	}

	public static int getHSBGoogle_Cat12() {
		return parent.color((float) 60, (float) 90, (float) 66.67);
	}// #aaaa11

	public static String getHSBGoogle_CSSStyle_Cat12() {
		return "#aaaa11";
	}

	public static int getHSBGoogle_Cat13() {
		return parent.color((float) 30, (float) 100, (float) 90.2);
	}// #6633cc

	public static String getHSBGoogle_CSSStyle_Cat13() {
		return "#e67300";
	}

	public static int getHSBGoogle_Cat14() {
		return parent.color((float) 260, (float) 75, (float) 80);
	}// #e67300

	public static String getHSBGoogle_CSSStyle_Cat14() {
		return "#6633cc";
	}

	public static int getHSBGoogle_Cat15() {
		return parent.color((float) 0, (float) 94.96, (float) 54.51);
	}// #8b0707

	public static String getHSBGoogle_CSSStyle_Cat15() {
		return "#8b0707";
	}

	public static int getHSBGoogle_Cat16() {
		return parent.color((float) 298.62, (float) 84.47, (float) 40.39);
	}// #651067

	public static String getHSBGoogle_CSSStyle_Cat16() {
		return "#651067";
	}

	public static int getHSBGoogle_Cat17() {
		return parent.color((float) 150, (float) 65.75, (float) 57.25);
	}// #329262

	public static String getHSBGoogle_CSSStyle_Cat17() {
		return "#329262";
	}

	public static int getHSBGoogle_Cat18() {
		return parent.color((float) 217.04, (float) 48.8, (float) 65.1);
	}// #5574a6

	public static String getHSBGoogle_CSSStyle_Cat18() {
		return "#5574a6";
	}

	public static int getHSBGoogle_Cat19() {
		return parent.color((float) 238.41, (float) 65.7, (float) 67.45);
	}// #3b3eac

	public static String getHSBGoogle_CSSStyle_Cat19() {
		return "#3b3eac";
	}

	public static int getHSB_Arc() {
		// #42a5f5
		return parent.color(206, 73, 96);
	}

	public static int getHSBGoogle_ColorCategory(int type) {
		switch (type) {
		case 0:
			return (parent != null) ? getHSBGoogle_Cat0() : 0;
		case 1:
			return (parent != null) ? getHSBGoogle_Cat1() : 0;
		case 2:
			return (parent != null) ? getHSBGoogle_Cat2() : 0;
		case 3:
			return (parent != null) ? getHSBGoogle_Cat3() : 0;
		case 4:
			return (parent != null) ? getHSBGoogle_Cat4() : 0;
		case 5:
			return (parent != null) ? getHSBGoogle_Cat5() : 0;
		case 6:
			return (parent != null) ? getHSBGoogle_Cat6() : 0;
		case 7:
			return (parent != null) ? getHSBGoogle_Cat7() : 0;
		case 8:
			return (parent != null) ? getHSBGoogle_Cat8() : 0;
		case 9:
			return (parent != null) ? getHSBGoogle_Cat9() : 0;
		case 10:
			return (parent != null) ? getHSBGoogle_Cat10() : 0;
		case 11:
			return (parent != null) ? getHSBGoogle_Cat11() : 0;
		case 12:
			return (parent != null) ? getHSBGoogle_Cat12() : 0;
		case 13:
			return (parent != null) ? getHSBGoogle_Cat13() : 0;
		case 14:
			return (parent != null) ? getHSBGoogle_Cat14() : 0;
		case 15:
			return (parent != null) ? getHSBGoogle_Cat15() : 0;
		case 16:
			return (parent != null) ? getHSBGoogle_Cat16() : 0;
		case 17:
			return (parent != null) ? getHSBGoogle_Cat17() : 0;
		case 18:
			return (parent != null) ? getHSBGoogle_Cat18() : 0;
		case 19:
			return (parent != null) ? getHSBGoogle_Cat19() : 0;
		default:
			return (parent != null) ? getHSB_Black() : 0;
		}
	}

	public static String getHSBGoogle_CSSStyle_ColorCategory(int type) {
		switch (type) {
		case 0:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat0() : "";
		case 1:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat1() : "";
		case 2:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat2() : "";
		case 3:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat3() : "";
		case 4:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat4() : "";
		case 5:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat5() : "";
		case 6:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat6() : "";
		case 7:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat7() : "";
		case 8:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat8() : "";
		case 9:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat9() : "";
		case 10:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat10() : "";
		case 11:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat11() : "";
		case 12:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat12() : "";
		case 13:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat13() : "";
		case 14:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat14() : "";
		case 15:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat15() : "";
		case 16:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat16() : "";
		case 17:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat17() : "";
		case 18:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat18() : "";
		case 19:
			return (parent != null) ? getHSBGoogle_CSSStyle_Cat19() : "";
		default:
			return (parent != null) ? "" : "";
		}
	}
}
