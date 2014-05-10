import sbt._
import Keys._
import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._
import com.typesafe.sbt.packager.archetypes.ServerLoader.SystemV

object ApplicationBuild extends Build {
  override lazy val settings = super.settings ++
    Seq(
      name := "vlok",
      version := "0.3.2",
      organization := "nl.gideondk",
      scalaVersion := "2.10.2",
      parallelExecution in Test := false,
      resolvers ++= Seq(Resolver.mavenLocal,
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
        "gideondk-repo" at "https://raw.github.com/gideondk/gideondk-mvn-repo/master"),
      publishTo := Some(Resolver.file("file", new File("/Users/gideondk/Development/gideondk-mvn-repo")))
    )

  val debianPackageSettings = packageArchetype.java_server ++  Seq(
    packageSummary in Debian := "Generate GUIDS",
    packageDescription in Debian := "Generate K-sorted unique ids",
    maintainer in Debian := "Gideon de Kok <gideondk@me.com>",

    serverLoading in Debian := SystemV,

    bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf""""
  ) ++ Seq(mappings in Universal <++= (packageBin in Compile, sourceDirectory) map { (_, src) =>
    // we are using the reference.conf as default application.conf
    // the user can override settings here
    val conf = src / "main" / "resources" / "application.conf"
    val logConf = src / "main" / "resources" / "logback.xml"
    Seq(conf -> "conf/application.conf", logConf -> "conf/logback.xml")
  })


  val appDependencies = Seq(
    "nl.gideondk" %% "nucleus" % "0.1.3",
    "com.typesafe" % "config" % "1.2.0",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",

    "org.specs2" %% "specs2" % "1.13" % "test"
  )

  lazy val root = Project(
    id = "vlok",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      libraryDependencies ++= appDependencies,
      mainClass := Some("Main")
    ) ++ Format.settings ++ debianPackageSettings)
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

