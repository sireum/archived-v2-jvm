package org.sireum.jvm.samples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@interface xyz {
}
@Deprecated
public class HelloWorld2 {
	public static int field ;
	public int field2;
	public static void main(String[] args) throws Exception{
		System.out.println("hello");
		int i=1;
		int j=2;
		int k=i+j;
		
		int l = -i;
		long l2= i;
		field = 1;
		
		HelloWorld2 hw = new HelloWorld2();
		hw.field2 = 2;
		
		int[] arr = new int[10];
		l++;
		
		int adf = i+j*3-4;
		
		if(i<j) {
			System.out.println("less than");
		}
		if(i==0){
			if (hw!=null) {
				hw.sum(i,j);
			}
		}
	}
	public int sum(int i, int j) {
		return 5;
	}

}
//
//@Deprecated
//public class HelloWorld {
//	public final static String global1 = "HelloWorld!";
//	private final String member1 = "field1";
//	private String member2 = "field2";
//	@xyz private String member3 = "fieldWithAnnotation";
//	
//	public static void main(String args[]) {
//		String local1= "local";
//		System.out.println(global1);
//		System.out.println(local1);
//		
//		HelloWorld hw = new HelloWorld();
//		System.out.println(hw.sum(3,4));
//		System.out.println(hw.member1 + " " + hw.member2 + " " +hw.member3);
//		
//		int i=-1;
//		int j=2;
//		int k=-i;
//	}
//	
//	public int sum(int a, int b) {
//		return a+b;
//	}
//	
//	class Point {
//		int x;
//		int y;
//		
//		Point(int x, int y) {
//			this.x = x;
//			this.y = y;
//		}
//	}
//}