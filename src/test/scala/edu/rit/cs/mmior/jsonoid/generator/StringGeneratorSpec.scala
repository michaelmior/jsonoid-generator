package edu.rit.cs.mmior.jsonoid
package generator

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

  it should "generate a valid string with length bounds" in {
    val str = StringGenerator.generate(stringSchema).extract[String]
    str.length should (be >= 3 and be <= 10)
  }

  it should "generate a valid string" in {
    val str = StringGenerator.generate(regexStringSchema).extract[String]
    str should fullyMatch regex schemaRegex
    str.length should (be >= 3 and be <= 10)
  }
}
