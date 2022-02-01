package edu.rit.cs.mmior.jsonoid
package generator

import org.json4s._

import discovery._
import discovery.schemas._
import discovery.schemas.PropertySets._

class ArrayGeneratorSpec extends UnitSpec {
  behavior of "ArrayGenerator"

  private val itemType = BooleanSchema()
  private val arraySchema = ArraySchema(
    ArraySchema(List(itemType)).properties.mergeValue(List(itemType, itemType))
  )
  private val schemaList = List(NullSchema(), BooleanSchema(true))
  private val tupleSchema = ArraySchema(
    ArraySchema(schemaList).properties.mergeValue(schemaList)
  )

  it should "generate an array of appropriate length for a tuple schema" in {
    val tuple = ArrayGenerator.generate(tupleSchema)

    tuple.arr.length shouldBe 2
    tuple(0) shouldBe JNull
    tuple(1) shouldBe a[JBool]
  }

  it should "generate an array of appropriate length for an array schema" in {
    val arr = ArrayGenerator.generate(arraySchema)

    arr.arr.length should be <= 2
    all(arr.arr) shouldBe a[JBool]
  }

  it should "generate an array of unique elements" in {
    val props = SchemaProperties.empty[List[JsonSchema[_]]]
    props.add(ItemTypeProperty(Left(BooleanSchema())))
    props.add(MinItemsProperty(Some(2)))
    props.add(MaxItemsProperty(Some(2)))
    props.add(UniqueProperty(true, false))
    val uniqueArraySchema = ArraySchema(props)

    val arr = ArrayGenerator.generate(uniqueArraySchema)
    arr.arr should contain theSameElementsAs List(JBool(true), JBool(false))
  }
}
