package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.BooleanSchema

import org.json4s._

object BooleanGenerator extends Generator[BooleanSchema, JBool] {
  def generate(
      schema: BooleanSchema,
      depth: Int,
      useExamples: Boolean
  ): JBool = {
    // Note: useExamples is ignored here since we only have two options
    JBool(util.Random.nextBoolean)
  }
}
