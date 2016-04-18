package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context

class Insert(entityName: String, context: Context) {

	def entity(obj: Any): InsertResult = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val fields = entry.tableKeys.filter(key => !key._2).keys.toList :::
		 entry.tableFields
		val args = entry.getArgs(fields, obj)

		exec(table, fields, args)
	}

	def values(args: (String, Any)*): InsertResult = {
		val record = context.record
		val fields = args.map(p => p._1)
		val entry = record entryFrom entityName
		val table = entry.tableName

		exec(table, fields, args)
	}

	protected def exec(
	table: String,
	fields:Seq[String],
	args:Seq[(String,Any)]): InsertResult = {
		val sql = "INSERT INTO " + table + fields.mkString("(", ",", ")") +
							" VALUES " + fields.mkString("({", "} ,{", "})")

		implicit val conn = context.conn
		var result = SQL.insert(sql, args:_*)

		InsertResult(result, sql)
	}
}

case class InsertResult(result: Option[Long], sql: String)

object Insert {
	def apply(entityName: String, context: Context): Insert =
		new Insert(entityName, context)
}
