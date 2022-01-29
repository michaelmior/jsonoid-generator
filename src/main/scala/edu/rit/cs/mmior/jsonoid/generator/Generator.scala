package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  ArraySchema,
  BooleanSchema,
  EnumSchema,
  IntegerSchema,
  JsonSchema,
  NullSchema,
  NumberSchema,
  ObjectSchema,
  ProductSchema,
  StringSchema
}

import org.json4s._

trait Generator[S, T] {
  def generate(schema: S): T
}

object Generator {
  def generateFromSchema(schema: JsonSchema[_]): JValue = {
    schema match {
      case s: ArraySchema   => ArrayGenerator.generate(s)
      case s: BooleanSchema => BooleanGenerator.generate(s)
      case s: EnumSchema    => EnumGenerator.generate(s)
      case s: IntegerSchema => IntegerGenerator.generate(s)
      case s: NumberSchema  => NumberGenerator.generate(s)
      case s: NullSchema    => NullGenerator.generate(s)
      case s: ObjectSchema  => ObjectGenerator.generate(s)
      case s: ProductSchema => ProductGenerator.generate(s)
      case s: StringSchema  => StringGenerator.generate(s)
    }
  }
}
