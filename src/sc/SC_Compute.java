package sc;

import Jama.Matrix;

public class SC_Compute {

	int thetaM = 12;
	int rN = 3;

	public static class PointFeature {
		int x, y;
		double[] feature;

		public PointFeature() {
			// TODO Auto-generated constructor stub
		}

		public PointFeature(int x, int y) {
			// TODO Auto-generated constructor stub
			this.x = x;
			this.y = y;
		}

		public PointFeature(int count) {
			// TODO Auto-generated constructor stub
			feature = new double[count];
		}

		public PointFeature(int x, int y, int count) {
			this(x, y);
			feature = new double[count];
		}
	}

	/**
	 * @param image_xy
	 *            boundry of image, feature points
	 * @param image
	 *            raw image
	 * @param r_inner
	 */
	PointFeature[] scCompute(int[][] image, int[][] image_xy, double r_outer) {
		double[][] theta = calcTheta(image);
		double theta_scale = Math.PI * 2 / thetaM;
		for (int i = 0; i < theta.length; i++) {
			for (int j = 0; j < theta[0].length; j++) {
				theta[i][j] = (int) (theta[i][j] / theta_scale);
			}
		}

		Matrix thetaMatrix = new Matrix(theta);
		thetaMatrix.print(2, 2);

		double[] thetaFeaturePoint = new double[image_xy.length];
		double[][] scFeature = new double[image_xy.length][thetaM * rN];

		double r_outer_log = Math.log10(r_outer);
		double scale = r_outer_log / rN;
		double[] rArray = new double[rN];
		for (int i = 0; i < rArray.length; i++) {
			rArray[i] = Math.pow(10, scale * (i + 1));
			System.out.println(rArray[i]);
		}

		PointFeature[] features = new PointFeature[image_xy.length];
		for (int i = 0; i < image_xy.length; i++) {
			features[i] = new PointFeature();
			features[i].feature = new double[rN * thetaM];
			for (int j = 0; j < features[i].feature.length; j++) {
				features[i].feature[j] = 0;
			}

			int x = image_xy[i][0];
			int y = image_xy[i][1];
			int xStart = (int) (image_xy[i][0] - r_outer < 0 ? 0
					: image_xy[i][0] - r_outer);
			int xEnd = (int) (image_xy[i][0] + r_outer > image[0].length - 1 ? image[0].length - 1
					: image_xy[i][0] + r_outer);

			int yStart = (int) (image_xy[i][1] - r_outer < 0 ? 0
					: image_xy[i][1] - r_outer);
			int yEnd = (int) (image_xy[i][1] + r_outer > image.length - 1 ? image.length - 1
					: image_xy[i][1] + r_outer);

			for (int j = yStart; j <= yEnd; j++) {
				for (int j2 = xStart; j2 < xStart; j2++) {
					int rIndex = -1;
					double distance = Math.sqrt(Math.pow(j2 - x, 2)
							+ Math.pow(j - y, 2));
					for (int k = 0; k < rN; k++) {
						if (distance < rArray[k]) {
							rIndex = k;
							break;
						}
					}

					if (rIndex < 0) {
						continue;
					}

					double thetaCurrent = Math.atan2(j2 - y, j - x);
					if (thetaCurrent < 0) {
						thetaCurrent += Math.PI;
					}
					thetaCurrent = (int) (thetaCurrent / theta_scale);
					features[i].feature[(int) (thetaCurrent + thetaM * rIndex)] += 1;

				}
			}
			double total = 0;
			for (int j = 0; j < features[i].feature.length; j++) {
				total += features[i].feature[j];
			}

			for (int j = 0; j < features[i].feature.length; j++) {
				features[i].feature[j] /= total;
			}
		}

		return features;
	};

	final static double eps = 0.000000000001;

	/**
	 * @param image_xy
	 *            boundry of image, feature points
	 * @param image
	 *            raw image
	 * @param r_inner
	 */
	PointFeature[] scCompute(int[][] image_xy, double r_outer) {

		double[][] xy = new double[image_xy.length][2];
		for (int i = 0; i < image_xy.length; i++) {
			xy[i][0] = image_xy[i][0];
			xy[i][1] = image_xy[i][1];
		}

		double[][] distance = Distance.dist2(xy);

		for (int i = 0; i < distance.length; i++) {
			for (int j = 0; j < distance.length; j++) {
				distance[i][j] = Math.sqrt(distance[i][j]);
			}
		}

		double[][] theta = theta2(image_xy);
		double thetaScale = 2 * Math.PI / this.thetaM;
		for (int i = 0; i < theta.length; i++) {
			for (int j = 0; j < theta.length; j++) {
				theta[i][j] = (int) (theta[i][j] / thetaScale);
				if (theta[i][j] == thetaM) {
					theta[i][j] = 0;
				}
			}
		}
		// Matrix thetaMatrix = new Matrix(theta);
		// thetaMatrix.print(4, 4);

		double r_outer_log = Math.log10(r_outer);
		double scale = r_outer_log / rN;
		double[] rArray = new double[rN];
		for (int i = 0; i < rArray.length; i++) {
			rArray[i] = Math.pow(10, scale * (i + 1));
		}

		PointFeature[] features = new PointFeature[image_xy.length];
		for (int i = 0; i < image_xy.length; i++) {
			features[i] = new PointFeature(image_xy[i][0], image_xy[i][1], rN
					* thetaM);
			// features[i].feature = new double[rN * thetaM];
			for (int j = 0; j < features[i].feature.length; j++) {
				features[i].feature[j] = 0;
			}

			for (int j = 0; j < features.length; j++) {

				if (j == i) {
					continue;
				}

				int rIndex = -1;
				for (int k = 0; k < rN; k++) {
					if (distance[i][j] < rArray[k]) {
						rIndex = k;
						break;
					}
				}

				if (rIndex < 0) {
					continue;
				}
//				System.out.println(theta[i][j] + " " + thetaM + " " + rIndex);

				features[i].feature[(int) (theta[i][j] + thetaM * rIndex)] += 1;
			}

			// gui yi hua
			double total = 0;
			for (int j = 0; j < features[i].feature.length; j++) {
				total += features[i].feature[j];
			}

			for (int j = 0; j < features[i].feature.length; j++) {
				features[i].feature[j] /= total + eps;
			}
		}

		return features;
	};

	double[][] theta2(int[][] image) {

		double theta[][] = new double[image.length][image.length];
		for (int i = 0; i < theta.length; i++) {
			for (int j = 0; j < theta.length; j++) {
				double dx = image[i][0] - image[j][0] + eps;
				double dy = image[i][1] - image[j][1];

				theta[i][j] = Math.atan2(dy, dx) + Math.PI;
			}
		}
		return theta;
	}

	double[][] calcTheta(int[][] image) {
		double theta[][] = new double[image.length][image.length];
		for (int i = 0; i < theta.length; i++) {
			for (int j = 0; j < theta[0].length; j++) {
				int x, y, xr, xl, yu, yd;
				x = j;
				y = i;
				xr = j + 1 >= image[0].length ? image[0].length - 1 : j + 1;
				xl = j - 1 < 0 ? 0 : j - 1;
				yd = i + 1 >= image.length ? image.length - 1 : i + 1;
				yu = i - 1 < 0 ? 0 : i - 1;
				double z1, z2, z3;
				double z4, z5, z6;
				double z7, z8, z9;
				z1 = image[yu][xl];
				z2 = image[yu][x];
				z3 = image[yu][xr];

				z4 = image[y][xl];
				z5 = image[y][x];
				z6 = image[y][xr];

				z7 = image[yd][xl];
				z8 = image[yd][x];
				z9 = image[yd][xr];

				double dx = (z1 + 2 * z2 + z3) - (z7 + 2 * z8 + z9) + eps;
				double dy = (z1 + 2 * z4 + z7) - (z3 + 2 * z6 + z9);

				theta[i][j] = Math.atan2(dy, dx) + Math.PI;
			}
		}

		return theta;
	}
}
