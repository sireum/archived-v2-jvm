package org.sireum.test.jvm.samples;


public class ClassB extends ClassA{
	
	String strB = "b";
	static int numB = 50;
	
	
	public String add (String str1){
		return ("at B" + str + str1);
	}
	
	public String add (String str1, String str2){
		return (strB + str1 + str2);
	}

	
	public static int add (int i){
		return (100 + num + i);
	}
	
	public static int sum(){
		return(numB + num);
	}

}
