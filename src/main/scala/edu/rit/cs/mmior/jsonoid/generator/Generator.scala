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
  def generate(schema: S, depth: Int = 0, useExamples: Boolean = false): T
}

object Generator {
  val MaxDepth: Int = 10

  val AnyGenerators: List[Function2[Int, Boolean, JValue]] = List(
    (depth, useExamples) =>
      BooleanGenerator.generate(BooleanSchema(), depth, useExamples),
    (depth, useExamples) =>
      IntegerGenerator.generate(IntegerSchema(), depth, useExamples),
    (depth, useExamples) =>
      NullGenerator.generate(NullSchema(), depth, useExamples),
    (depth, useExamples) =>
      NumberGenerator.generate(NumberSchema(), depth, useExamples),
    (depth, useExamples) =>
      StringGenerator.generate(StringSchema(), depth, useExamples)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def generateFromSchema(
      schema: JsonSchema[_],
      depth: Int = 0,
      useExamples: Boolean = false
  ): JValue = {
    schema match {
      case s: ArraySchema   => ArrayGenerator.generate(s, depth, useExamples)
      case s: BooleanSchema => BooleanGenerator.generate(s, depth, useExamples)
      case s: EnumSchema    => EnumGenerator.generate(s, depth, useExamples)
      case s: IntegerSchema => IntegerGenerator.generate(s, depth, useExamples)
      case s: NullSchema    => NullGenerator.generate(s, depth, useExamples)
      case s: NumberSchema  => NumberGenerator.generate(s, depth, useExamples)
      case s: ObjectSchema  => ObjectGenerator.generate(s, depth, useExamples)
      case s: ProductSchema => ProductGenerator.generate(s, depth, useExamples)
      case s: ReferenceSchema =>
        val ref = s.properties.getOrNone[ReferenceObjectProperty].map(_.schema)
        ref match {
          case Some(refSchema) =>
            generateFromSchema(refSchema, depth, useExamples)
          case None =>
            throw new UnsupportedOperationException(
              "unresolved reference found"
            )
        }
      case s: StringSchema => StringGenerator.generate(s, depth, useExamples)
      case s: AnySchema =>
        AnyGenerators(Random.nextInt(AnyGenerators.size))(depth, useExamples)
    }
  }
}
