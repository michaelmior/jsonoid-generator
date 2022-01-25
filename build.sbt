import Dependencies._

ThisBuild / scalaVersion      := "2.11.12"
ThisBuild / versionScheme     := Some("early-semver")
ThisBuild / organization      := "edu.rit.cs"
ThisBuild / organizationName  := "Rochester Institute of Technology"
ThisBuild / githubOwner       := "michaelmior"
ThisBuild / githubRepository  := "jsonoid-web"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .settings(
    name := "JSONoid Generator",
    resolvers += Resolver.githubPackages("michaelmior"),
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies ++= Seq(
      jsonoid,

      scalaTest % Test,
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Xfatal-warnings",
    )
  )

wartremoverErrors ++= Seq(
  Wart.ArrayEquals,
  Wart.EitherProjectionPartial,
  Wart.Enumeration,
  Wart.Equals,
  Wart.ExplicitImplicitTypes,
  Wart.FinalCaseClass,
  Wart.MutableDataStructures,
  Wart.Null,
  Wart.Option2Iterable,
  Wart.PublicInference,
  Wart.Recursion,
  Wart.Return,
  Wart.StringPlusAny,
  Wart.TraversableOps,
  Wart.TryPartial,
  Wart.While,
)

enablePlugins(GitVersioning)
enablePlugins(GitHubPagesPlugin)
enablePlugins(SiteScaladocPlugin)

gitHubPagesOrgName := "michaelmior"
gitHubPagesRepoName := "jsonoid-generator"
gitHubPagesSiteDir := baseDirectory.value / "target/site"

git.remoteRepo := "git@github.com:michaelmior/jsonoid-generator.git"
git.useGitDescribe := true
