package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  MaxIntValueProperty,
  MinIntValueProperty,
  IntExamplesProperty,
  IntMultipleOfProperty,
  IntegerSchema
}

import org.json4s._

object IntegerGenerator extends Generator[IntegerSchema, JInt] {
  def generate(
      schema: IntegerSchema,
      depth: Int,
      useExamples: Boolean
  ): JInt = {
    if (useExamples) {
      val examples =
        schema.properties.get[IntExamplesProperty].examples.examples
      JInt(examples(util.Random.nextInt(examples.length)))
    } else {
      generate(schema, depth)
    }
  }

  def generate(schema: IntegerSchema, depth: Int): JInt = {
    val multiple = schema.properties
      .getOrNone[IntMultipleOfProperty]
      .flatMap(_.multiple)
      .map(_.intValue)
      .getOrElse(1)

    val minValue = schema.properties
      .getOrNone[MinIntValueProperty]
      .flatMap(_.minIntValue)
      .map(_.intValue)
      .getOrElse(0)
    val maxValue = schema.properties
      .getOrNone[MaxIntValueProperty]
      .flatMap(_.maxIntValue)
      .map(_.intValue)
      .getOrElse((1000 + (minValue / multiple).ceil.toInt) * multiple)

    // Get the possible range of multiples
    val range = (maxValue / multiple).floor - (minValue / multiple).ceil

    // Find the smallest value in range which meets the necessary multiple
    val smallest = ((minValue / multiple).ceil * multiple).toInt

    JInt((util.Random.nextFloat * range).toInt * multiple + smallest)
  }
}
