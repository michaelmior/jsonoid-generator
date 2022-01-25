package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  MaxIntValueProperty,
  MinIntValueProperty,
  MultipleOfProperty,
  IntegerSchema
}

import org.json4s._

object IntegerGenerator extends Generator[IntegerSchema, JInt] {
  def generate(schema: IntegerSchema): JInt = {
    val multiple = schema.properties
      .get[MultipleOfProperty]
      .multiple
      .map(_.intValue)
      .getOrElse(1)

    val minValue = schema.properties
      .get[MinIntValueProperty]
      .minIntValue
      .map(_.intValue)
      .getOrElse(0)
    val maxValue = schema.properties
      .get[MaxIntValueProperty]
      .maxIntValue
      .map(_.intValue)
      .getOrElse((1000 + (minValue / multiple).ceil.toInt) * multiple)

    // Get the possible range of multiples
    val range = (maxValue / multiple.floor) - (minValue / multiple).ceil

    // Find the smallest value in range which meets the necessary multiple
    val smallest = ((minValue / multiple).ceil * multiple).toInt

    JInt((util.Random.nextFloat * range).toInt * multiple + smallest)
  }
}