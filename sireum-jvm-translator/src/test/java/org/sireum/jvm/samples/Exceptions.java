package org.sireum.jvm.samples;

public class Exceptions {
	public static void main(String[] args) throws java.io.IOException {
		try {
			int i = 2;
			System.out.println(i);
		} catch (ArithmeticException ae) {
			System.out.println(ae);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
}
