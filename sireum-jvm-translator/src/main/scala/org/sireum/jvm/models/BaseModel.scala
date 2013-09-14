package org.sireum.jvm.models

import java.util.HashMap

trait BaseModel {
	val annotations = new HashMap[String, String]()
}