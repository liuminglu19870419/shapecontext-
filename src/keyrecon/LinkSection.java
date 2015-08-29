package keyrecon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import kdtree.KDTree;
import kdtree.KeyDuplicateException;
import kdtree.KeySizeException;

//import edu.wlu.cs.levy.CG.KDTree;
//import edu.wlu.cs.levy.CG.KeyDuplicateException;
//import edu.wlu.cs.levy.CG.KeySizeException;

public class LinkSection {
	static class Pair {
		public int start;
		public int end;
		public int line;
		public int color;
		public int index;
		public boolean merge;

		public Pair(int start, int end, int color, int line) {
			this.end = end;
			this.start = start;
			this.color = color;
			this.line = line;
			this.merge = false;
		}

		@Override
		public String toString() {
			return new String("start:" + start + " " + "end:" + end + " "
					+ "index:" + index + " " + "color:" + color);
		}

	}

	static int index = 0;

	public static ArrayList<Pair> genRowPair(final int[][] bitImage, int row,
			int bgRGB) {

		ArrayList<Pair> pairs = new ArrayList<Pair>();
		int currentRGB = 0;
		int preRGB = bgRGB;
		int start = -1;
		int end = -1;
		for (int x = 0; x <= bitImage[0].length; x++) {
			if (x == bitImage[0].length)
				currentRGB = bgRGB;
			else {
				currentRGB = bitImage[row][x];
			}
			if (currentRGB != bgRGB) {
				if (start < 0) {
					start = x;
					end = x;
					preRGB = currentRGB;
					continue;
				} else if (currentRGB == preRGB) { // the same pair
					end++;
					preRGB = currentRGB;
					continue;
				} else {
					Pair pair = new Pair(start, end, preRGB, row);// find one
					pair.index = index++;
					pairs.add(pair);

					start = x;
					end = x;
					preRGB = currentRGB;
					continue;
				}
			} else if (start < 0) { // background section
				preRGB = currentRGB;
				continue;
			} else {
				Pair pair = new Pair(start, end, preRGB, row);
				pair.index = index++;
				pairs.add(pair);
				start = -1;
				end = -1;
			}
		}
		return pairs;
	}

	static public void mergeToPreLine(ArrayList<Pair> currentLine,
			ArrayList<Pair> preLine, Map<Integer, Integer> equalityMap) {
		Pair preLinePair = null;
		int preLineIndex = 0;
		for (Pair p : preLine) {
			p.merge = false;
		}
		if (preLine.size() != 0) {
			preLinePair = preLine.get(preLineIndex);
		} else {
			return;
		}

		Pair currentLinePair = null;
		int currentLineIndex = 0;
		if (currentLine.size() != 0) {
			currentLinePair = currentLine.get(currentLineIndex);
		} else {
			return;
		}

		while (true) {
			if (preLinePair.start > currentLinePair.end + 1) {
				if (currentLine.size() > currentLineIndex) {
					currentLinePair = currentLine.get(currentLineIndex++);
					continue;
				} else {
					break;
				}
			} else if (currentLinePair.start > preLinePair.end + 1) {
				if (preLine.size() > preLineIndex) {
					preLinePair = preLine.get(preLineIndex++);
					continue;
				} else {
					break;
				}
			} else {
				if (currentLinePair.color == preLinePair.color
						&& currentLinePair.index != preLinePair.index) {
					if (currentLinePair.merge == true) {
						equalityMap.put(preLinePair.index,
								currentLinePair.index);
						preLinePair.index = currentLinePair.index;
					} else {
						currentLinePair.index = preLinePair.index;
						currentLinePair.merge = true;
					}
				} // if (pair.color == prePair.color
				if (currentLinePair.end <= preLinePair.end) {
					if (currentLine.size() > currentLineIndex) {
						currentLinePair = currentLine.get(currentLineIndex++);
						continue;
					} else {
						break;
					}
				}
				if (preLinePair.end < currentLinePair.end) {
					if (preLine.size() > preLineIndex) {
						preLinePair = preLine.get(preLineIndex++);
						continue;
					} else {
						break;
					}
				}

			} // if (prePair.start > pair.end + 1) {
		} // while (true)
	}

	static public Map<Integer, Integer> reImage(Map<Integer, Integer> indexImage) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Integer integer : indexImage.keySet()) {
			Integer imageInteger = indexImage.get(integer);
			while (indexImage.get(imageInteger) != null) {
				imageInteger = indexImage.get(imageInteger);
			}
			map.put(integer, imageInteger);
		}
		return map;
	}

	static public void changSectionIndex(Map<Integer, Integer> indexImageMap,
			ArrayList<ArrayList<Pair>> pairs) {
		for (ArrayList<Pair> linkedList : pairs) {
			for (Pair pair : linkedList) {
				if (indexImageMap.get(pair.index) != null) {
					pair.index = indexImageMap.get(pair.index);
				}
			}
		}
	}

	static public ArrayList<ArrayList<Pair>> linkedSection(int[][] bitImage,
			int bgRGB) {

		ArrayList<ArrayList<Pair>> pairs = new ArrayList<ArrayList<Pair>>();

		ArrayList<Pair> preLine = genRowPair(bitImage, 0, bgRGB);
		pairs.add(preLine);

		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i = 1; i < bitImage.length; i++) {
			ArrayList<Pair> currentLine = genRowPair(bitImage, i, bgRGB);
			mergeToPreLine(currentLine, preLine, indexMap);
			// System.out.println(preLine);
			// System.out.println(currentLine);
			pairs.add(currentLine);
			preLine = currentLine;
		}
		indexMap = reImage(indexMap);
		changSectionIndex(indexMap, pairs);
		return pairs;
	}

	public static class Section {
		public int x1, y1, x2, y2, area;
		public int index;
		ArrayList<Pair> pairs;

		public Section(int index) {
			this.index = index;
			pairs = new ArrayList<LinkSection.Pair>();
			area = x2 = y2 = 0;
			x1 = y1 = 1000;
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof Section) {
				return ((Section) object).index == this.index;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(index);
		}
	}

	public static Map<Integer, Section> genSections(
			ArrayList<ArrayList<Pair>> pairs) {
		Map<Integer, Section> sectionsMap = new HashMap<Integer, Section>();

		for (ArrayList<Pair> arrayList : pairs) {
			if (arrayList.size() == 0)
				continue;
			for (Pair pair : arrayList) {
				int index = pair.index;
				if (!sectionsMap.containsKey(index)) {
					sectionsMap.put(index, new Section(index));
				}

				sectionsMap.get(index).pairs.add(pair);
				sectionsMap.get(index).area += pair.end - pair.start + 1;
				if (sectionsMap.get(index).x1 >= pair.start) {
					sectionsMap.get(index).x1 = pair.start;
				}
				if (sectionsMap.get(index).x2 <= pair.end) {
					sectionsMap.get(index).x2 = pair.end;
				}
				if (sectionsMap.get(index).y1 >= pair.line) {
					sectionsMap.get(index).y1 = pair.line;
				}
				if (sectionsMap.get(index).y2 <= pair.line) {
					sectionsMap.get(index).y2 = pair.line;
				}
			}
		}
		return sectionsMap;
	}

	static public Section mergeSection(Section s1, Section s2) {
		Section section = new Section(s1.index);
		section.area = s1.area + s2.area;
		for (Pair pair : s2.pairs) {
			pair.index = s1.index;
		}
		section.pairs.addAll(s1.pairs);
		section.pairs.addAll(s2.pairs);
		section.x1 = s1.x1 < s2.x1 ? s1.x1 : s2.x1;
		section.x2 = s1.x2 > s2.x2 ? s1.x2 : s2.x2;
		section.y1 = s1.y1 < s2.y1 ? s1.y1 : s2.y1;
		section.y2 = s1.y2 > s2.y2 ? s1.y2 : s2.y2;
		return section;
	}

	static public Map<Integer, int[][]> getLinkedSections(int[][] image, int bg) {

		ArrayList<ArrayList<Pair>> pairs = linkedSection(image, bg);
		Map<Integer, Section> sectionsMap = genSections(pairs);

		if (sectionsMap.size() == 3) { // need seperate

			int widthMax = 0;
			int widthMaxIndex = 0;
			for (Integer integer : sectionsMap.keySet()) {
				int width = sectionsMap.get(integer).x2
						- sectionsMap.get(integer).x1;
				if (width > widthMax) {
					widthMax = width;
					widthMaxIndex = integer;
				}
			}

			int seperateWidth = widthMax / 2;
			if (seperateWidth / 2 == 0) {
				seperateWidth += 1;
			}
			int[][] doubleChar = new int[sectionsMap.get(widthMaxIndex).y2
					- sectionsMap.get(widthMaxIndex).y1 + 1][seperateWidth];
			System.out.println(doubleChar[0].length);
			for (Pair pair : sectionsMap.get(widthMaxIndex).pairs) {
				for (int i = pair.start; i < pair.end; i++) {
					if (i < (sectionsMap.get(widthMaxIndex).x1 + sectionsMap
							.get(widthMaxIndex).x2) / 2 + seperateWidth / 2
							&& i >= (sectionsMap.get(widthMaxIndex).x1 + sectionsMap
									.get(widthMaxIndex).x2)
									/ 2
									- seperateWidth
									/ 2) {

						doubleChar[pair.line
								- sectionsMap.get(widthMaxIndex).y1][i
								- (sectionsMap.get(widthMaxIndex).x1 + sectionsMap
										.get(widthMaxIndex).x2) / 2
								+ seperateWidth / 2] = Color.BLUE.getRGB();
					}
				}
			}
			ArrayList<Pair> seperatePairs = SeperateImage.dijkstraPath(
					doubleChar, 0);

			ArrayList<Pair> seperatePairs2 = new ArrayList<Pair>(seperatePairs);
			for (int i = 0; i < seperatePairs.size(); i++) {
				seperatePairs.get(i).start += (sectionsMap.get(widthMaxIndex).x1 + sectionsMap
						.get(widthMaxIndex).x2) / 2 - seperateWidth / 2;
				seperatePairs2.set(seperatePairs.get(i).end,
						seperatePairs.get(i));
			}
			seperatePairs = seperatePairs2;
			seperatePairs.add(new Pair(
					seperatePairs.get(seperatePairs.size() - 1).start,
					sectionsMap.get(widthMaxIndex).y2
							- sectionsMap.get(widthMaxIndex).y1 + 1, 0, 0));
			System.out.println(seperatePairs);
			Integer integerNew = widthMaxIndex + 1;
			while (sectionsMap.containsKey(integerNew)) {
				integerNew = integerNew + 1;
			}

			ArrayList<ArrayList<Pair>> tempPairs = new ArrayList<ArrayList<Pair>>();
			tempPairs.add(new ArrayList<LinkSection.Pair>());
			int y1 = sectionsMap.get(widthMaxIndex).y1;
			for (Pair pair : sectionsMap.get(widthMaxIndex).pairs) {
				if (pair.end < seperatePairs.get(pair.line - y1).start) {
					tempPairs.get(0).add(pair);
				} else if (pair.start > seperatePairs.get(pair.line - y1).start) {
					pair.index = integerNew;
					tempPairs.get(0).add(pair);
				} else {
					Pair pair1 = new Pair(pair.start,
							seperatePairs.get(pair.line - y1).start, 0,
							pair.line);
					pair1.index = widthMaxIndex;
					Pair pair2 = new Pair(
							seperatePairs.get(pair.line - y1).start, pair.end,
							0, pair.line);
					pair2.index = integerNew;
					tempPairs.get(0).add(pair1);
					tempPairs.get(0).add(pair2);
				}
			}
			Map<Integer, Section> tempSectionMap = genSections(tempPairs);
			sectionsMap.putAll(tempSectionMap);
		} // if (sectionsMap.size() == 3) {

		if (sectionsMap.size() == 5) { // need merge

			// find the intersection sections
			ArrayList<Section> sectionList = new ArrayList<Section>(
					sectionsMap.size());
			Section s1 = null, s2 = null;
			for (Integer integer : sectionsMap.keySet()) {
				sectionList.add(sectionsMap.get(integer));
			}

			int interSectionCount = 0;
			for (int i = 0; i < sectionList.size() - 1; i++) {
				for (int j = 1; j < sectionList.size(); j++) {
					if (isInterSection(sectionList.get(i).x1,
							sectionList.get(i).x2, sectionList.get(j).x1,
							sectionList.get(2).x2)) {
						interSectionCount++;
						s1 = sectionList.get(i);
						s2 = sectionList.get(j);
					}
				}
			}

			if (interSectionCount == 1) {
				s2 = sectionsMap.remove(s2.index);
				sectionsMap.put(s1.index, mergeSection(s1, s2));
			} else {

				while (sectionsMap.size() != 4) {
					int minAreaIndex = 0;
					int minArea = Integer.MAX_VALUE;
					Section minSection = null;
					sectionList = new ArrayList<Section>();
					for (Integer integer : sectionsMap.keySet()) {
						sectionList.add(sectionsMap.get(integer));

					}

					for (Section section : sectionList) {
						if (section.area < minArea) {
							minArea = section.area;
							minAreaIndex = section.index;
							minSection = section;
						}
					}

					minSection = sectionsMap.remove(minAreaIndex);
					if (minSection == null) {
						continue;
					}

					ArrayList<double[]> minAreaSectionPointList = new ArrayList<double[]>();
					for (Pair pair : minSection.pairs) {
						for (int i = pair.start; i <= pair.end; i++) {
							minAreaSectionPointList.add(new double[] { i,
									pair.line });
						}
					}

					double minDistance = Double.POSITIVE_INFINITY;
					int minDistanceSectionIndex = 0;
					Section minDistanceSection = null;
					for (Integer integer : sectionsMap.keySet()) {
						KDTree<Pair> kdTree = makeKdTreeFromSection(sectionsMap
								.get(integer));
						for (double[] ds : minAreaSectionPointList) {
							try {
								Pair pair = kdTree.nearest(ds);
								double curDistance = Math.sqrt(Math.pow(ds[0]
										- pair.start, 2)
										+ Math.pow(ds[1] - pair.end, 2));

								System.out.println(curDistance);
								if (curDistance < minDistance) {
									minDistanceSectionIndex = integer;
									minDistanceSection = sectionsMap
											.get(integer);
									minDistance = curDistance;
									System.out.println(curDistance);
								}
							} catch (KeySizeException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} // for (Map.Entry<Integer, Section> entry : sectionsMap

					// if (minDistanceSection != null) {
					sectionsMap.remove(minDistanceSection);
					sectionsMap.put(minDistanceSectionIndex,
							mergeSection(minSection, minDistanceSection));
					// }
				} // while();
			} // if (interSectionCount == 1) {

		} // if (sectionsMap.size() == 5) { // need merge

		System.out.println(sectionsMap.size());
		Map<Integer, Section> tempMap = new TreeMap<>();
		for (Integer integer : sectionsMap.keySet()) {
			tempMap.put(sectionsMap.get(integer).x1, sectionsMap.get(integer));
		}

		sectionsMap = tempMap;
	
		Map<Integer, int[][]> resultMap = new TreeMap<Integer, int[][]>();
		for (Integer integer : sectionsMap.keySet()) {
			int[][] eleImage = new int[sectionsMap.get(integer).y2
					- sectionsMap.get(integer).y1 + 1][sectionsMap.get(integer).x2
					- sectionsMap.get(integer).x1 + 1];
			resultMap.put(integer, eleImage);
			for (Pair pair : sectionsMap.get(integer).pairs) {
				for (int i = pair.start; i < pair.end; i++) {
					resultMap.get(integer)[pair.line
							- sectionsMap.get(integer).y1][i
							- sectionsMap.get(integer).x1] = Color.BLUE
							.getRGB();
				}
			}

		}

		return resultMap;
	}

	static public boolean isInterSection(double x1, double y1, double x2,
			double y2) {
		if (x1 >= x2) {
			return x1 <= y2;
		} else {
			return x2 <= y1;
		}

	};

	static public BufferedImage drawResult(ArrayList<ArrayList<Pair>> pairs,
			int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2d = bufferedImage.createGraphics();
		// graphics2d.setColor(Color.white);
		// graphics2d.fillRect(0, 0, bufferedImage.getWidth(),
		// bufferedImage.getHeight());

		Color[] color = new Color[7];
		color[0] = Color.RED;
		color[1] = Color.BLUE;
		color[2] = Color.YELLOW;
		color[3] = Color.GREEN;
		color[4] = Color.ORANGE;
		color[5] = Color.CYAN;
		color[6] = Color.MAGENTA;
		int colorIndex = 0;
		Map<Integer, Color> colorIndexMap = new HashMap<Integer, Color>();
		for (ArrayList<Pair> linkedList : pairs) {
			for (Pair pair : linkedList) {
				if (colorIndexMap.get(pair.index) == null) {
					colorIndexMap.put(pair.index, color[colorIndex
							% color.length]);
					colorIndex++;
				}
				graphics2d.setColor(colorIndexMap.get(pair.index));
				graphics2d.drawLine(pair.start, pair.line, pair.end, pair.line);
			}
		}
		return bufferedImage;

	}

	static public KDTree<Pair> makeKdTreeFromSection(Section section) {
		KDTree<Pair> kdTree = new KDTree<Pair>(2);

		for (Pair pair : section.pairs) {
			for (int i = pair.start; i <= pair.end; i++) {
				try {
					Pair pair2 = new Pair(i, pair.line, 0, 0);
					kdTree.insert(new double[] { i, pair.line }, pair2);
				} catch (KeySizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeyDuplicateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return kdTree;
	}
}
