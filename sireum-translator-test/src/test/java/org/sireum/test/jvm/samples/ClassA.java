package org.sireum.test.jvm.samples;


public class ClassA {
	
	String str = "a";
	static int num = 0;
	
	
	public String add (String str1){
		return (str + str1);
	}
	
	public String add (String str1, String str2){
		return (str + str1 + str2);
	}

	public static int add (int i){
		return (num + i);
	}
	
	public static int add (int i, int j){
		return (num + i + j);
	}
	

}
