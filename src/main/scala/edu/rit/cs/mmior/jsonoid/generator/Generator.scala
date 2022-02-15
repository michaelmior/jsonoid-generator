package edu.rit.cs.mmior.jsonoid.generator

import scala.util.Random

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  AnySchema,
  ArraySchema,
  BooleanSchema,
  EnumSchema,
  IntegerSchema,
  JsonSchema,
  NullSchema,
  NumberSchema,
  ObjectSchema,
  ProductSchema,
  ReferenceObjectProperty,
  ReferenceSchema,
  StringSchema
}

import org.json4s._

trait Generator[S, T] {
  def generate(schema: S): T
}

object Generator {
  val AnyGenerators: List[Function0[JValue]] = List(
    () => BooleanGenerator.generate(BooleanSchema()),
    () => IntegerGenerator.generate(IntegerSchema()),
    () => NullGenerator.generate(NullSchema()),
    () => NumberGenerator.generate(NumberSchema()),
    () => StringGenerator.generate(StringSchema())
  )

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def generateFromSchema(schema: JsonSchema[_]): JValue = {
    schema match {
      case s: ArraySchema   => ArrayGenerator.generate(s)
      case s: BooleanSchema => BooleanGenerator.generate(s)
      case s: EnumSchema    => EnumGenerator.generate(s)
      case s: IntegerSchema => IntegerGenerator.generate(s)
      case s: NullSchema    => NullGenerator.generate(s)
      case s: NumberSchema  => NumberGenerator.generate(s)
      case s: ObjectSchema  => ObjectGenerator.generate(s)
      case s: ProductSchema => ProductGenerator.generate(s)
      case s: ReferenceSchema =>
        val refSchema = s.properties.get[ReferenceObjectProperty].schema
        generateFromSchema(refSchema)
      case s: StringSchema => StringGenerator.generate(s)
      case s: AnySchema =>
        AnyGenerators(Random.nextInt(AnyGenerators.size))()
    }
  }
}
