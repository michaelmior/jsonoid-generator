package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  MaxNumValueProperty,
  MinNumValueProperty,
  NumMultipleOfProperty,
  NumberSchema
}

import org.json4s._

object NumberGenerator extends Generator[NumberSchema, JDecimal] {
  def generate(schema: NumberSchema): JDecimal = {
    val multiple = schema.properties
      .getOrNone[NumMultipleOfProperty]
      .flatMap(_.multiple)
      .map(_.toDouble)
      .getOrElse(0.1)

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

    // Get the possible range of multiples
    val range = (maxValue / multiple).floor - (minValue / multiple).ceil

    // Find the smallest value in range which meets the necessary multiple
    val smallest = (minValue / multiple).ceil * multiple

    JDecimal((util.Random.nextFloat * range).toInt * multiple + smallest)
  }
}
