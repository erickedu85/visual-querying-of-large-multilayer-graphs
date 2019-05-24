package app.utils;

public class SumgraToGml {

	public static void main(String[] args) {

		String pathName = "data/flickr/relationships.txt";
		String outPathName = "data/flickr/graph.gml";

		DiversParser.saveGML(DiversParser.loadSUMGRA(pathName), outPathName);

		System.out.println("Finish :)");

	}

}
