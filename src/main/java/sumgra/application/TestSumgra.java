package sumgra.application;

import app.gui.main.SumgraBuffer;
import sumgra.data.GraphDatabase;

public class TestSumgra {

	public static void main(String[] args) {
		String pathMainGraph = "data/dblp_vis_bd/relationships.txt";
		String pathQueryGraph = "data/dblp_vis_bd/query.txt";

		SumgraBuffer sumgraBuffer = new SumgraBuffer(10000);
		GraphDatabase.sumgraBuffer = sumgraBuffer;
		int[] constraints = {3740};
		int sleepTimeBacktracking = 1;

		// In this case the buffer is filled and their is not a pickUp call
		MainSumgra main = new MainSumgra(pathMainGraph, pathQueryGraph, constraints, sleepTimeBacktracking);
		main.start();
	}
}
