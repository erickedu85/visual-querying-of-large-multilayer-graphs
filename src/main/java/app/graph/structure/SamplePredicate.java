package app.graph.structure;

import java.util.function.Predicate;

public class SamplePredicate<T> implements Predicate<T> {
	public T varc1;

	public boolean test(T varc) {
		if (varc instanceof Graph) {
			Graph graphCompare = (Graph) varc;
			if (((Graph) varc1).getListNode().containsAll(graphCompare.getListNode())) {
				return true;
			}
		}
		return false;
	}
}