package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  AnySchema,
  DependenciesProperty,
  ObjectSchema,
  ObjectTypesProperty,
  PatternTypesProperty,
  RequiredProperty
}

import com.github.curiousoddman.rgxgen.RgxGen
import com.github.curiousoddman.rgxgen.config.{RgxGenOption, RgxGenProperties}
import org.json4s._

object ObjectGenerator extends Generator[ObjectSchema, JObject] {
  def generate(
      schema: ObjectSchema,
      useExamples: Boolean,
      depth: Int
  ): JObject = {
    val required =
      schema.properties.get[RequiredProperty].required.getOrElse(Set())
    val objectTypes = schema.properties.get[ObjectTypesProperty].objectTypes
    val notRequired = objectTypes.keySet -- required

    // TODO: Make choice weighted based on FieldPresenceProperty
    val chosenKeys =
      required ++ notRequired.filter(_ =>
        util.Random.nextBoolean && depth <
          Generator.MaxDepth
      )

    val patternTypes = schema.properties
      .getOrNone[PatternTypesProperty]
      .map(_.patternTypes)
      .getOrElse(Map.empty)
    val patternPropCount =
      if (
        patternTypes.isEmpty || depth >=
          Generator.MaxDepth
      ) {
        0
      } else {
        util.Random.nextInt(10)
      }
    val patternProps = if (patternPropCount > 0) {
      (1 to patternPropCount).map { _ =>
        val prop = patternTypes.toSeq(util.Random.nextInt(patternTypes.size))
        val genProps = new RgxGenProperties()
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(genProps, 5)
        val generator = new RgxGen(prop._1.toString)
        generator.setProperties(genProps)

        (
          generator.generate(),
          Generator.generateFromSchema(prop._2, useExamples, depth + 1)
        )
      }.toList
    } else {
      List()
    }

    JObject(
      chosenKeys
        .map(k =>
          (
            k,
            // We default to AnySchema here, although there's a good chance this
            // is an error in the schema since an undefined property is required
            Generator.generateFromSchema(
              objectTypes.get(k).getOrElse(AnySchema()),
              useExamples,
              depth + 1
            )
          )
        )
        .toList ++ patternProps
    )
  }
}
