name := "ninja"
organization in ThisBuild := "ninja"
scalaVersion in ThisBuild := "2.12.3"

// PROJECTS

lazy val global = project
  .in(file("."))
  .settings(settings)
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    GameModule,
    PlayerModule
  )

lazy val GameModule = project
  .settings(
    name := "GameModule",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .aggregate(PlayerModule)
  .dependsOn(PlayerModule)

lazy val PlayerModule = project
  .settings(
    name := "PlayerModule",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )

// DEPENDENCIES

lazy val dependencies =
  new {
    val akkaVersion     = "2.5.12"
    val akkaHttp        = "10.1.1"
    val logbackV        = "1.2.3"
    val logstashV       = "4.11"
    val scalaLoggingV   = "3.7.2"
    val slf4jV          = "1.7.25"
    val typesafeConfigV = "1.3.1"
    val pureconfigV     = "0.8.0"
    val monocleV        = "1.4.0"
    val akkaV           = "2.5.6"
    val scalatestV      = "3.0.4"
    val scalacheckV     = "1.13.5"

    val logback        = "ch.qos.logback"             % "logback-classic"          % logbackV
    val logstash       = "net.logstash.logback"       % "logstash-logback-encoder" % logstashV
    val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"           % scalaLoggingV
    val slf4j          = "org.slf4j"                  % "jcl-over-slf4j"           % slf4jV
    val typesafeConfig = "com.typesafe"               % "config"                   % typesafeConfigV
    val akka           = "com.typesafe.akka"          %% "akka-stream"             % akkaV
    val monocleCore    = "com.github.julien-truffaut" %% "monocle-core"            % monocleV
    val monocleMacro   = "com.github.julien-truffaut" %% "monocle-macro"           % monocleV
    val pureconfig     = "com.github.pureconfig"      %% "pureconfig"              % pureconfigV
    val scalatest      = "org.scalatest"              %% "scalatest"               % scalatestV
    val scalacheck     = "org.scalacheck"             %% "scalacheck"              % scalacheckV
  }

lazy val commonDependencies = Seq(
  dependencies.logback,
  dependencies.logstash,
  dependencies.scalaLogging,
  dependencies.slf4j,
  dependencies.typesafeConfig,
  dependencies.akka,
  dependencies.scalatest   % "test",
  dependencies.scalacheck  % "test",
  "org.scalatest"          %% "scalatest" % "3.0.1" % "test",
  "org.scala-lang.modules" % "scala-swing_2.12" % "2.0.3",
  "com.google.inject"      % "guice" % "4.1.0",
  "net.codingwell"         %% "scala-guice" % "4.1.0",
  "org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6",
  "com.typesafe.play"      %% "play-json" % "2.6.6",
  "org.scala-lang.modules" % "scala-swing_2.12" % "2.0.3",
  "org.scalafx"            %% "scalafx" % "11-R16",
  "com.typesafe.akka"      %% "akka-actor" % dependencies.akkaVersion,
  "com.typesafe.akka"      %% "akka-http-core" % dependencies.akkaHttp,
  "com.typesafe.akka"      %% "akka-http" % dependencies.akkaHttp,
  "com.typesafe.play"      %% "play-ws-standalone-json" % "1.1.8",
  "com.typesafe.akka"      %% "akka-slf4j" % dependencies.akkaVersion,
  "ch.qos.logback"         % "logback-classic" % "1.2.3",
  "de.heikoseeberger"      %% "akka-http-play-json" % "1.17.0",
  "com.typesafe.akka"      %% "akka-testkit" % dependencies.akkaVersion % "test",
  "com.typesafe.akka"      %% "akka-http-spray-json" % dependencies.akkaHttp,
  "com.typesafe.akka"      %% "akka-http-xml" % dependencies.akkaHttp,
  "com.typesafe.akka"      %% "akka-stream" % dependencies.akkaVersion,
  "com.typesafe.akka"      %% "akka-stream-testkit" % dependencies.akkaVersion % Test,
  "org.scala-lang.modules" %% "scala-async" % "0.10.0",
)

// SETTINGS

lazy val settings =
commonSettings ++
wartremoverSettings ++
scalafmtSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val wartremoverSettings = Seq(
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case "application.conf"            => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)
