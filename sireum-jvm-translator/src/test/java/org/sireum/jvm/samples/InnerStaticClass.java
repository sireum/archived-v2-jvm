package org.sireum.jvm.samples;

public class InnerStaticClass {
	public static class Point {
		int x;
		int y;

		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		void print() {
			System.out.println(x + "," + y);
		}
	}

	public static void main(String[] args) {
		InnerStaticClass.Point p = new InnerStaticClass.Point(2, 3);

		p.print();
	}
}
