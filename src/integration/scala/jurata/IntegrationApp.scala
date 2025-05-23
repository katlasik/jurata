package jurata
import jurata.{*, given}

case class DbConfig(
  @env("DB_HOST") @prop("db.host") host: String,
  @env("DB_PORT") @prop("db.port") port: Int,
  @env("DB_USER") @prop("db.user") user: String,
  @env("DB_PASSWORD") @prop("db.password") password: Secret[String]
) derives ConfigValue

case class Config(
  @env("PORT") @prop("http.port") port: Int,
  @env("HOST") @prop("http.host") host: String,
  dbConfig: DbConfig
) derives ConfigValue

@main
def app(): Unit =

  System.getProperties.entrySet().forEach(println)

  load[Config] match {
    case Right(config) => println(config)
    case Left(error)   => throw new RuntimeException("Failed to load config", error)
  }
