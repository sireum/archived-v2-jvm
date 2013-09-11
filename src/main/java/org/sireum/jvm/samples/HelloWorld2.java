package org.sireum.jvm.samples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@interface xyz {
}
@Deprecated
public class HelloWorld2 {
	public static void main(String[] args) {
		System.out.println("hello");
	}
}
