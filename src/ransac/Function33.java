package ransac;

public class Function33 {

	public static double[] fun33(double[][] a, double[] b) {
		double[] result = new double[3];
		double m = a[0][0] * a[1][1] * a[2][2] + a[2][0] * a[0][1] * a[1][2]
				+ a[1][0] * a[2][1] * a[0][2] - a[2][0] * a[1][1] * a[0][2]
				- a[1][0] * a[0][1] * a[2][2] - a[0][0] * a[2][1] * a[1][2];
		result[0] = b[0] * a[1][1] * a[2][2] + b[2] * a[0][1] * a[1][2] + b[1]
				* a[2][1] * a[0][2] - b[2] * a[1][1] * a[0][2] - b[1] * a[0][1]
				* a[2][2] - b[0] * a[2][1] * a[1][2];
		result[1] = a[0][0] * b[1] * a[2][2] + a[2][0] * b[0] * a[1][2]
				+ a[1][0] * b[2] * a[0][2] - a[2][0] * b[1] * a[0][2] - a[1][0]
				* b[0] * a[2][2] - a[0][0] * b[2] * a[1][2];
		result[2] = a[0][0] * a[1][1] * b[2] + a[2][0] * a[0][1] * b[1]
				+ a[1][0] * a[2][1] * b[0] - a[2][0] * a[1][1] * b[0] - a[1][0]
				* a[0][1] * b[2] - a[0][0] * a[2][1] * b[1];
		result[0] /= m;
		result[1] /= m;
		result[2] /= m;
		return result;
	}

	public static void main(String[] args) {
		double[][] a = { { 1, 2, 1 }, { 2, -1, 3 }, { 3, 1, 2 } };
		double[] b = { 7, 7, 18 };
		double[] result = fun33(a, b);
		for (double d : result) {
			System.out.println(d);
		}

	}
}
