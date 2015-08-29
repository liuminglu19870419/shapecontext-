package sc.keyrecon;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class PreProcess {

	public static int[][] toRGB(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int[][] greyImage = new int[height][width];
		for (int x = 0; x < bufferedImage.getWidth(); x++) {
			for (int y = 0; y < bufferedImage.getHeight(); y++) {
				greyImage[y][x] = bufferedImage.getRGB(x, y);
			}
		}// for (int x = 0; x < bufferedImage.getWidth(); x++) {
		return greyImage;
	}

	public static int bgColor = 0;

	/**
	 * calc the background golor and the character color, then filter
	 */
	public static int[][] noiseFilter(final int[][] bitImage, int radius) {
		int[][] resultImage = new int[bitImage.length][bitImage[0].length];
		Map<Integer, Integer> colorStaticMap = new HashMap<Integer, Integer>();

		for (int x = 0; x < bitImage[0].length; x++) {
			for (int y = 0; y < bitImage.length; y++) {
				Integer colorInteger = bitImage[y][x];
				if (colorStaticMap.containsKey(colorInteger)) {
					colorStaticMap.put(colorInteger,
							colorStaticMap.get(colorInteger) + 1);
				} else {
					colorStaticMap.put(colorInteger, 1);
				}
			}
		}

		int max1 = 0, max1Color = 0, max2 = 0, max2Color = 0;
		for (Integer integer : colorStaticMap.keySet()) {
			if (colorStaticMap.get(integer) > max1) {
				max1 = colorStaticMap.get(integer);
				max1Color = integer;
			} else if (colorStaticMap.get(integer) > max2) {
				max2 = colorStaticMap.get(integer);
				max2Color = integer;
			}
		}
		// System.out.println(max1 + " " + colorStaticMap.get(max1));
		// System.out.println(max2 + " " + colorStaticMap.get(max2));
		bgColor = max1Color;
		int threshold = 3;
		for (int x = 0; x < bitImage[0].length; x++) {
			for (int y = 0; y < bitImage.length; y++) {
				if (bitImage[y][x] == max2Color) {
					resultImage[y][x] = max2Color;
					continue;
				}
				if(bitImage[y][x] == max1Color) {
					resultImage[y][x] = max1Color;
					continue;
				}
				resultImage[y][x] = max1Color;
				int count = 0;
				for (int i = x - radius < 0 ? 0 : x - radius; i <= ((x + radius >= bitImage[0].length) ? bitImage[0].length - 1
						: x + radius); i++) {
					for (int j = y - radius < 0 ? 0 : y - radius; j <= ((y
							+ radius >= bitImage.length) ? bitImage.length - 1
								: y + radius); j++) {
						if (i == x && j == y) {
							continue;
						}
						if (bitImage[j][i] == max2Color) {
							count++;
						}
						if (count > threshold) {
							resultImage[y][x] = max2Color;
							break;
						}
					}
					if (count > threshold) {
						break;
					}
				}
			}
		}
		return resultImage;
	}

	public static double[][] toGrey(final BufferedImage bufferedImage) {
		double w1 = 0.299, w2 = 0.587, w3 = 0.114;
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		double[][] greyImage = new double[height][width];
		for (int x = 0; x < bufferedImage.getWidth(); x++) {
			for (int y = 0; y < bufferedImage.getHeight(); y++) {
				int rgb = bufferedImage.getRGB(x, y);
				int red = (rgb >> 16) & 0xFF;
				int blue = (rgb >> 0) & 0xFF;
				int green = (rgb >> 8) & 0xFF;
				greyImage[y][x] = (double) (w1 * red + w2 * green + w3 * blue) / 3;
			}
		}// for (int x = 0; x < bufferedImage.getWidth(); x++) {
		return greyImage;
	}

}
