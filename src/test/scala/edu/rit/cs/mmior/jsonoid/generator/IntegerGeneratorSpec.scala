package edu.rit.cs.mmior.jsonoid
package generator

import discovery._
import discovery.schemas._
import discovery.schemas.PropertySets._

class IntegerGeneratorSpec extends UnitSpec {
  behavior of "IntegerGenerator"

  private val numberSchema =
    IntegerSchema(1).merge(IntegerSchema(5)).asInstanceOf[IntegerSchema]

  it should "generate a number in range" in {
    val cp = new Checkpoint()

    val num = IntegerGenerator.generate(numberSchema).num.toInt
    cp { num should be >= 1 }
    cp { num should be <= 5 }

    cp.reportAll()
  }
}
