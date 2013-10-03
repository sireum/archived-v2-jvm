package org.sireum.test.jvm.samples;

public class Test {
	
  public static void main(String[] args){
	  
	  Test mytest = new Test();
	  mytest.foo1();
	  mytest.foo2();
	  
  }
  
  void foo1(){
	   StringBuilder i = new StringBuilder();
	    i.append("abc");
	    Class c = i.getClass();
	   
	    StringBuilder j = new StringBuilder();
	    j.append("def");
	    Class d = j.getClass();
	    
	    Class e = StringBuilder.class;
	    
	    String name1 = c.getName();
//	    System.out.println(name1 + " is c.name");
	    
	 
	    Class f = e.getClass();
	    
	    String name4 = f.getName();
//	    System.out.println(name4 + " is f.name");
	    
	    Class g = f.getSuperclass();
	    
	    String name5 = g.getName();
//	    System.out.println(name5 + " is g.name");
//	    
//
//	    
//	    System.out.println("c==d is " + (c==d));
//	    System.out.println("e==d is " + (e==d));
//	    System.out.println("f==d is " + (f==d));
	    System.out.println("g==f is " + (g==f));
  }
  
  void foo2(){
	  int x = ClassA.add(5);
	  int y = ClassB.add(5);
	  int z = ClassB.add(5, 5);
	  int u = ClassA.num;
	  int v = ClassB.sum();
	  int w = ClassB.numB;
	  ClassA a = new ClassA();
	  ClassB b = new ClassB();
	  a.add("abc");
	  b.add("abc");
	  b.add("abc", "def");
	  String s1 = a.str;
	  String s2 = b.str;
	  String s3 = b.strB;
	  System.out.println("s3 = " + s3);
	  System.out.println("v = " + v + " w = " + w);
  }
  
}

