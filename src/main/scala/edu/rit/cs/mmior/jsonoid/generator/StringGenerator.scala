package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  FormatProperty,
  MaxLengthProperty,
  MinLengthProperty,
  PatternProperty,
  StringSchema
}

import org.json4s._

object StringGenerator extends Generator[StringSchema, JString] {
  def generate(schema: StringSchema): JString = {
    val patternProp =
      schema.properties.getOrNone[PatternProperty].getOrElse(PatternProperty())
    val prefix = patternProp.prefix.getOrElse("")
    val suffix = patternProp.suffix.getOrElse("")

    // Get the minimum length but ignore the prefix and suffix
    val minLength = (schema.properties
      .getOrNone[MinLengthProperty]
      .flatMap(_.minLength)
      .getOrElse(0) - prefix.length - suffix.length).max(0)

    val maxLength = schema.properties
      .getOrNone[MaxLengthProperty]
      .flatMap(_.maxLength)
      .getOrElse(minLength + 10)

    val formats =
      schema.properties
        .getOrNone[FormatProperty]
        .map(_.formats)
        .getOrElse(Map.empty)

    if (formats.isEmpty) {
      // Pick a length for the random part of the string
      // and build the string including prefix and suffix
      val length = minLength + util.Random.nextInt(maxLength - minLength + 1)
      val randString = util.Random.alphanumeric.take(length).mkString
      JString(prefix + randString + suffix)
    } else {
      throw new UnsupportedOperationException("format is not supported")
    }
  }
}
