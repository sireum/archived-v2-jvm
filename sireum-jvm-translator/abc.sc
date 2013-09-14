import org.sireum.jvm.translator._
import collection.JavaConversions._

object abc {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  val n = Map("a" -> "b", "c" -> "d")             //> n  : scala.collection.immutable.Map[String,String] = Map(a -> b, c -> d)
  val m: java.util.Map[String, String] = n        //> m  : java.util.Map[String,String] = {a=b, c=d}
                                                  
                                                  
                                         
}