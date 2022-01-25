package edu.rit.cs.mmior.jsonoid
package generator

import discovery._
import discovery.schemas._
import discovery.schemas.PropertySets._

class NumberGeneratorSpec extends UnitSpec {
  behavior of "NumberGenerator"

  private val numberSchema =
    NumberSchema(1.0).merge(NumberSchema(5.0)).asInstanceOf[NumberSchema]

  it should "generate a number in range" in {
    val cp = new Checkpoint()

    val num = NumberGenerator.generate(numberSchema).num.toDouble
    cp { num should be >= 1.0 }
    cp { num should be <= 5.0 }

    cp.reportAll()
  }
}
