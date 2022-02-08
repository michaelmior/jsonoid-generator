package edu.rit.cs.mmior.jsonoid
package generator

import org.json4s._

import discovery._
import discovery.schemas._

class GeneratorSpec extends UnitSpec {
  behavior of "Generator"

  private val anySchema = AnySchema()

  it should "generate a value for AnySchema" in {
    Generator.generateFromSchema(anySchema) shouldBe a[JValue]
  }
}
