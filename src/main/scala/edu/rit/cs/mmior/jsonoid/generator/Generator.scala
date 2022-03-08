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
  def generate(schema: S, useExamples: Boolean = false, depth: Int = 0): T
}

object Generator {
  val MaxDepth: Int = 10

  val AnyGenerators: List[Function2[Boolean, Int, JValue]] = List(
    (useExamples, depth) =>
      BooleanGenerator.generate(BooleanSchema(), useExamples, depth),
    (useExamples, depth) =>
      IntegerGenerator.generate(IntegerSchema(), useExamples, depth),
    (useExamples, depth) =>
      NullGenerator.generate(NullSchema(), useExamples, depth),
    (useExamples, depth) =>
      NumberGenerator.generate(NumberSchema(), useExamples, depth),
    (useExamples, depth) =>
      StringGenerator.generate(StringSchema(), useExamples, depth)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def generateFromSchema(
      schema: JsonSchema[_],
      useExamples: Boolean = false,
      depth: Int = 0
  ): JValue = {
    schema match {
      case s: ArraySchema   => ArrayGenerator.generate(s, useExamples, depth)
      case s: BooleanSchema => BooleanGenerator.generate(s, useExamples, depth)
      case s: EnumSchema    => EnumGenerator.generate(s, useExamples, depth)
      case s: IntegerSchema => IntegerGenerator.generate(s, useExamples, depth)
      case s: NullSchema    => NullGenerator.generate(s, useExamples, depth)
      case s: NumberSchema  => NumberGenerator.generate(s, useExamples, depth)
      case s: ObjectSchema  => ObjectGenerator.generate(s, useExamples, depth)
      case s: ProductSchema => ProductGenerator.generate(s, useExamples, depth)
      case s: ReferenceSchema =>
        val ref = s.properties.getOrNone[ReferenceObjectProperty].map(_.schema)
        ref match {
          case Some(refSchema) =>
            generateFromSchema(refSchema, useExamples, depth)
          case None =>
            throw new UnsupportedOperationException(
              "unresolved reference found"
            )
        }
      case s: StringSchema => StringGenerator.generate(s, useExamples, depth)
      case s: AnySchema =>
        AnyGenerators(Random.nextInt(AnyGenerators.size))(useExamples, depth)
    }
  }
}
