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
  def generate(schema: S, depth: Int = 0): T
}

object Generator {
  val MaxDepth: Int = 10

  val AnyGenerators: List[Function1[Int, JValue]] = List(
    (depth) => BooleanGenerator.generate(BooleanSchema(), depth),
    (depth) => IntegerGenerator.generate(IntegerSchema(), depth),
    (depth) => NullGenerator.generate(NullSchema(), depth),
    (depth) => NumberGenerator.generate(NumberSchema(), depth),
    (depth) => StringGenerator.generate(StringSchema(), depth)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def generateFromSchema(schema: JsonSchema[_], depth: Int = 0): JValue = {
    schema match {
      case s: ArraySchema   => ArrayGenerator.generate(s, depth)
      case s: BooleanSchema => BooleanGenerator.generate(s, depth)
      case s: EnumSchema    => EnumGenerator.generate(s, depth)
      case s: IntegerSchema => IntegerGenerator.generate(s, depth)
      case s: NullSchema    => NullGenerator.generate(s, depth)
      case s: NumberSchema  => NumberGenerator.generate(s, depth)
      case s: ObjectSchema  => ObjectGenerator.generate(s, depth)
      case s: ProductSchema => ProductGenerator.generate(s, depth)
      case s: ReferenceSchema =>
        val ref = s.properties.getOrNone[ReferenceObjectProperty].map(_.schema)
        ref match {
          case Some(refSchema) => generateFromSchema(refSchema, depth)
          case None =>
            throw new UnsupportedOperationException(
              "unresolved reference found"
            )
        }
      case s: StringSchema => StringGenerator.generate(s, depth)
      case s: AnySchema =>
        AnyGenerators(Random.nextInt(AnyGenerators.size))(depth)
    }
  }
}
