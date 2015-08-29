package ransac;

public class Function22 {
	public static double[] func22(double[][] a, double[] b) {
		double[] result = new double[2];
		result[1] = a[0][0] * b[1] - a[1][0] * b[0];
		result[0] = a[1][1] * b[0] - a[0][1] * b[1];
		double m = a[0][0] * a[1][1] - a[1][0] * a[0][1];
		result[0] /= m;
		result[1] /= m;
		return result;
	}

	public static void main(String[] args) {
		double[][] a = { { 1, -1 }, { 3, -8 } };
		double[] b = { 3, 4 };
		double[] result = func22(a, b);
		System.out.println(result[0] + " " + result[1]);
	}
}
