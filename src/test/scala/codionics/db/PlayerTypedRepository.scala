package codionics.db

import codionics.domain.Player

class PlayerTypedRepository extends AbstractGenericTypedRepository[Player, String] {

  override def tableName: String = "players"

  override def pkColumnName: String = "nickname"

  override def isPKAutoGenerated: Boolean = false
}
