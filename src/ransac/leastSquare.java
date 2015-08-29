package ransac;

public class leastSquare {
	public static double[] leastSquare2(double[][] sample) {

		double[] result;
		double[][] a = new double[2][2];
		double[] b = new double[2];

		a[0][0] = E.ex2(sample, 0);
		a[0][1] = E.ex(sample, 0);
		a[1][0] = E.ex(sample, 0);
		a[1][1] = sample.length;
		b[0] = E.exy(sample, 0, 1);
		b[1] = E.ex(sample, 1);

		result = Function22.func22(a, b);
		return result;
	}

	public static double[] leastSquare3(double[][] sample) {

		double[] result;
		double[][] a = new double[3][3];
		double[] b = new double[3];

		a[0][0] = E.ex2(sample, 0);
		a[0][1] = E.exy(sample, 0, 1);
		a[0][2] = E.ex(sample, 0);
		a[1][0] = a[0][1];
		a[1][1] = E.ex2(sample, 1);
		a[1][2] = E.ex(sample, 1);

		a[2][0] = a[0][2];
		a[2][1] = a[1][2];
		a[2][2] = sample.length;

		b[0] = E.exy(sample, 2, 0);
		b[1] = E.exy(sample, 2, 1);
		b[2] = E.ex(sample, 2);

		result = Function33.fun33(a, b);
		
		return result;
	}
}
