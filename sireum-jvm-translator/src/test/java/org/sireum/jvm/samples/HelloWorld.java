package org.sireum.jvm.samples;

public class HelloWorld {
	public static int sum(int a, int b) {
		return a + b;
	}

	public static void main(String[] args) {
		String hello = "Hello World!";
		int i = 0;
		int j = 1;
		int k = sum(i, j);
		System.out.println(hello);
	}
}
