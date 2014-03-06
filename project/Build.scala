import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object ApplicationBuild extends Build {
  override lazy val settings = super.settings ++
    Seq(
      name := "vlok",
      version := "0.3.1",
      organization := "nl.gideondk",
      scalaVersion := "2.10.2",
      parallelExecution in Test := false,
      resolvers ++= Seq(Resolver.mavenLocal,
        "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
        "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
        "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
        "gideondk-repo" at "https://raw.github.com/gideondk/gideondk-mvn-repo/master"),
      publishTo := Some(Resolver.file("file", new File("/Users/gideondk/Development/gideondk-mvn-repo")))
    )

  val appDependencies = Seq(
    "org.specs2" %% "specs2" % "1.13",
    
    "nl.gideondk" %% "nucleus" % "0.1.1"
  )

  lazy val root = Project(id = "vlok",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      libraryDependencies ++= appDependencies,
      mainClass := Some("Main")
    ) ++ Format.settings ++ assemblySettings 
  ) settings (
    mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
      case ".DS_Store" => MergeStrategy.discard
      case "application.conf" => MergeStrategy.concat
      case x => old(x)
    }
  },
    mainClass in assembly := Some("nl.gideondk.vlok.Main")
  )
}

object Format {

  import com.typesafe.sbt.SbtScalariform._

  lazy val settings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences := formattingPreferences
  )

  lazy val formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences().
      setPreference(AlignParameters, true).
      setPreference(AlignSingleLineCaseStatements, true).
      setPreference(DoubleIndentClassDeclaration, true).
      setPreference(IndentLocalDefs, true).
      setPreference(IndentPackageBlocks, true).
      setPreference(IndentSpaces, 2).
      setPreference(MultilineScaladocCommentsStartOnFirstLine, true).
      setPreference(PreserveSpaceBeforeArguments, false).
      setPreference(PreserveDanglingCloseParenthesis, false).
      setPreference(RewriteArrowSymbols, true).
      setPreference(SpaceBeforeColon, false).
      setPreference(SpaceInsideBrackets, false).
      setPreference(SpacesWithinPatternBinders, true)
  }
}

