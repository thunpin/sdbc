package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context
import tptfc.sdbc.Entry
import tptfc.sdbc.error.EntityNotFound

case class Insert(entityName: String, context: Context) {
	def Exec(): Insert = {
		val record = context.record
		record entryFrom entityName match {
			case Some(entry:Entry) =>
				Insert(entityName, context)
			case None =>
				throw EntityNotFound()
		}
	}

	def value(entity: Any): InsertResult = {
		val record = context.record
		val entry = (record entryFrom entityName).get
		val table = entry.tableName
		val fields = entry.tableKeys.filter(key => !key._2).keys.toList :::
		 entry.tableFields

		val sql = "INSERT INTO " + table + fields.mkString("(", ",", ")") +
							" VALUES " + fields.mkString("({", "} ,{", "})")

		val args = entry.getArgs(fields, entity)
		implicit val conn = context.conn
		var result = SQL.insert(sql, args:_*)

		InsertResult(result, sql)
	}
}

case class InsertResult(result: Option[Long], sql: String)
