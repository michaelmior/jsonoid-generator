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
    validator: Option[File] = None,
    count: Int = 1,
    examples: Boolean = false,
    input_properties: Option[Seq[String]] = None,
    validator_properties: Option[Seq[String]] = None,
    validator_valid: Option[Boolean] = None,
    seed: Option[Int] = None
)

object GeneratorCLI {
  def convertSchema(
      schemaSource: Source,
      limitProperties: Option[Seq[String]]
  ): JsonSchema[_] = {
    val schema =
      parse(schemaSource.getLines().mkString("\n")).asInstanceOf[JObject]
    val jsonoidSchema = JsonSchema.fromJson(schema)
    val resolvedSchema = ReferenceResolver.transformSchema(jsonoidSchema)(
      EquivalenceRelations.NonEquivalenceRelation
    )

    // Limit to only the desired properties
    limitProperties match {
      case Some(props) => resolvedSchema.onlyPropertiesNamed(props)
      case None        => resolvedSchema
    }
  }

  // $COVERAGE-OFF$ No automated testing of CLI
  @SuppressWarnings(
    Array("org.wartremover.warts.Equals", "org.wartremover.warts.While")
  )
  def main(args: Array[String]): Unit = {
    val parser = new OptionParser[Config]("jsonoid-generator") {
      head("jsonoid-generator", BuildInfo.version)

      help("help")

      arg[File]("<input>")
        .optional()
        .action((x, c) => c.copy(input = Some(x)))
        .text("a JSON schema to use for data generation")

      opt[File]('v', "validator")
        .optional()
        .action((x, c) => c.copy(validator = Some(x)))
        .text("a JSON schema to use for validation")

      opt[Int]('c', "count")
        .optional()
        .action((x, c) => c.copy(count = x))
        .text("the number of JSON documents to generate")

      opt[Unit]('e', "examples")
        .optional()
        .action((_, c) => c.copy(examples = true))
        .text("generate values only from examples")

      opt[Seq[String]]('p', "properties")
        .optional()
        .action((x, c) => c.copy(input_properties = Some(x)))
        .text("use only specified properties on the input schema")

      opt[Seq[String]]('q', "validator-properties")
        .optional()
        .action((x, c) => c.copy(validator_properties = Some(x)))
        .text("use only specified properties on the validator schema")

      opt[Boolean]('t', "validator-valid")
        .optional()
        .action((x, c) => c.copy(validator_valid = Some(x)))
        .text(
          "only produce schemas which are (or are not) valid against the validator schema"
        )

      opt[Int]('s', "seed")
        .optional()
        .action((x, c) => c.copy(seed = Some(x)))
        .text("random number seed")
    }

    parser.parse(args, Config()) match {
      case Some(config) =>
        val source = config.input match {
          case Some(file) => Source.fromFile(file)
          case None       => Source.stdin
        }
        val inputSchema = convertSchema(source, config.input_properties)
        val validatorSchema: Option[JsonSchema[_]] = config.validator match {
          case Some(file) =>
            Some(
              convertSchema(Source.fromFile(file), config.validator_properties)
            )
          case None => None
        }

        // Set the seed for the RNG
        if (config.seed.isDefined) {
          util.Random.setSeed(config.seed.get)
        }

        var passedSchemas = 0
        var failedSchemas = 0
        (1 to config.count).foreach { _ =>
          var json =
            Generator.generateFromSchema(inputSchema, config.examples)
          if (config.validator_valid.isDefined) {
            while (
              validatorSchema.get.isAnomalous(
                json
              ) == config.validator_valid.get
            ) {
              json = Generator.generateFromSchema(inputSchema, config.examples)
              System.err.println(validatorSchema.get.collectAnomalies(json))
            }
          }

          if (validatorSchema.isDefined) {
            if (validatorSchema.get.isAnomalous(json)) {
              failedSchemas += 1
            } else {
              passedSchemas += 1
            }
          }
          println(compact(render(json)))
        }

        if (validatorSchema.isDefined) {
          System.err.println(f"  Valid: ${passedSchemas}")
          System.err.println(f"Invalid: ${failedSchemas}")
        }
      case None =>
    }
  }
  // $COVERAGE-ON$
}
