package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  AllOf,
  ProductSchema,
  ProductSchemaTypesProperty
}

import org.json4s._

object ProductGenerator extends Generator[ProductSchema, JValue] {
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def generate(
      schema: ProductSchema,
      useExamples: Boolean,
      depth: Int
  ): JValue = {
    val schemaTypesProp =
      schema.properties.get[ProductSchemaTypesProperty]
    val schemaTypes = schemaTypesProp.schemaTypes

    if (schemaTypesProp.productType == AllOf) {
      throw new UnsupportedOperationException("allOf is not supported")
    }

    // TODO: Make choice weighted
    val chosenSchema = schemaTypes(util.Random.nextInt(schemaTypes.length))

    Generator.generateFromSchema(chosenSchema, useExamples, depth)
  }
}
