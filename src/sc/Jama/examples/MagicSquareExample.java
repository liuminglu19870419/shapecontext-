package sc.Jama.examples;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class MagicSquareExample {

	/** Generate magic square test matrix. **/

	public static Matrix magic(int n) {

		double[][] M = new double[n][n];

		// Odd order

		if ((n % 2) == 1) {
			int a = (n + 1) / 2;
			int b = (n + 1);
			for (int j = 0; j < n; j++) {
				for (int i = 0; i < n; i++) {
					M[i][j] = n * ((i + j + a) % n) + ((i + 2 * j + b) % n) + 1;
				}
			}

			// Doubly Even Order

		} else if ((n % 4) == 0) {
			for (int j = 0; j < n; j++) {
				for (int i = 0; i < n; i++) {
					if (((i + 1) / 2) % 2 == ((j + 1) / 2) % 2) {
						M[i][j] = n * n - n * i - j;
					} else {
						M[i][j] = n * i + j + 1;
					}
				}
			}

			// Singly Even Order

		} else {
			int p = n / 2;
			int k = (n - 2) / 4;
			Matrix A = magic(p);
			for (int j = 0; j < p; j++) {
				for (int i = 0; i < p; i++) {
					double aij = A.get(i, j);
					M[i][j] = aij;
					M[i][j + p] = aij + 2 * p * p;
					M[i + p][j] = aij + 3 * p * p;
					M[i + p][j + p] = aij + p * p;
				}
			}
			for (int i = 0; i < p; i++) {
				for (int j = 0; j < k; j++) {
					double t = M[i][j];
					M[i][j] = M[i + p][j];
					M[i + p][j] = t;
				}
				for (int j = n - k + 1; j < n; j++) {
					double t = M[i][j];
					M[i][j] = M[i + p][j];
					M[i + p][j] = t;
				}
			}
			double t = M[k][0];
			M[k][0] = M[k + p][0];
			M[k + p][0] = t;
			t = M[k][k];
			M[k][k] = M[k + p][k];
			M[k + p][k] = t;
		}
		return new Matrix(M);
	}

	/** Shorten spelling of print. **/

	private static void print(String s) {
		System.out.print(s);
	}

	/** Format double with Fw.d. **/

	public static String fixedWidthDoubletoString(double x, int w, int d) {
		java.text.DecimalFormat fmt = new java.text.DecimalFormat();
		fmt.setMaximumFractionDigits(d);
		fmt.setMinimumFractionDigits(d);
		fmt.setGroupingUsed(false);
		String s = fmt.format(x);
		while (s.length() < w) {
			s = " " + s;
		}
		return s;
	}

	/** Format integer with Iw. **/

	public static String fixedWidthIntegertoString(int n, int w) {
		String s = Integer.toString(n);
		while (s.length() < w) {
			s = " " + s;
		}
		return s;
	}

	public static void main(String argv[]) {

		/*
		 * | Tests LU, QR, SVD and symmetric Eig decompositions. | | n = order
		 * of magic square. | trace = diagonal sum, should be the magic sum,
		 * (n^3 + n)/2. | max_eig = maximum eigenvalue of (A + A')/2, should
		 * equal trace. | rank = linear algebraic rank, | should equal n if n is
		 * odd, be less than n if n is even. | cond = L_2 condition number,
		 * ratio of singular values. | lu_res = test of LU factorization,
		 * norm1(L*U-A(p,:))/(n*eps). | qr_res = test of QR factorization,
		 * norm1(Q*R-A)/(n*eps).
		 */
		double[][] matrix = { { 1, 2, 3 }, { 2, 2, 1 }, { 3, 4, 3 } };
		Matrix matrix2 = new Matrix(matrix);
		Matrix inv = matrix2.inverse();
		inv.print(0, 10);
		double[][] matrix3 ={{1,5,9}, {2,6,10}, {3,7,11}, {4,8,12}};
		Matrix matrix4 = new Matrix(matrix3);
		SingularValueDecomposition svd = matrix4.svd();
		Matrix sMatrix = svd.getS();
		sMatrix.print(10, 10);
		
		//function
		double[][] xishu = {{1,1,1}, {1,2,3}, {1,10,100}};
		Matrix xishuMatrix = new Matrix(xishu);
		double[][] right = {{3,4}, {6,10}, {111,2}};
		Matrix rightMatrix = new Matrix(right);
		xishuMatrix.solve(rightMatrix).getMatrix(0, right.length - 1, 0, 0).print(10, 10);
		
		Matrix c1 = rightMatrix.getMatrix(0, right.length - 1, 0, 0);
		c1.print(10, 10);
		Matrix c2 = c1.transpose();
		c2.print(10, 10);
		Matrix K = xishuMatrix;
		K.print(10, 10);
		Matrix Q = c2.times(K).times(c1);
		Q.print(10, 10);
	}

}