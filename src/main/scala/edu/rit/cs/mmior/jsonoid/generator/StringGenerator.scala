package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  FormatProperty,
  MaxLengthProperty,
  MinLengthProperty,
  PatternProperty,
  StaticPatternProperty,
  StringExamplesProperty,
  StringSchema
}

import com.github.javafaker.Faker
import java.text.SimpleDateFormat
import org.json4s._

object StringGenerator extends Generator[StringSchema, JString] {
  def generate(
      schema: StringSchema,
      useExamples: Boolean,
      depth: Int
  ): JString = {
    if (useExamples) {
      val examples =
        schema.properties.get[StringExamplesProperty].examples.examples
      JString(examples(util.Random.nextInt(examples.length)))
    } else {
      generate(schema, depth)
    }
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def generate(schema: StringSchema, depth: Int): JString = {
    val patternProp =
      schema.properties.getOrNone[PatternProperty].getOrElse(PatternProperty())
    val prefix = patternProp.prefix.getOrElse("")
    val suffix = patternProp.suffix.getOrElse("")

    val regex = schema.properties.getOrNone[StaticPatternProperty].map(_.regex)

    if ((!prefix.isEmpty || !suffix.isEmpty) && !regex.isEmpty) {
      throw new UnsupportedOperationException(
        "can't generate strings with prefix/suffix and regex"
      )
    }

    // Get the minimum length but ignore the prefix and suffix
    val minLength = (schema.properties
      .getOrNone[MinLengthProperty]
      .flatMap(_.minLength)
      .getOrElse(0) - prefix.length - suffix.length).max(0)

    val maxLength = schema.properties
      .getOrNone[MaxLengthProperty]
      .flatMap(_.maxLength)

    val formats =
      schema.properties
        .getOrNone[FormatProperty]
        .map(_.formats)
        .getOrElse(Map.empty)

    if (formats.isEmpty) {
      regex match {
        case Some(regexObj) =>
          val regexStr = regexObj.toString
          val (prefix, suffix) = if (regexStr.contains(".*")) {
            regexStr.stripPrefix("^").stripSuffix("$").split("\\.\\*") match {
              case Array(prefix, suffix) => (prefix, suffix)
              case _ =>
                throw new UnsupportedOperationException("regex unsupported")
            }
          } else if (regexStr.startsWith("^")) {
            (regexStr.stripPrefix("^"), "")
          } else if (regexStr.endsWith("$")) {
            ("", regexStr.stripSuffix("$"))
          } else {
            throw new UnsupportedOperationException("regex unsupported")
          }

          val minGenLength = (minLength - prefix.length - suffix.length).max(0)
          val maxGenLength = (maxLength.getOrElse(50) - prefix.length - suffix.length).max(0)
          val maxRand = maxGenLength - minGenLength + 1;
          val genLength = minGenLength + (Math.log(1 + util.Random.nextDouble) * maxRand).toInt
          val str = prefix + util.Random.alphanumeric.take(genLength).mkString("") + suffix
          JString(str)
        case None =>
          // Pick a length for the random part of the string
          // and build the string including prefix and suffix
          val maxLengthInt = maxLength.getOrElse(minLength + 10)
          val length =
            minLength + util.Random.nextInt(maxLengthInt - minLength + 1)
          val randString = util.Random.alphanumeric.take(length).mkString
          JString(prefix + randString + suffix)
      }
    } else {
      @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
      val format = formats.maxBy(_._2)._1
      val faker = new Faker()
      JString(format match {
        case "color"                          => faker.color.hex
        case "date"                           => randomDate("yyyy-MM-dd", faker)
        case "date-time"                      => randomDate("yyyy-MM-dd'T'HH:mm:ss+00:00", faker)
        case "email" | "idn-email"            => faker.internet.emailAddress
        case "host" | "hostname" | "idn-host" => faker.internet.domainName
        case "ipv4"                           => faker.internet.ipV4Address
        case "ipv6"                           => faker.internet.ipV6Address
        case "text" =>
          faker.lorem.characters(minLength, maxLength.getOrElse(minLength + 10))
        case "time" => randomDate("HH:mm:ss+00:00", faker)
        case "uuid" => faker.internet.uuid
        case "uri" | "url" | "uri-reference" | "iri" | "iri-reference" =>
          "https://" + faker.internet.url
        case _ =>
          throw new UnsupportedOperationException("unsupported format")
      })
    }
  }

  private def randomDate(format: String, faker: Faker): String = {
    val formatObj = new SimpleDateFormat(format)
    formatObj.format(faker.date.birthday)
  }
}
