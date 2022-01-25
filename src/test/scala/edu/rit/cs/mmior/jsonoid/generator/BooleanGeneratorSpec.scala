package edu.rit.cs.mmior.jsonoid
package generator

import org.json4s._

import discovery._
import discovery.schemas._

class BooleanGeneratorSpec extends UnitSpec {
  behavior of "BooleanGenerator"

  private val boolSchema = BooleanSchema()

  it should "generate a number in range" in {
    BooleanGenerator.generate(boolSchema) shouldBe a[JBool]
  }
}
