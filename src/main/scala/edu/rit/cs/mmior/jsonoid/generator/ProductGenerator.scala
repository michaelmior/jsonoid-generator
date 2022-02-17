package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  ProductSchema,
  ProductSchemaTypesProperty
}

import org.json4s._

object ProductGenerator extends Generator[ProductSchema, JValue] {
  def generate(schema: ProductSchema, depth: Int): JValue = {
    val schemaTypesProp =
      schema.properties.get[ProductSchemaTypesProperty]
    val schemaTypes = schemaTypesProp.schemaTypes

    if (schemaTypesProp.all) {
      throw new UnsupportedOperationException("allOf is not supported")
    }

    // TODO: Make choice weighted
    val chosenSchema = schemaTypes(util.Random.nextInt(schemaTypes.length))

    Generator.generateFromSchema(chosenSchema, depth)
  }
}
