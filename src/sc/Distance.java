package sc;

import sc.SC_Compute.PointFeature;

public class Distance {

	/**
	 * calc distance of x[i] with x[0]->x[n]
	 * 
	 * @param x
	 *            2 dim point (x, y)
	 * @return distance matrix of n * n
	 */
	public static double[][] dist2(double[][] x) {
		double[][] dist = new double[x.length][x.length];
		for (int i = 0; i < dist.length; i++) {
			for (int j = 0; j < dist.length; j++) {
				dist[i][j] = Math.pow(x[i][0] - x[j][0], 2)
						+ Math.pow(x[i][1] - x[j][1], 2);
			}
		}
		return dist;
	}

	public static double[][] dist2(double[][] a, double[][] b) {
		double[][] result = new double[b.length][a.length];
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < a.length; j++) {
				result[i][j] = Math.pow(a[j][0] - b[i][0], 2)
						+ Math.pow(a[j][1] - b[i][1], 2);
			}
		}
		return result;
	}

	public static double[][] dist2(PointFeature[] x1, PointFeature[] x2) {
		double[][] distance = new double[x1.length][x2.length];
		for (int i = 0; i < x1.length; i++) {
			for (int j = 0; j < x2.length; j++) {
				distance[i][j] = dist(x1[i], x2[j]);
			}

		}
		return distance;
	}

	static double eps = 0.00000000001;

	public static double dist(PointFeature x1, PointFeature x2) {
		double dist = 0;

		for (int i = 0; i < x1.feature.length; i++) {
			dist += Math.abs(x1.feature[i] - x2.feature[i])
					/ (x1.feature[i] + x2.feature[i] + eps);
		}

		return dist;
	}

	public static double mean(double[][] dist) {
		double meanDistance = 0;
		for (int i = 0; i < dist.length; i++) {
			for (int j = 0; j < dist[0].length; j++) {
				meanDistance += dist[i][j];
			}
		}

		meanDistance = meanDistance / (dist.length * dist[0].length);
		return meanDistance;
	}

}
