package svm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;

import keyrecon.LinkSection;
import keyrecon.LoadImage;
import keyrecon.PreProcess;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class Test {
	static int bgRGB = -16777216;

	static private int[] makeFeature(File file) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(file);
		int[] image = new int[bufferedImage.getHeight()
				* bufferedImage.getWidth() - bufferedImage.getHeight()];
		// bgRGB = bufferedImage.getRGB(0, 0);
		for (int i = 0; i < bufferedImage.getHeight(); i++) {
			for (int j = 0; j < bufferedImage.getWidth() - 1; j++) {
				int rgb = bufferedImage.getRGB(j, i);
				if (rgb == bgRGB) {
					image[i * (bufferedImage.getWidth() - 1) + j] = 0;
				} else {
					image[i * (bufferedImage.getWidth() - 1) + j] = 1;
				}
			}
		}

		int result[] = new int[200];
		double scale = (double) image.length / result.length;
		for (int i = 0; i < result.length; i++) {
			result[i] = image[(int) (scale * i)];
		}
		return result;
	}

	static private int[] makeFeature(int[][] input) {
		int[] image = new int[input.length * input[0].length - input.length];

		bgRGB = input[0][0];
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[0].length - 1; j++) {
				int rgb = input[i][j];
				// System.out.println(rgb);
				if (rgb == 0) {
					image[i * (input[0].length - 1) + j] = 0;
					// System.out.println(0);
				} else {
					image[i * (input[0].length - 1) + j] = 1;
					// System.out.println(1);
				}
			}
		}

		int result[] = new int[200];
		double scale = (double) image.length / result.length;
		for (int i = 0; i < result.length; i++) {
			result[i] = image[(int) (scale * i)];
		}
		return result;
	}

	static public svm_model makeLableModel(File dir, String label)
			throws IOException {
		if (dir.isFile()) {
			return null;
		}

		Vector<Double> vy = new Vector<Double>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		int max_index = 0;
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && files[i].getName().length() == 1) {
				File[] files2 = files[i].listFiles();
				for (int j = 0; j < files2.length; j++) {
					int feature[] = makeFeature(files2[j]);
					svm_node[] x = new svm_node[feature.length];
					for (int k = 0; k < x.length; k++) {
						x[k] = new svm_node();
						x[k].index = k;
						x[k].value = feature[k];
					}
					vx.add(x);
					if (files[i].getName().equals(label)) {
						vy.add(1.);
					} else {
						vy.add(-1.);
					}
					max_index = feature.length;
				}
			}
		}

		// 定义svm_problem对象
		svm_problem problem = new svm_problem();
		problem.l = vx.size(); // 向量个数
		problem.x = new svm_node[problem.l][];
		for (int i = 0; i < problem.l; i++)
			problem.x[i] = vx.elementAt(i);
		problem.y = new double[problem.l];
		for (int i = 0; i < problem.l; i++)
			problem.y[i] = vy.elementAt(i);

		// 定义svm_parameter对象
		svm_parameter param = new svm_parameter();
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.cache_size = 100;
		param.eps = 0.00001;
		param.C = 10;
		param.probability = 1;

		// 训练SVM分类模型
		// System.out.println(svm.svm_check_parameter(problem, param)); //
		// 如果参数没有问题，则svm.svm_check_parameter()函数返回null,否则返回error描述。
		svm_model model = svm.svm_train(problem, param); // svm.svm_train()训练出SVM分类模型

		return model;
	}

	public static String recongKey(String filename) {

		BufferedImage bufferedImage = LoadImage.loadImage(filename);
		int[][] rgbImage = PreProcess.toRGB(bufferedImage);
		rgbImage = PreProcess.noiseFilter(rgbImage, 1);
		Map<Integer, int[][]> result = LinkSection.getLinkedSections(rgbImage,
				PreProcess.bgColor);
		if (result == null)
			return null;

		int index = 10;
		String resultString = "";
		for (Integer integer : result.keySet()) {
			index++;
			int[][] input = result.get(integer);

			// try {
			// DrawImage.drawGreyImage(index + ".png", input);
			// index++;
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			if(input == null) {
				continue;
			}

			int[] feature = makeFeature(input);

			svm_node[] pc = new svm_node[feature.length];
			for (int i = 0; i < feature.length; i++) {
				pc[i] = new svm_node();
				pc[i].index = i;
				pc[i].value = feature[i];
			}

			double[] proability = new double[label.length()];
			for (int i = 0; i < proability.length; i++) {
				double values[] = new double[2];
				double r = svm.svm_predict_probability(models[i], pc, values);
				if (r == 1.0) {
					proability[i] = Math.max(values[0], values[1]);
					// System.out.println(label.charAt(i));
				} else {
					proability[i] = Math.min(values[0], values[1]);
				}
			}

			int maxIndex = 0;
			double maxPro = 0;
			for (int i = 0; i < proability.length; i++) {
				if (proability[i] > maxPro) {
					maxPro = proability[i];
					maxIndex = i;
				}
			}
			// System.err.println(label.charAt(maxIndex));
			resultString += label.charAt(maxIndex);
		}
		return resultString;
	}

	static String label = "345678acdefghijklmnoqrtuwx";
	static svm_model[] models = new svm_model[label.length()];

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {

		String pathname = "./";
		File dir = new File(pathname);
		System.out.println(dir.getAbsolutePath());
		/************* from file dirs read train data ****************/

		for (int i = 0; i < models.length; i++) {
			File modelFile = new File(label.substring(i, i + 1) + ".model");
			if (modelFile.exists()) {
				ObjectInputStream inputStream = new ObjectInputStream(
						new FileInputStream(modelFile));
				models[i] = (svm_model) inputStream.readObject();
			} else {
				models[i] = makeLableModel(dir, label.substring(i, i + 1));
				ObjectOutputStream outputStream = new ObjectOutputStream(
						new FileOutputStream(modelFile));
				outputStream.writeObject(models[i]);
			}
		}

		String testPathString = "./test/";
		File testDir = new File(testPathString);
		File[] testFiles = testDir.listFiles();
		for (File file : testFiles) {
			System.out.println(file.getName());
			String newNameString = recongKey(file.getAbsolutePath());
			File newFile = new File("./result/" + newNameString + ".png");
			FileInputStream fileInputStream = new FileInputStream(file);
			FileOutputStream fileOutputStream = new FileOutputStream(newFile);
			byte []b = new byte[(int) file.length()];
			fileInputStream.read(b);
			fileOutputStream.write(b);
//			file.renameTo(newFile);
		}

		// // 定义测试数据点c
		// File file = new File("17.png");
		// if (!file.exists()) {
		// System.exit(0);
		// }
		// BufferedImage bufferedImage = ImageIO.read(file);
		// int[][] input = new int[bufferedImage.getHeight()][bufferedImage
		// .getWidth()];
		//
		// for (int i = 0; i < input.length; i++) {
		// for (int j = 0; j < input[0].length; j++) {
		// input[i][j] = bufferedImage.getRGB(j, i);
		// }
		// }
		//
		// int[] feature = makeFeature(input);
		// svm_node[] pc = new svm_node[feature.length];
		// for (int i = 0; i < feature.length; i++) {
		// pc[i] = new svm_node();
		// pc[i].index = i;
		// pc[i].value = feature[i];
		// }
		//
		// double[] proability = new double[label.length()];
		// for (int i = 0; i < proability.length; i++) {
		// double values[] = new double[2];
		// double r = svm.svm_predict_probability(models[i], pc, values);
		// if (r == 1.0) {
		// proability[i] = Math.max(values[0], values[1]);
		// System.out.println(label.charAt(i));
		// } else {
		// proability[i] = Math.min(values[0], values[1]);
		// }
		// }
		//
		// int maxIndex = 0;
		// double maxPro = 0;
		// for (int i = 0; i < proability.length; i++) {
		// if (proability[i] > maxPro) {
		// maxPro = proability[i];
		// maxIndex = i;
		// }
		// }
		//
		// System.out.println(label.charAt(maxIndex));
	}
}
