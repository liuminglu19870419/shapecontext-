package keyrecon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.Map;

public class Main {
	public static void testFilter(String inString, String outString) {

		BufferedImage bufferedImage = LoadImage.loadImage(inString);
		int[][] rgbImage = PreProcess.toRGB(bufferedImage);
		rgbImage = PreProcess.noiseFilter(rgbImage, 1);
		try {
			DrawImage.drawGreyImage(outString, rgbImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testLinkeSection(String inString, String outString) {

		BufferedImage bufferedImage = LoadImage.loadImage(inString);

		File file = new File(inString);
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
		Map<Integer, int[][]> result = LinkSection.getLinkedSections(rgbImage,
				PreProcess.bgColor);
		if (result == null)
			return;
		try {
			int index = 0;
			for (Integer i : result.keySet()) {
				String tempString = dirStrings[index] + "/" + dirStrings[index]
						+ "_" + outString;
				DrawImage.drawGreyImage(tempString, result.get(i));
				index++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String pathString = "/home/minglu/yaohao/";
		String outPathString = "./";
		File rootFile = new File(pathString);
		File[] files = rootFile.listFiles();
		// testFilter(pathString + "3ljk.png", "3ljk.png");
		testLinkeSection(pathString + "kmhw.png", "kmhw.png");
		// testLinkeSection(pathString + "3ctk.png", "3ctk.png");
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
			// testFilter(file.getAbsolutePath(), file.getName());
			testLinkeSection(file.getAbsolutePath(), file.getName());
		}
	}
}
