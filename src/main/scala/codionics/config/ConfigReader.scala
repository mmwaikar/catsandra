package codionics.config

import cats.effect.IO
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

trait ConfigReader {

  def load(filename: String = "config/application.conf"): IO[Config]
}

class ConfigReaderImpl extends ConfigReader {

  override def load(filename: String): IO[Config] = {
    ConfigSource.file(filename).loadF[IO, Config]
  }
}