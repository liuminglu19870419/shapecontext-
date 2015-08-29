package keyrecon;

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
}
