package sc.hungarian;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Hungarian {
	double[][] A;
	double[][] orig;
	int[] r_cov;
	int[] c_cov;
	public int[][] Match;
	int Z_r, Z_c;
	int stepNumber;
	int N, M;

	public Hungarian(double[][] A) {
		this.orig = A;
		this.N = A.length;
		this.M = A[0].length;
		if (A[0].length != A.length) {
			int maxLength = Math.max(A.length, A[0].length);
			this.A = new double[maxLength][maxLength];
		} else {
			this.A = new double[A.length][A.length];
		}

		for (int i = 0; i < this.A.length; i++) {
			for (int j = 0; j < this.A.length; j++) {
				if (i >= A.length || j >= A[0].length) {
					this.A[i][j] = Integer.MAX_VALUE;
				} else {
					this.A[i][j] = A[i][j];
				}
			}
		}
		Match = new int[A.length][A.length];
		r_cov = new int[A.length];
		c_cov = new int[A.length];
	}

	// 行列减去最小值
	protected void step1() {
		double colMin[] = new double[A.length];
		for (int i = 0; i < colMin.length; i++) {
			colMin[i] = Integer.MAX_VALUE;
			for (int j = 0; j < colMin.length; j++) {
				if (colMin[i] > A[i][j]) {
					colMin[i] = A[i][j];
				}
			}
		}

		for (int i = 0; i < colMin.length; i++) {
			for (int j = 0; j < colMin.length; j++) {
				A[i][j] -= colMin[i];
			}
		}

		double rowMin[] = new double[A.length];
		for (int i = 0; i < rowMin.length; i++) {
			rowMin[i] = Integer.MAX_VALUE;
			for (int j = 0; j < rowMin.length; j++) {
				if (rowMin[i] > A[j][i]) {
					rowMin[i] = A[j][i];
				}
			}
		}

		for (int i = 0; i < rowMin.length; i++) {
			for (int j = 0; j < rowMin.length; j++) {
				A[j][i] -= rowMin[i];
			}
		}
		stepNumber = 2;
	}

	// 寻找每一行的0值，如果他所在行列均为被标记过，那么标记这个0值，遍历所有行
	protected void step2() {
		c_cov = new int[Match.length];
		r_cov = new int[Match.length];
		zero(c_cov);
		zero(r_cov);

		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A.length; j++) {
				if (A[i][j] == 0 && r_cov[i] == 0 && c_cov[j] == 0) {
					Match[i][j] = 1;
					r_cov[i] = 1;
					c_cov[j] = 1;
				}
			}
		}
		zero(r_cov);
		stepNumber = 3;
	}

	// 判断是否寻找到了A.length个独立的0元素
	protected void step3() {
		c_cov = sum(Match, 1);
		if (sum(c_cov) == A.length) {
			stepNumber = 7;
		} else {
			stepNumber = 4;
		}
	}

	// 寻找一个未被标记的0元素并将其标记为优先0元素, 如果优先0元素所在行没有0元素那么进入步骤5，否则标记所在行，并取消标记所在列
	// 循环进行至没有未被覆盖的0元素留下时，进入步骤6
	protected void step4() {
		boolean flag = true;
		while (flag) {
			int row = -1, col = -1;
			boolean exit_flag = false;
			// 寻找未被标记的0元素
			for (int i = 0; i < A.length; i++) {
				for (int j = 0; j < A.length; j++) {
					if (A[i][j] == 0.0 && r_cov[i] == 0 && c_cov[j] == 0) {
						exit_flag = true;
						row = i;
						col = j;
						break;
					}
				}
				if (exit_flag) {
					break;
				}
			} // for (int i = 0; i < A.length; i++) {

			if (row == -1) {// 没有找到未被使用0元素表示当前系数矩阵需要变形
				stepNumber = 6;
				Z_c = -1;
				Z_r = -1;
				return;
			} else {
				// 标记优先元素
				Match[row][col] = 2;
				if (sum(find(Match[row], 1)) != 0) {
					r_cov[row] = 1;
					List<Integer> zcov = find(Match[row], 1);
					for (Integer integer : zcov) {
						c_cov[integer] = 0;
					}
				} else {
					stepNumber = 5;
					Z_c = col;
					Z_r = row;
					return;
				}
			}
		}
	}

	protected void step5() {
		boolean flag = true;
		int i = 0;
		List<Integer> z_c = new ArrayList<>();
		List<Integer> z_r = new ArrayList<>();
		z_c.add(Z_c);
		z_r.add(Z_r);

		while (flag) {
			int rindex = find(Match, z_c.get(i), 1, 1);
			if (rindex > -1) {
				z_r.add(rindex);
				z_c.add(z_c.get(i));
				i = i + 1;
			} else {
				flag = false;
			}

			if (flag) {
				int cindex = find(Match, z_r.get(i), 2, 0);
				z_r.add(z_r.get(i));
				z_c.add(cindex);
				i = i + 1;

			}
		}
		for (int j = 0; j < z_c.size(); j++) {
			if (Match[z_r.get(j)][z_c.get(j)] == 1) {
				Match[z_r.get(j)][z_c.get(j)] = 0;
			} else {
				Match[z_r.get(j)][z_c.get(j)] = 1;
			}
		}
		zero(c_cov);
		zero(r_cov);
		for (int j = 0; j < A.length; j++) {
			for (int j2 = 0; j2 < A.length; j2++) {
				if (Match[j][j2] == 2) {
					Match[j][j2] = 0;
				}
			}
		}
		stepNumber = 3;
	}

	protected void step6() {
		List<Integer> a = find(r_cov, 0);
		List<Integer> b = find(c_cov, 0);
		double min = Double.MAX_VALUE;
		for (Integer integerr : a) {
			for (Integer integerc : b) {
				if (min > A[integerr][integerc]) {
					min = A[integerr][integerc];
				}
			}
		}

		List<Integer> c = find(r_cov, 1);

		for (Integer integer : c) {
			for (int i = 0; i < A.length; i++) {
				A[integer][i] = A[integer][i] + min;
			}
		}

		for (Integer integer : b) {
			for (int i = 0; i < A.length; i++) {
				A[i][integer] = A[i][integer] - min;
			}
		}

		stepNumber = 4;
	}

	public void algrm() {
		boolean exit_flag = false;
		stepNumber = 1;
		while (!exit_flag) {
			switch (stepNumber) {
			case 1:
				step1();
				// print();
				break;
			case 2:
				step2();
				// print();
				break;
			case 3:
				step3();
				// print();
				break;
			case 4:
				step4();
				break;
			case 5:
				step5();
				break;
			case 6:
				step6();
				break;
			case 7:
				exit_flag = true;
			default:
				break;
			}
		}
		double cost = 0;
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A.length; j++) {
				if (Match[i][j] == 1) {
					cost += orig[i][j];
				}
			}

		}
//		print();
		System.out.println(cost);
	}

	public void print() {
		System.out.println("P_cond");
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A.length; j++) {
				System.out.print(A[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println("M");
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A.length; j++) {
				System.out.print(Match[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println("r_cov");
		for (int j = 0; j < A.length; j++) {
			System.out.print(r_cov[j] + " ");
		}
		System.out.println();
		System.out.println("c_cov");
		for (int j = 0; j < A.length; j++) {
			System.out.print(c_cov[j] + " ");
		}
		System.out.println();
	}

	private void zero(int[] v) {
		for (int i = 0; i < v.length; i++) {
			v[i] = 0;
		}
	}

	// 求和
	private int sum(int[] v) {
		int result = 0;
		for (int i = 0; i < v.length; i++) {
			result += v[i];
		}
		return result;
	}

	// 求和
	private int sum(List<Integer> v) {
		int result = 0;
		for (Integer integer : v) {
			result += 1;
		}
		return result;
	}

	private List<Integer> find(int[] v, int value) {
		List<Integer> result = new LinkedList<>();
		for (int i = 0; i < v.length; i++) {
			if (v[i] == value) {
				result.add(i);
			}
		}
		return result;

	}

	private int[] sum(int[][] v, int r_c) {
		int[] result = new int[v.length];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result.length; j++) {
				if (r_c != 1) {
					result[i] += v[i][j];
				} else {
					result[i] += v[j][i];
				}
			}
		}

		return result;
	}

	public Hungarian() {
		// TODO Auto-generated constructor stub
	}

	private int find(int[][] m, int c_r, int value, int index) {
		int result = -1;
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				if (index == 1) {
					if (m[j][c_r] == value) {
						return j;
					}
				} else {
					if (m[c_r][j] == value) {
						return j;
					}
				}
			}
		}

		return result;
	}

	public void debug_reduce() {
		step1();
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A.length; j++) {
				System.out.print(A[i][j] + " ");
			}
			System.out.println();
		}

		PriorityQueue<Pair> row = new PriorityQueue(new PairCompartor());
		PriorityQueue<Pair> col = new PriorityQueue(new PairCompartor());
		this.rowCowZeroCount(row, col);
		System.out.println(row);
		System.out.println(col);
	}

	static private class Pair {

		private Integer count;
		private Integer index;

		public Pair(int count, int index) {
			this.count = count;
			this.index = index;
		}

		@Override
		public String toString() {
			return index + ":" + count;
		}
	}

	static private class PairCompartor implements Comparator<Pair> {

		@Override
		public int compare(Pair o1, Pair o2) {
			// TODO Auto-generated method stub
			if (o1.count > o2.count) {
				return 1;
			}
			if (o1.count == o2.count) {
				return 0;
			}
			return -1;
		}
	}

	private void rowCowZeroCount(PriorityQueue<Pair> row,
			PriorityQueue<Pair> col) {
		for (int i = 0; i < A.length; i++) {
			Pair pair = new Pair(0, i);
			int count1 = 0;
			for (int j = 0; j < A.length; j++) {
				if (A[i][j] == 0) {
					count1++;
				}
			}
			pair.count = count1;
			row.add(pair);
		}

		for (int i = 0; i < A.length; i++) {
			Pair pair = new Pair(0, i);
			int count1 = 0;
			for (int j = 0; j < A.length; j++) {
				if (A[j][i] == 0) {
					count1++;
				}
			}
			pair.count = count1;
			col.add(pair);
		}
	}
}
