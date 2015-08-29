package sc;

public class Direction {
	final static double eps = 0.0000000000001;

	public static double[][] theta(double[][] X) {
		double[][] theta = new double[X.length][X.length];
		for (int i = 0; i < theta.length; i++) {
			for (int j = 0; j < theta.length; j++) {
				theta[i][j] = Math.atan2(X[j][1] - X[i][1], X[j][0] - X[i][0])
						+ Math.PI;
			}
		}

		return theta;
	}
}
