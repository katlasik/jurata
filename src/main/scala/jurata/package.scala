package jurata

import jurata.{ConfigDecoder, ConfigError, ConfigLoader, ConfigValue}

import java.io.File
import java.net.{InetAddress, UnknownHostException}
import java.util.UUID
import scala.annotation.implicitNotFound
import java.nio.file.{InvalidPathException, Path, Paths}

given [T: ConfigDecoder] => ConfigDecoder[Option[T]] {
  override def decode(raw: String): Either[ConfigError, Option[T]] = ConfigDecoder[T].decode(raw).map(Some(_))

  override def isOption: Boolean = true
}

given [T: ConfigValue] => ConfigLoader[Option[T]] {
  override def load(reader: ConfigReader): Either[ConfigError, Option[T]] = ConfigValue[T] match {
    case loader: ConfigLoader[T] => loader.load(reader).map(Some(_))
    case decoder: ConfigDecoder[T] => Left(ConfigError.other("You can only load case class!"))
  }

  override def isOption: Boolean = true
}

given ConfigDecoder[String] with {
  override def decode(raw: String): Either[ConfigError, String] = Right(raw)
}

given ConfigDecoder[Short] with {
  override def decode(raw: String): Either[ConfigError, Short] = try
    Right(raw.toShort)
  catch
    case _: NumberFormatException => Left(ConfigError.invalid("Can't decode short value", raw))
}


given ConfigDecoder[Int] with {
  override def decode(raw: String): Either[ConfigError, Int] = try
    Right(raw.toInt)
  catch
    case _: NumberFormatException => Left(ConfigError.invalid(s"was expecting integer", raw))
}


given ConfigDecoder[Long] with {
  override def decode(raw: String): Either[ConfigError, Long] = try
    Right(raw.toLong)
  catch
    case _: NumberFormatException => Left(ConfigError.invalid(s"was expecting long", raw))
}

given ConfigDecoder[Float] with {
  override def decode(raw: String): Either[ConfigError, Float] = try
    Right(raw.toFloat)
  catch
    case _: NumberFormatException => Left(ConfigError.invalid(s"Can't decode double value", raw))
}

given ConfigDecoder[Double] with {
  override def decode(raw: String): Either[ConfigError, Double] = try
    Right(raw.toDouble)
  catch
    case _: NumberFormatException => Left(ConfigError.invalid(s"Can't decode double value", raw))
}

given ConfigDecoder[BigDecimal] with {
  override def decode(raw: String): Either[ConfigError, BigDecimal] = try
    Right(BigDecimal(raw))
  catch
    case _: NumberFormatException => Left(ConfigError.invalid(s"Can't decode BigDecimal value", raw))
}

given ConfigDecoder[BigInt] with {
  override def decode(raw: String): Either[ConfigError, BigInt] = try
    Right(BigInt(raw))
  catch
    case _: NumberFormatException => Left(ConfigError.invalid(s"Can't decode BigInt value", raw))
}

given ConfigDecoder[InetAddress] with {
  override def decode(raw: String): Either[ConfigError, InetAddress] = try
    Right(InetAddress.getByName(raw))
  catch
    case _: UnknownHostException => Left(ConfigError.invalid(s"Can't decode InetAddress value", raw))
}

given ConfigDecoder[UUID] with { 
  override def decode(raw: String): Either[ConfigError, UUID] = try
    Right(UUID.fromString(raw))
  catch
    case _: IllegalArgumentException => Left(ConfigError.invalid(s"Can't decode UUID value", raw))
}

given ConfigDecoder[Path] with {
  override def decode(raw: String): Either[ConfigError, Path] = try
    Right(Paths.get(raw))
  catch
    case _: InvalidPathException => Left(ConfigError.invalid(s"Can't decode Path value", raw))
}

given ConfigDecoder[File] with {
  override def decode(raw: String): Either[ConfigError, File] = Right(new File(raw))
}

given ConfigDecoder[Boolean] with {
  override def decode(raw: String): Either[ConfigError, Boolean] = raw.toLowerCase match
    case "true" | "yes" | "1" => Right(true)
    case "false" | "no" | "0" => Right(false)
    case _ => Left(ConfigError.invalid(s"Can't decode boolean value", raw))
}

def load[C](using @implicitNotFound("Can't find required givens. Did you forget to use derives for your case classes?") cv: ConfigValue[C], reader: ConfigReader): Either[ConfigError, C] = cv match {
  case loader: ConfigLoader[C] => loader.load(reader)
  case decoder: ConfigDecoder[C] => Left(ConfigError.other("You can only load case class!"))
}

given ConfigReader = LiveConfigReader

