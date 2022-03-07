package edu.rit.cs.mmior.jsonoid
package generator

import discovery._
import discovery.schemas._
import discovery.schemas.PropertySets._

class IntegerGeneratorSpec extends UnitSpec {
  behavior of "IntegerGenerator"

  private val numberSchema =
    IntegerSchema(2).merge(IntegerSchema(100)).asInstanceOf[IntegerSchema]

  it should "generate using examples" in {
    val num = IntegerGenerator.generate(numberSchema, 0, true).num.toInt

    num should (be(2) or be(100))
  }

  it should "generate a number in range" in {
    val cp = new Checkpoint()

    val num = IntegerGenerator.generate(numberSchema).num.toInt
    cp { num should be >= 2 }
    cp { num should be <= 100 }
    cp { (num % 2) shouldBe 0 }

    cp.reportAll()
  }
}
