package sc.keyrecon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LoadImage {
	static public BufferedImage loadImage(String filename) {
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(new File(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bufferedImage;
	}

	static public int[][] loadImagetoInt(String filename) {
		BufferedImage bufferedImage = null;
		int[][] image = null;
		try {
			bufferedImage = ImageIO.read(new File(filename));
            image = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
			for (int i = 0; i < bufferedImage.getHeight(); i++) {
				for (int j = 0; j < bufferedImage.getWidth(); j++) {
					image[i][j] = bufferedImage.getRGB(j, i);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}
}
