package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  ArraySchema,
  MaxItemsProperty,
  MinItemsProperty,
  ItemTypeProperty,
  UniqueProperty
}

import org.json4s._

object ArrayGenerator extends Generator[ArraySchema, JArray] {
  def generate(schema: ArraySchema): JArray = {
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
        val numItems = minItems + util.Random.nextInt(maxItems - minItems + 1)
        val isUnique =
          schema.properties
            .getOrNone[UniqueProperty]
            .map(p => p.unique && !p.unary)
            .getOrElse(false)

        JArray(if (isUnique) {
          Stream
            .from(1)
            .map(_ => Generator.generateFromSchema(itemSchema))
            .distinct
            .take(numItems)
            .toList
        } else {
          (1 to numItems)
            .map(_ => Generator.generateFromSchema(itemSchema))
            .toList
        })
      case Right(schemas) =>
        JArray(schemas.map(s => Generator.generateFromSchema(s)))
    }
  }
}
