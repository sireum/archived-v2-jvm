package org.sireum.test.jvm.samples;

class Base {
	public int i = 0;

	Base(int i) {
		this.i = i;
	}
}

public class Inheritance extends Base {

	Inheritance(int i) {
		super(i);
	}

	void print() {
		System.out.println("hello");
	}

	public static void main(String[] args) {
		Inheritance i = new Inheritance(2);
		i.print();
	}
}
