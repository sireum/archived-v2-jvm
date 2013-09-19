package org.sireum.jvm.samples;

@interface WithValue {
	String bytes();
	int[] arr();
	Nested[] nested();
}
@interface Nested {
	String name();
}
@WithValue(bytes = "akljfla", arr= {1,2}, nested= @Nested(name="abc"))
public class Annotations {
	int x;
}
