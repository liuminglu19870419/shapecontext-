package sc.keyrecon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class LinkSection {
	static public class Pair {
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
}
