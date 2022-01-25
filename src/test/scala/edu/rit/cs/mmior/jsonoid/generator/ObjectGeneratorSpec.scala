package edu.rit.cs.mmior.jsonoid
package generator

import org.json4s._

import discovery._
import discovery.schemas._
import discovery.schemas.PropertySets._

class ObjectGeneratorSpec extends UnitSpec {
  behavior of "ObjectGenerator"

  private val objectTypes =
    Map("foo" -> BooleanSchema(), "bar" -> BooleanSchema())
  private val schemaProperties = ObjectSchema(
    Map("foo" -> BooleanSchema())
  ).properties.mergeValue(objectTypes)
  private val objectSchema = ObjectSchema(schemaProperties)

  it should "include all required properties" in {
    val obj = ObjectGenerator.generate(objectSchema)
    obj.obj.map(_._1) should contain("foo")
    obj.obj.find(_._1 == "foo").get._2 shouldBe a[JBool]
  }
}
