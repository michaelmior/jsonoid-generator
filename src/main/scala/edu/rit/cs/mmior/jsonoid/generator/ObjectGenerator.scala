package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  DependenciesProperty,
  ObjectSchema,
  ObjectTypesProperty,
  RequiredProperty
}

import org.json4s._

object ObjectGenerator extends Generator[ObjectSchema, JObject] {
  def generate(schema: ObjectSchema): JObject = {
    val required =
      schema.properties.get[RequiredProperty].required.getOrElse(Set())
    val objectTypes = schema.properties.get[ObjectTypesProperty].objectTypes
    val notRequired = objectTypes.keySet -- required

    // TODO: Make choice weighted based on FieldPresenceProperty
    val chosenKeys =
      required ++ notRequired.filter(_ => util.Random.nextBoolean)

    JObject(
      chosenKeys
        .map(k => (k, Generator.generateFromSchema(objectTypes(k))))
        .toList
    )
  }
}
