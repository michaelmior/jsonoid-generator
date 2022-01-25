package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  EnumSchema,
  EnumValuesProperty
}

import org.json4s._

object EnumGenerator extends Generator[EnumSchema, JValue] {
  def generate(schema: EnumSchema): JValue = {
    val values = schema.properties.get[EnumValuesProperty].values

    values.iterator.drop(util.Random.nextInt(values.size)).next
  }
}
