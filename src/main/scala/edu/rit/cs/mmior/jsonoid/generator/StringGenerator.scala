package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.{
  FormatProperty,
  MaxLengthProperty,
  MinLengthProperty,
  PatternProperty,
  StaticPatternProperty,
  StringSchema
}

import com.github.curiousoddman.rgxgen.RgxGen
import com.github.curiousoddman.rgxgen.config.{RgxGenOption,RgxGenProperties}
import com.github.javafaker.Faker
import java.text.SimpleDateFormat
import org.json4s._

object StringGenerator extends Generator[StringSchema, JString] {
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def generate(schema: StringSchema): JString = {
    val patternProp =
      schema.properties.getOrNone[PatternProperty].getOrElse(PatternProperty())
    val prefix = patternProp.prefix.getOrElse("")
    val suffix = patternProp.suffix.getOrElse("")

    val regex =
      schema.properties.getOrNone[StaticPatternProperty].map(_.regex)

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
          val genProps = new RgxGenProperties()
          RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(genProps, 5)
          val generator = new RgxGen(regexObj.toString)
          generator.setProperties(genProps)

          if (minLength == 0 && maxLength.isEmpty) {
            JString(generator.generate())
          } else {
            throw new UnsupportedOperationException("regexes not supported with length constraints")
          }
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
        case "date"                => randomDate("yyyy-MM-dd", faker)
        case "date-time"           => randomDate("yyyy-MM-dd'T'HH:mm:ss+00:00", faker)
        case "email" | "idn-email" => faker.internet.emailAddress
        case "host" | "idn-host"   => faker.internet.domainName
        case "ipv4"                => faker.internet.ipV4Address
        case "ipv6"                => faker.internet.ipV6Address
        case "time"                => randomDate("HH:mm:ss+00:00", faker)
        case "uuid"                => faker.internet.uuid
        case "uri" | "iri" | "uri-reference" | "iri-reference" =>
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
