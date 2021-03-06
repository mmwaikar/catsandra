package codionics.db

import codionics.domain._

class PlayerRepository extends AbstractRepository[Player, String] {

  override def tableName: String = "players"

  override def pkColumnName: String = "nickname"
}
