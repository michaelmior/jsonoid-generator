package edu.rit.cs.mmior.jsonoid.generator

import edu.rit.cs.mmior.jsonoid.discovery.schemas.NullSchema

import org.json4s._

object NullGenerator extends Generator[NullSchema, JValue] {
  def generate(schema: NullSchema, depth: Int): JValue = JNull
}
