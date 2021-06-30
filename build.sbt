import scoverage.ScoverageKeys

name := """client-backend"""
organization := "example.com"

version := "1.0-SNAPSHOT"


lazy val root = (project in file(".")).enablePlugins(PlayScala)
	.settings(routesGenerator := InjectedRoutesGenerator)
	.configs(ITest)
	.settings( inConfig(ITest)(Defaults.testSettings) : _*)
	.settings(
		fork in IntegrationTest := false,
		unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest)(base => Seq(base / "it")).value,
		unmanagedResourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest)(base => Seq(base / "it" / "resources")).value)
	.settings(scoverageSettings)
lazy val ITest = config("it") extend(Test)

lazy val scoverageSettings = Seq(
	ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;config.*;.*(AuthService|BuildInfo|Routes).*",
	ScoverageKeys.coverageMinimumStmtTotal := 95,
	ScoverageKeys.coverageFailOnMinimum := true,
	ScoverageKeys.coverageHighlighting := true
)

scalaVersion := "2.12.13"

resolvers += "HMRC-open-artefacts-maven2" at "https://open.artefacts.tax.service.gov.uk/maven2"

PlayKeys .devSettings := Seq ("play.server.http.port" -> "9006")

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.28.2" % Test
libraryDependencies += "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.49.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0-RC2"
libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.2.5.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.5"

resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-28" % "0.49.0"




// Adds additional packages into Twirl
//TwirlKeys.templateImports += "example.com.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "example.com.binders._"
