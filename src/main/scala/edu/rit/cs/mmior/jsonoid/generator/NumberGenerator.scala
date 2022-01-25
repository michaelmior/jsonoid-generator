package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  MaxNumValueProperty,
  MinNumValueProperty,
  NumberSchema
}

import org.json4s._

object NumberGenerator extends Generator[NumberSchema, JDecimal] {
  def generate(schema: NumberSchema): JDecimal = {
    val minValue = schema.properties
      .get[MinNumValueProperty]
      .minNumValue
      .map(_.doubleValue)
      .getOrElse(0.0)
    val maxValue = schema.properties
      .get[MaxNumValueProperty]
      .maxNumValue
      .map(_.doubleValue)
      .getOrElse(minValue + 1000.0)

    val range = maxValue - minValue

    JDecimal(util.Random.nextFloat * range + minValue)
  }
}
