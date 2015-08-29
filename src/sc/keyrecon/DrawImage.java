package sc.keyrecon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DrawImage {

	static int scale = 10;

	public static void drawGreyImage(final String filename,
			final int[][] greyImage) throws IOException {

		BufferedImage image = new BufferedImage(greyImage[0].length * scale,
				greyImage.length * scale, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < greyImage[0].length; x++) {
			for (int y = 0; y < greyImage.length; y++) {
				image.setRGB(x * scale, y * scale, greyImage[y][x]);
			}
		}
		File file = new File(filename);
		ImageIO.write(image, "png", file);
	}

	public static void drawEdgeImage(final String filename,
			final int[][] imageXY, Color color) throws IOException {

		int maxX = 0;
		int maxY = 0;
		for (int i = 0; i < imageXY.length; i++) {
			if (imageXY[i][0] > maxX) {
				maxX = imageXY[i][0];
			}
			if (imageXY[i][1] > maxY) {
				maxX = imageXY[i][0];
			}
		}
		BufferedImage image = new BufferedImage(maxX * scale, maxY * scale,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageXY.length; x++) {
			image.setRGB(imageXY[x][0] * scale, imageXY[x][1] * scale, color.getRGB());
		}
		File file = new File(filename);
		ImageIO.write(image, "png", file);
	}

	public static BufferedImage drawEdgeImage(final int[][] imageXY, Color color)
			throws IOException {

		int maxX = 0;
		int maxY = 0;
		for (int i = 0; i < imageXY.length; i++) {
			if (imageXY[i][0] > maxX) {
				maxX = imageXY[i][0];
			}
			if (imageXY[i][1] > maxY) {
				maxY = imageXY[i][1];
			}
		}
		maxX += 10;
		maxY += 10;

		BufferedImage image = new BufferedImage(maxX * scale , maxY * scale,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageXY.length; x++) {
			image.setRGB(imageXY[x][0] * scale, imageXY[x][1] * scale, color.getRGB());
		}

		return image;
	}

	public static void drawEdgeImage(final String filename,
			BufferedImage image, final int[][] imageXY, Color color)
			throws IOException {

		for (int x = 0; x < imageXY.length; x++) {
			image.setRGB(imageXY[x][0] * scale, imageXY[x][1] * scale, color.getRGB());
		}
		File file = new File(filename);
		ImageIO.write(image, "png", file);
	}

	public static void drawEdgeImage(BufferedImage image,
			final int[][] imageXY, Color color) throws IOException {
		for (int x = 0; x < imageXY.length; x++) {
			image.setRGB(imageXY[x][0] * scale, imageXY[x][1] * scale,
					color.getRGB());
		}
	}
}
