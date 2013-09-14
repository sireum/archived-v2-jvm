import org.sireum.jvm.translator._
import collection.JavaConversions._

object abc {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(128); 
  println("Welcome to the Scala worksheet");$skip(41); 
  
  val n = Map("a" -> "b", "c" -> "d");System.out.println("""n  : scala.collection.immutable.Map[String,String] = """ + $show(n ));$skip(43); 
  val m: java.util.Map[String, String] = n;System.out.println("""m  : java.util.Map[String,String] = """ + $show(m ))}
                                                  
                                                  
                                         
}
