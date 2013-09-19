package org.sireum.jvm.samples;

public class ArrayCreation {
	private int[][] fieldArr1 = new int[2][4];
	public double[] doubleArr1 = new double[2];
	public static final String[][] globalArr = new String[3][2];

	public static void main(String[] args) {
		int[] arr1 = new int[10];

		float[] arr2 = new float[10];

		String[] arr3 = new String[1];

		globalArr[2][1] = "hello";

		arr1[2] = 4;

		arr2[2] = arr1[1];

		arr3[0] = "hello";

		globalArr[1][0] = arr3[0];
	}
}
