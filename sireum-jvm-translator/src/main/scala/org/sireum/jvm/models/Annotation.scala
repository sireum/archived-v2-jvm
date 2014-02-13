package org.sireum.jvm.models


sealed abstract class AnnotationValue

case class BracketAnnotation(val values: List[String]) extends AnnotationValue
case class TextAnnotation(val value: String) extends AnnotationValue
case class SimpleAnnotation(val value: String) extends AnnotationValue
case class EmptyAnnotation() extends AnnotationValue

class Annotation(val name: String, val value: AnnotationValue) {
  val getName = name

}
