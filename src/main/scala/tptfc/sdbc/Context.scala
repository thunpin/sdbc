package tptfc.sdbc

case class Context(conn: java.sql.Connection,
record: Record, params:List[(String, Any)])
