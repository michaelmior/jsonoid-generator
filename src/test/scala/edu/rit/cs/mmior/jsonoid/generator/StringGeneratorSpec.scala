package edu.rit.cs.mmior.jsonoid
package generator

import java.net.URI
import java.time.{LocalDate, OffsetDateTime, OffsetTime}
import java.util.UUID
import org.json4s.{DefaultFormats, Formats}
import org.json4s._

import discovery._
import discovery.schemas._

class StringGeneratorSpec extends UnitSpec {
  behavior of "StringGenerator"

  implicit val formats: Formats = DefaultFormats

  private val stringSchema = StringSchema({
    val props = SchemaProperties.empty[String]
    props.add(MinLengthProperty(Some(3)))
    props.add(MaxLengthProperty(Some(10)))
    props
  })

  private val schemaRegex = "[0-9A-Z]+".r
  private val regexStringSchema = StringSchema({
    val props = SchemaProperties.empty[String]
    props.add(MinLengthProperty(Some(3)))
    props.add(MaxLengthProperty(Some(10)))
    props.add(StaticPatternProperty(schemaRegex))
    props
  })

  private def schemaWithFormat(format: String): StringSchema = {
    val props = SchemaProperties.empty[String]
    props.add(FormatProperty(Map(format -> 1)))

    StringSchema(props)
  }

  it should "generate a valid string with length bounds" in {
    val str = StringGenerator.generate(stringSchema).extract[String]
    str.length should (be >= 3 and be <= 10)
  }

  it should "generate a valid string" in {
    val str = StringGenerator.generate(regexStringSchema).extract[String]
    str should fullyMatch regex schemaRegex
    str.length should (be >= 3 and be <= 10)
  }

  it should "generate a date" in {
    val dateSchema = schemaWithFormat("date")
    val str = StringGenerator.generate(dateSchema).extract[String]
    noException should be thrownBy LocalDate.parse(str)
  }

  it should "generate a date-time" in {
    val dateTimeSchema = schemaWithFormat("date-time")
    val str = StringGenerator.generate(dateTimeSchema).extract[String]
    noException should be thrownBy OffsetDateTime.parse(str)
  }

  it should "generate a time" in {
    val timeSchema = schemaWithFormat("time")
    val str = StringGenerator.generate(timeSchema).extract[String]
    noException should be thrownBy OffsetTime.parse(str)
  }

  it should "generate a uri" in {
    val uriSchema = schemaWithFormat("uri")
    val str = StringGenerator.generate(uriSchema).extract[String]
    noException should be thrownBy new URI(str).getScheme().length
  }

  it should "generate a uuid" in {
    val uuidSchema = schemaWithFormat("uuid")
    val str = StringGenerator.generate(uuidSchema).extract[String]
    noException should be thrownBy UUID.fromString(str)
  }

  it should "generate a host" in {
    val hostSchema = schemaWithFormat("host")
    val str = StringGenerator.generate(hostSchema).extract[String]
    // https://stackoverflow.com/a/26093611/123695
    str should fullyMatch regex """^[a-zA-Z0-9][a-zA-Z0-9-]{1,61}[a-zA-Z0-9](?:\.[a-zA-Z]{2,})+$""".r
  }

  it should "generate an email" in {
    val emailSchema = schemaWithFormat("email")
    val str = StringGenerator.generate(emailSchema).extract[String]
    // https://stackoverflow.com/a/32445372/123695
    str should fullyMatch regex """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
  }

  it should "generate an IPv4" in {
    val ipv4Schema = schemaWithFormat("ipv4")
    val str = StringGenerator.generate(ipv4Schema).extract[String]
    str should fullyMatch regex "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)".r
  }

  it should "generate an IPv6" in {
    val ipv6Schema = schemaWithFormat("ipv6")
    val str = StringGenerator.generate(ipv6Schema).extract[String]
    str should fullyMatch regex "^(?:(?:(?:[a-fA-F0-9]{1,4}:){6}|(?=(?:[A-F0-9]{0,4}:){0,6}(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$)(([0-9a-fA-F]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:))(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|(?:[a-fA-F0-9]{1,4}:){7}[a-fA-F0-9]{1,4}|(?=(?:[a-fA-F0-9]{0,4}:){0,7}[a-fA-F0-9]{0,4}$)(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:))$".r
  }
}
