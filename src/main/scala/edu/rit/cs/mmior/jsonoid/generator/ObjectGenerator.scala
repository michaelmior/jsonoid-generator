package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  AnySchema,
  ObjectSchema,
  ObjectTypesProperty,
  PatternTypesProperty,
  RequiredProperty,
  StaticDependenciesProperty
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
      schema.properties
        .getOrNone[RequiredProperty]
        .flatMap(_.required)
        .getOrElse(Set())
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

    // XXX Only supports StaticDependenciesProperty, not DependenciesProperty
    //     but this should be fine since this is the property we read in when
    //     processing files via the CLI
    val dependencies = schema.properties.getOrNone[StaticDependenciesProperty].map(_.dependencies).getOrElse(Map.empty)
    val chosenKeysWithDeps = chosenKeys ++ chosenKeys.flatMap(dependencies.getOrElse(_, List()))

    JObject(
      chosenKeysWithDeps
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
