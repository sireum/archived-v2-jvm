package org.sireum.jvm.samples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.google.common.annotations.Beta;

@Target(ElementType.FIELD)
@interface xyz {
}

@Deprecated
public class HelloWorld2 {
	public static final int field = 9;
	public int field2;
	public Point p = new Point(1,2);

	@Beta
	public static void main(String[] args) throws java.io.IOException,
			java.io.FileNotFoundException {
		System.out.println("hello");
		int i = 1;
		int j = 2;
		int k = i + j;

		int l = -i;
		long l2 = i;

		HelloWorld2 hw = new HelloWorld2();
		hw.field2 = field + (new HelloWorld2()).field2 + (new HelloWorld2()).field2;
		hw.p.x = 3;
		
		int[] arr = new int[10];
		l++;

		int adf = i + j * 3 - 4;

		if (i < j) {
			System.out.println("less than");
		}
		if (i == 0) {
			if (hw != null) {
				hw.sum(i, j);
			}
		}

		for (int laf = 0; laf < 10; laf++) {
			hw.sum(laf, j);
		}

		if (hw instanceof HelloWorld2) {
			hw.sum(i, j);
		}

		HelloWorld2[] hw2 = new HelloWorld2[10];
		try {
			i=2;
		} catch (ArithmeticException ae) {
			
		}
		
		switch(i) {
		case 1: return;
		case 200: return;
		case 3000: return;
		}
		
		switch(i) {
		case 1: return;
		case 2: return;
		case 3: return;
		default: return;
		}
		
		//int [][] asakf = new int[10][10];
	}

	public int sum(int i, int j) {
		return 5;
	}

	class Point {
		int x;
		int y;

		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		void print() {
			System.out.println(HelloWorld2.this.field);
		}
	}

}
//
// @Deprecated
// public class HelloWorld {
// public final static String global1 = "HelloWorld!";
// private final String member1 = "field1";
// private String member2 = "field2";
// @xyz private String member3 = "fieldWithAnnotation";
//
// public static void main(String args[]) {
// String local1= "local";
// System.out.println(global1);
// System.out.println(local1);
//
// HelloWorld hw = new HelloWorld();
// System.out.println(hw.sum(3,4));
// System.out.println(hw.member1 + " " + hw.member2 + " " +hw.member3);
//
// int i=-1;
// int j=2;
// int k=-i;
// }
//
// public int sum(int a, int b) {
// return a+b;
// }
//
// class Point {
// int x;
// int y;
//
// Point(int x, int y) {
// this.x = x;
// this.y = y;
// }
// }
// }