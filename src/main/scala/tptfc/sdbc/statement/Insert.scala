package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context
import tptfc.sdbc.Entry
import tptfc.sdbc.error.EntityNotFound

case class Insert(entityName: String, context: Context) {
	validate()
	private def validate(): Unit = {
		val record = context.record
		record entryFrom entityName match {
			case None =>
				throw EntityNotFound()
			case _ =>
		}
	}

	def value(entity: Any): InsertResult = {
		val record = context.record
		val entry = (record entryFrom entityName).get
		val table = entry.tableName
		val fields = entry.tableKeys.filter(key => !key._2).keys.toList :::
		 entry.tableFields
		val args = entry.getArgs(fields, entity)

		exec(table, fields, args)
	}

	def values(args: (String, Any)*): InsertResult = {
		val record = context.record
		val fields = args.map(p => p._1)
		val entry = (record entryFrom entityName).get
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
