package edu.rit.cs.mmior.jsonoid
package generator

import org.json4s._

import discovery._
import discovery.schemas._

class NullGeneratorSpec extends UnitSpec {
  behavior of "NullGenerator"

  private val nullSchema = NullSchema()

  it should "generate a null value" in {
    NullGenerator.generate(nullSchema) shouldBe JNull
  }
}
