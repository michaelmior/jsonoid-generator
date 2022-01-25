package edu.rit.cs.mmior.jsonoid
package generator

import discovery._

import org.scalatest.Checkpoints
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UnitSpec extends AnyFlatSpec with Checkpoints with Matchers {
  implicit val er: EquivalenceRelation =
    EquivalenceRelations.KindEquivalenceRelation
}
