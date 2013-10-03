package org.sireum.test.jvm.samples;

import com.google.common.annotations.Beta;

@interface WithValue {
	String bytes();
	int[] arr();
	Nested abc();
	Nested[] nested();
}
@interface Nested {
	String name();
}
@Beta
@WithValue(bytes = "akljfla", arr= {1,2}, abc = @Nested(name="lol"), nested= {@Nested(name="abc"), @Nested(name="xyz")})
public class Annotations {
	int x;
}
