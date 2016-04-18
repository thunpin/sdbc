package tptfc.sdbc.error

case class FieldNotFound(field: String) extends Exception(field)
