package sc.imageprocess;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import sc.keyrecon.LinkSection;
import sc.keyrecon.LoadImage;
import sc.keyrecon.PreProcess;
import sc.keyrecon.LinkSection.Pair;

public class GetEdge {
	private boolean isEdge(int[][] image, int i, int j, double bgRGB) {

		boolean haveBg = false;
		boolean haveChar = false;
		int bg = 0;

		for (int x = i - 1; x < i + 2; x++) {
			for (int y = j - 1; y < j + 2; y++) {
				if (x < 0 || x >= image[0].length) {
					bg++;
					continue;
				}

				if (y < 0 || y >= image.length) {
					// haveBg = true;
					bg++;
					continue;
				}

				if (x == i && y == j) {
					continue;
				}

				if (image[y][x] == bgRGB) {
					bg++;
					// haveBg = true;
				}
				if (image[y][x] != bgRGB) {
					haveChar = true;
				}
			}
		}

		if (bg > 2)
			haveBg = true;
		return haveBg && haveChar;

	}

	public int[][] edge(int[][] rgbImage) {
		double bgRGB = rgbImage[0][0];
		int[][] tag = new int[rgbImage.length][rgbImage[0].length];
		int count = 0;
		for (int i = 0; i < rgbImage[0].length; i++) {
			for (int j = 0; j < rgbImage.length; j++) {
				if (rgbImage[j][i] == bgRGB) {
					tag[j][i] = 0;
					continue;
				}
				if (isEdge(rgbImage, i, j, bgRGB)) {
					tag[j][i] = 1;
					count++;
				} else {
					tag[j][i] = 0;
				}
			}
		}
		int[][] result = new int[count][2];

		count = 0;
		for (int i = 0; i < rgbImage[0].length; i++) {
			for (int j = 0; j < rgbImage.length; j++) {
				if (tag[j][i] == 1) {
					result[count][0] = j;
					result[count][1] = i;
					count++;
				}
			}
		}
		int scale = 1;
		int[][] resultResample = new int[result.length / scale][2];
		// return result;
		for (int i = 0; i < resultResample.length; i++) {
			resultResample[i] = result[i * scale];
		}
		return resultResample;
	}

	public void edge(String srcPath, String targetPath) throws IOException {
		BufferedImage bufferedImage = LoadImage.loadImage(srcPath);
		File file = new File(srcPath);
		String nameString = file.getName();
		nameString = nameString.substring(0, 4);
		char[] dirChars = nameString.toCharArray();
		String[] dirStrings = new String[4];
		for (int j = 0; j < dirStrings.length; j++) {
			dirStrings[j] = new String();
			dirStrings[j] += dirChars[j];
			File dirFile = new File(dirStrings[j]);
			dirFile.mkdir();
		}

		int[][] rgbImage = PreProcess.toRGB(bufferedImage);
		rgbImage = PreProcess.noiseFilter(rgbImage, 1);
		int bgRGB = rgbImage[0][0];

		ArrayList<ArrayList<Pair>> pairs = new ArrayList<ArrayList<Pair>>();
		ArrayList<Pair> preLine = LinkSection.genRowPair(rgbImage, 0, bgRGB);
		pairs.add(preLine);

		int type = bufferedImage.getColorModel().getTransparency();
		BufferedImage tag = new BufferedImage(bufferedImage.getWidth(),
				bufferedImage.getHeight(), type);

		for (int i = 0; i < rgbImage[0].length; i++) {
			for (int j = 0; j < rgbImage.length; j++) {
				if (rgbImage[j][i] == bgRGB)
					continue;
				if (isEdge(rgbImage, i, j, bgRGB)) {
					tag.setRGB(i, j, Color.blue.getRGB());
				}
			}
		}

		File file1 = new File(targetPath);
		ImageIO.write(tag, "png", file1);

	}

	public static void main(String[] args) throws IOException {
		System.out.println("图像缩放--ImageDone");
		new GetEdge().edge("4ran.png", "4ran_s.png");
	}
}
