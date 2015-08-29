package sc.imageprocess;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Rotate {

	public void rotate(String srcPath, String targetPath, double degree) {
		try {
			BufferedImage src = ImageIO.read(new File(srcPath)); // 读入源图像
			int width = src.getWidth(); // 源图宽
			int height = src.getHeight(); // 源图高

			int type = src.getColorModel().getTransparency();
			double fSrcX1, fSrcY1, fSrcX2, fSrcY2, fSrcX3, fSrcY3, fSrcX4, fSrcY4;
			fSrcX1 = (double) (-(width - 1) / 2);
			fSrcY1 = (double) ((height - 1) / 2);
			fSrcX2 = (double) ((width - 1) / 2);
			fSrcY2 = (double) ((height - 1) / 2);
			fSrcX3 = (double) (-(width - 1) / 2);
			fSrcY3 = (double) (-(height - 1) / 2);
			fSrcX4 = (double) ((width - 1) / 2);
			fSrcY4 = (double) (-(height - 1) / 2);

			// 旋转后四个角的坐标（以图像中心为坐标系原点）
			double fDstX1, fDstY1, fDstX2, fDstY2, fDstX3, fDstY3, fDstX4, fDstY4;
			double theta = Math.toRadians(degree);
			fDstX1 = Math.cos(theta) * fSrcX1 + Math.sin(theta) * fSrcY1;
			fDstY1 = -Math.sin(theta) * fSrcX1 + Math.cos(theta) * fSrcY1;
			fDstX2 = Math.cos(theta) * fSrcX2 + Math.sin(theta) * fSrcY2;
			fDstY2 = -Math.sin(theta) * fSrcX2 + Math.cos(theta) * fSrcY2;
			fDstX3 = Math.cos(theta) * fSrcX3 + Math.sin(theta) * fSrcY3;
			fDstY3 = -Math.sin(theta) * fSrcX3 + Math.cos(theta) * fSrcY3;
			fDstX4 = Math.cos(theta) * fSrcX4 + Math.sin(theta) * fSrcY4;
			fDstY4 = -Math.sin(theta) * fSrcX4 + Math.cos(theta) * fSrcY4;
			int newWidth = (int) (Math.max(Math.abs(fDstX4 - fDstX1),
					Math.abs(fDstX3 - fDstX2)) + 0.5);
			int newHeight = (int) (Math.max(Math.abs(fDstY4 - fDstY1),
					Math.abs(fDstY3 - fDstY2)) + 0.5);
			BufferedImage tag = new BufferedImage(newWidth, newHeight, type);
			double dx = -0.5 * newWidth * Math.cos(theta) - 0.5 * newHeight
					* Math.sin(theta) + 0.5 * width;
			double dy = 0.5 * newWidth * Math.sin(theta) - 0.5 * newHeight
					* Math.cos(theta) + 0.5 * height;

			int x, y;
			for (int i = 0; i < newHeight; i++) {
				for (int j = 0; j < newWidth; j++) {
					x = (int) ((double) (j) * Math.cos(theta) + (double) (i)
							* Math.sin(theta) + dx);
					y = (int) ((double) (-j) * Math.sin(theta) + (double) (i)
							* Math.cos(theta) + dy);
					if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
						tag.setRGB(j, i, src.getRGB(0, 0));
					} else {
						tag.setRGB(j, i, src.getRGB(x, y));
					}
				}
			}
			File file = new File(targetPath);
			ImageIO.write(tag, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("图像缩放--ImageDone");
		new Rotate().rotate("4ran.png", "4ran_s.png", 60);
	}
}
