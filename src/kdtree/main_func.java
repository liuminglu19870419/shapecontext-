package kdtree;

import java.util.ArrayList;
import java.util.List;

public class main_func {

	public static void main(String[] args) throws KeySizeException, IllegalArgumentException {
		KDTree<Integer> kdTree = new KDTree<>(2);
		List<KDNode<Integer>> list = new ArrayList<KDNode<Integer>>();
		double[][] keys = { { 2, 3 }, { 4, 7 }, { 5, 4 }, { 7, 2 }, { 8, 1 },
				{ 9, 6 } };
		int index = 0;
		for (double[] ds : keys) {
			list.add(new KDNode<Integer>(ds, index++));
		}
		kdTree.create(list);
		System.out.println(kdTree);
		double[] key = { 8, 2 };
		KDNode<Integer> target = new KDNode<Integer>(key, 0);
		index = 0;
		for (double[] ds : keys) {
			System.out.println(index++
					+ " "
					+ Math.sqrt(Math.pow(ds[0] - key[0], 2)
							+ Math.pow(ds[1] - key[1], 2)));
		}
		List<Integer> result = kdTree.nearest(key, 2);
		System.out.println(result);
	}
}
