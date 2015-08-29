package ransac;

import java.util.Random;

public class Ransac {
	static Random random = new Random(0);

	static double[] ransac2(double[][] sample) {
		return null;
	}

	static double[][] ransac3(double[][] sample) {

		double p1 = 0.3;
		double p2 = 0.3;
		double threshold = 5;
		int iterCount = 10;

		double[][] result = new double[2][3];

		while (iterCount > 0) {
			int[] state = new int[sample.length];

			int trainCount = 0;
			for (int i = 0; i < state.length; i++) {

				if (p1 < random.nextDouble()) {
					state[i] = 1;
					trainCount++;
				} else {
					state[i] = 0;
				}
			}

			double[][] trainSample = new double[trainCount][4];
			trainCount = 0;
			for (int i = 0; i < sample.length; i++) {
				if (state[i] == 1) {
					trainSample[trainCount] = sample[i];
					trainCount++;
				}
			}

		}
		return null;
	}
}
