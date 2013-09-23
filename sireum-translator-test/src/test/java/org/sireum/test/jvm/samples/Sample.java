package org.sireum.test.jvm.samples;

public class Sample {
	public static final int field = 9;

	public static void main(String[] args) {

		for (int laf = 0; laf < 10; laf++) {
			System.out.println(laf);
		}

		for (int abc = 0; abc < 10; abc++) {
			System.out.println(abc);
		}

		Sample[] hw2 = new Sample[10];
		try {
			System.out.println(hw2[1].field);
		} catch (ArithmeticException ae) {

		}

		try {
			System.out.println(hw2[2].field);
		} catch (ArrayIndexOutOfBoundsException ab) {

		}
	}

}
