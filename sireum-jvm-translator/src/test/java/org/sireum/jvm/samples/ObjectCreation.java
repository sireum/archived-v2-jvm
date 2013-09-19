package org.sireum.jvm.samples;

public class ObjectCreation {
	String hello = "Namaste";
	public static void main(String[] args) {
		ObjectCreation oc = new ObjectCreation();
		
		System.out.println(oc.getHello());
	}
	public String getHello() {
		return hello;
	}
}
