package sc.keyrecon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import sc.keyrecon.LinkSection.Pair;

public class SeperateImage {
	static public ArrayList<Pair> dijkstraPath(int image[][], int bgRGB) {
		int pointSet[] = new int[image.length * image[0].length];
		int map[][] = new int[pointSet.length][pointSet.length];
		int path[][] = new int[map.length][map.length];
		int dist[][] = new int[pointSet.length][pointSet.length];

		int nan = 99999;
		int min = 1;
		int max = 10;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				if (i != j) {
					int xi, yi;
					yi = i / image[0].length;
					xi = i - yi * image[0].length;
					int xj, yj;
					yj = j / image[0].length;
					xj = j - yj * image[0].length;

					if (yi == yj && xi - xj == 1) {
						map[i][j] = image[yj][xj] == bgRGB ? min : max;
					} else if (yi == yj && xj - xi == 1) {
						map[i][j] = image[yj][xj] == bgRGB ? min : max;
					} else if (yi - yj == 1 && xi == xj) {
						map[i][j] = image[yj][xj] == bgRGB ? min : max;
					} else if (yi - yj == 1 && xi - xj == 1) {
						map[i][j] = image[yj][xj] == bgRGB ? min : max;
					} else if (yi - yj == 1 && xj - xi == 1) {
						map[i][j] = image[yj][xj] == bgRGB ? min : max;
					} else {
						map[i][j] = nan;
					}

				} // if(i != j)
				else {
					map[i][j] = 0;
				}
			}
		}

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				dist[i][j] = map[i][j];
				path[i][j] = 0;
			}
		}

		for (int k = 1; k < map.length; k++) {
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map.length; j++) {
					if (dist[i][k] + dist[k][j] < dist[i][j]) {
						{
							dist[i][j] = dist[i][k] + dist[k][j];
							path[i][j] = k;
						}
					}
				}
			}
		} // for (int k = 1; k < map.length; k++) {

		int desIndex = 0;
		int srcIndex = 0;
		int minPath = nan;
		for (int j = 0; j < image[0].length; j++) {
			for (int i = 0; i < image[0].length; i++) {
				if (dist[dist.length - j - 1][i] < minPath) {
					desIndex = i;
					srcIndex = dist.length - j - 1;
					minPath = dist[dist.length - j - 1][i];
				}
			}
		}

		ArrayList<Pair> pathArrayList = new ArrayList<Pair>();
		output(srcIndex, desIndex, pathArrayList, path, image[0].length);
		return pathArrayList;
	}

	static void output(int i, int j, ArrayList<Pair> pathArrayList,
			int path[][], int width) {
		if (i == j) {
			return;
		}
		if (path[i][j] == 0) {
			int xj, yj;
			yj = j / width;
			xj = j - yj * width;
			Pair pair = new Pair(xj, yj, 0, 0);
			pathArrayList.add(pair);
		} else {
			output(i, path[i][j], pathArrayList, path, width);
			output(path[i][j], j, pathArrayList, path, width);
		}
	}

	static public void drawSeperateLine(ArrayList<Pair> path,
			BufferedImage bufferedImage, int xOffset) {
		Graphics2D graphics2d = bufferedImage.createGraphics();
		graphics2d.setColor(Color.RED);
		for (int i = 1; i < path.size(); i++) {
			graphics2d.drawLine(path.get(i).start + xOffset, path.get(i).end,
					path.get(i - 1).start + xOffset, path.get(i - 1).end);
		}
	}
}
