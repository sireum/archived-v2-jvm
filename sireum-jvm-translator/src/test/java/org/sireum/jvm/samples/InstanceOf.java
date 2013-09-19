package org.sireum.jvm.samples;

public class InstanceOf {
	public static void main(String[] args) {
		String a = new String("hello");
		String b = new String("world");

		Object c = (Object) b;
		if (c instanceof String) {
			a = (String) c;
		} else {
			System.out.println(a);
		}
	}
}
