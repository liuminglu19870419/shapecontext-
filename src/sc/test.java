package sc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import sc.SC_Compute.PointFeature;
import sc.hungarian.Hungarian;
import sc.imageprocess.GetEdge;
import sc.keyrecon.DrawImage;
import sc.keyrecon.LoadImage;
import sc.tps.TPS;

public class test {
	public static void main(String[] args) throws IOException {
		int[][] origImage = LoadImage.loadImagetoInt("orig.bmp");
		// int[][] transImage = LoadImage.loadImagetoInt("trans.bmp");
		int[][] transImage = LoadImage.loadImagetoInt("trans2.bmp");

		// int[][] origImage = LoadImage.loadImagetoInt("5.png");
		// int[][] transImage = LoadImage.loadImagetoInt("52.png");
		int[][] origEdge = new GetEdge().edge(origImage);
		int[][] transEdge = new GetEdge().edge(transImage);
		SC_Compute origCompute = new SC_Compute();
		PointFeature[] origFeatures = origCompute.scCompute(origEdge, 5);
		PointFeature[] transFeatures = origCompute.scCompute(transEdge, 5);
		double[][] distance = Distance.dist2(origFeatures, transFeatures);
		double meanDistance = Distance.mean(distance);

		int size = Math.max(distance.length, distance[0].length);
		// System.out.println(size);
		double[][] resizeDistance = new double[size + size / 4][size + size / 4];
		// double[][] resizeDistance = new double[size][size];
		for (int i = 0; i < resizeDistance.length; i++) {
			for (int j = 0; j < resizeDistance[0].length; j++) {
				if (i < distance.length && j < distance[0].length) {
					resizeDistance[i][j] = distance[i][j];
				} else {
					resizeDistance[i][j] = meanDistance;
				}
			}
		}

		Hungarian hungarian = new Hungarian(resizeDistance);
		hungarian.algrm();
		int[][] match = hungarian.Match;
		ArrayList<int[]> pointPairArrayList = new ArrayList<int[]>();
		for (int i = 0; i < origFeatures.length; i++) {
			for (int j = 0; j < transFeatures.length; j++) {
				if (match[i][j] == 1) {
					pointPairArrayList.add(new int[] { i, j });
				}
			}
		}

		System.out.println(pointPairArrayList.size());
		TPS tps = new TPS();
		double[][] x = new double[pointPairArrayList.size()][2];
		double[][] y = new double[pointPairArrayList.size()][2];

		for (int i = 0; i < pointPairArrayList.size(); i++) {
			x[i][0] = origFeatures[pointPairArrayList.get(i)[0]].x;
			x[i][1] = origFeatures[pointPairArrayList.get(i)[0]].y;

			y[i][0] = transFeatures[pointPairArrayList.get(i)[1]].x;
			y[i][1] = transFeatures[pointPairArrayList.get(i)[1]].y;
		}
		tps.bookStein(x, y, 1000);
		int[][] tpsImage = new int[x.length][2];
		for (int i = 0; i < tps.Z.length; i++) {
			tpsImage[i][0] = (int) tps.Z[i][0];
			tpsImage[i][1] = (int) tps.Z[i][1];
		}

		// BufferedImage bufferedImage = DrawImage.drawEdgeImage(origEdge,
		// Color.BLUE);

		BufferedImage bufferedImage = DrawImage.drawEdgeImage(tpsImage,
				Color.BLUE);
		// DrawImage.drawEdgeImage(bufferedImage, tpsImage, Color.GREEN);
		DrawImage.drawEdgeImage("out.png", bufferedImage, transEdge, Color.red);

	}
}
