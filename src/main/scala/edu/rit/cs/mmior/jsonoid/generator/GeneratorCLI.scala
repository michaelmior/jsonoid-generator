package edu.rit.cs.mmior.jsonoid.generator

import java.io.File
import scala.io.Source

import org.json4s._
import org.json4s.jackson.JsonMethods._
import scopt.OptionParser

import edu.rit.cs.mmior.jsonoid.discovery.{
  EquivalenceRelations,
  ReferenceResolver
}
import edu.rit.cs.mmior.jsonoid.discovery.schemas.JsonSchema

final case class Config(
    input: Option[File] = None,
    count: Int = 1,
    examples: Boolean = false
)

object GeneratorCLI {
  // $COVERAGE-OFF$ No automated testing of CLI
  def main(args: Array[String]): Unit = {
    val parser = new OptionParser[Config]("jsonoid-generator") {
      head("jsonoid-generator", BuildInfo.version)

      help("help")

      arg[File]("<input>")
        .optional()
        .action((x, c) => c.copy(input = Some(x)))
        .text("a JSON schema to use for data generation")

      opt[Int]('c', "count")
        .optional()
        .action((x, c) => c.copy(count = x))
        .text("the number of JSON documents to generate")

      opt[Boolean]('e', "examples")
        .optional()
        .action((x, c) => c.copy(examples = x))
        .text("generate values only from examples")
    }

    parser.parse(args, Config()) match {
      case Some(config) =>
        val source = config.input match {
          case Some(file) => Source.fromFile(file)
          case None       => Source.stdin
        }
        val schema =
          parse(source.getLines().mkString("\n")).asInstanceOf[JObject]
        val jsonoidSchema = JsonSchema.fromJson(schema)
        val resolvedSchema = ReferenceResolver.transformSchema(jsonoidSchema)(
          EquivalenceRelations.NonEquivalenceRelation
        )

        (1 to config.count).foreach { _ =>
          val json =
            Generator.generateFromSchema(resolvedSchema)
          println(compact(render(json)))
        }
      case None =>
    }
  }
  // $COVERAGE-ON$
}
