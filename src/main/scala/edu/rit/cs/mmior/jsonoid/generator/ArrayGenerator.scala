package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  ArraySchema,
  EnumSchema,
  EnumValuesProperty,
  MaxItemsProperty,
  MinItemsProperty,
  ItemTypeProperty,
  UniqueProperty
}

import org.json4s._

object ArrayGenerator extends Generator[ArraySchema, JArray] {
  def generate(schema: ArraySchema, depth: Int): JArray =
    generate(schema, depth, None)

  def generate(schema: ArraySchema, depth: Int, items: Option[Int]): JArray = {
    val itemType = schema.properties.get[ItemTypeProperty].itemType

    itemType match {
      case Left(itemSchema) =>
        val minItems = schema.properties
          .getOrNone[MinItemsProperty]
          .flatMap(_.minItems)
          .getOrElse(0)
        val maxItems = schema.properties
          .getOrNone[MaxItemsProperty]
          .flatMap(_.maxItems)
          .getOrElse(minItems + 10)
        val randItems = items.getOrElse(
          minItems + util.Random.nextInt(maxItems - minItems + 1)
        )
        val isUnique =
          schema.properties
            .getOrNone[UniqueProperty]
            .map(p => p.unique && !p.unary)
            .getOrElse(false)

        // Make sure we don't try to generate too many things for EnumSchema
        val numItems = if (isUnique && itemSchema.isInstanceOf[EnumSchema]) {
          randItems.min(
            itemSchema
              .asInstanceOf[EnumSchema]
              .properties
              .get[EnumValuesProperty]
              .values
              .size
          )
        } else {
          randItems
        }

        JArray(if (isUnique) {
          Stream
            .from(1)
            .map(_ => Generator.generateFromSchema(itemSchema, depth + 1))
            .distinct
            .take(numItems)
            .toList
        } else {
          (1 to numItems)
            .map(_ => Generator.generateFromSchema(itemSchema, depth + 1))
            .toList
        })
      case Right(schemas) =>
        JArray(schemas.map(s => Generator.generateFromSchema(s, depth + 1)))
    }
  }
}
