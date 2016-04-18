package tptfc.sdbc.error

case class EntityNotFound(entity: String) extends Exception(entity)
