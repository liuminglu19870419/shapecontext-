package ransac;

public class E {

	public static double ex(double[][] points, int index) {
		double result = 0;
		for (int i = 0; i < points.length; i++) {
			result += points[i][index];
		}
		return result;
	}

	public static double exy(double[][] points, int index1, int index2) {
		double result = 0;
		for (int i = 0; i < points.length; i++) {
			result += points[i][index1] * points[i][index2];
		}
		return result;
	}
	
	public static double ex2(double[][] points, int index) {
		double result = 0;
		for (int i = 0; i < points.length; i++) {
			result += points[i][index] * points[i][index];
		}
		return result;
	}
}
