package jurata.utils

import jurata.ConfigDecoder
import jurata.ConfigError

private[jurata] class EnumConfigDecoder[T](values: Array[T], enumName: String)
    extends ConfigDecoder[T] {
  override def decode(raw: String): Either[ConfigError, T] =

    values.toList
      .find(_.toString == raw)
      .toRight(
        ConfigError.invalid(
          s"couldn't find case for enum $enumName (available values: ${values.mkString(", ")})",
          raw
        )
      )
}
