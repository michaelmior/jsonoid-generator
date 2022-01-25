package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  ProductSchema,
  ProductSchemaTypesProperty
}

import org.json4s._

object ProductGenerator extends Generator[ProductSchema, JValue] {
  def generate(schema: ProductSchema): JValue = {
    val schemaTypes =
      schema.properties.get[ProductSchemaTypesProperty].schemaTypes

    // TODO: Make choice weighted
    val chosenSchema = schemaTypes(util.Random.nextInt(schemaTypes.length))

    Generator.generateFromSchema(chosenSchema)
  }
}
