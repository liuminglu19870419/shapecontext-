package sc.hungarian;

public class test {
	public static void main(String[] args) {
		double[][] A = { { 12, 7, 9, 7 }, { 8, 9, 6, 6 },
				{ 7, 17, 12, 14 }, { 15, 14, 6, 6 } };
		Hungarian hungarian = new Hungarian(A);
		hungarian.algrm();
	}
}
