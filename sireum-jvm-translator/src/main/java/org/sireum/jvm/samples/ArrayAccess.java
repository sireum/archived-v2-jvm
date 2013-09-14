package org.sireum.jvm.samples;

public class ArrayAccess {
	public static void main(String[] args) {
		double[] arr1 = new double[2];
		int[][] arr2 = new int[10][10];
		int[][][][] arr3 = new int[2][2][2][2];
		
		arr2[2][3] = 2;
		arr1[1] = arr2[2][3];
		arr3[1][1][1][1] = arr2[2][3];
	}
}
