package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context

class Insert(entityName: String, iContext: InsertContext, context: Context) {

	def entity[T](obj: T): Insert = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val fields = entry.tableKeys.filter(key => !key._2).keys.toList :::
		  entry.tableFields
		val args = entry.getArgs(fields, obj)

		val iContext = InsertContext(table, fields, args)
		new Insert(entityName, iContext, context)
	}

	def values(args: (String, Any)*): Insert = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val fields = args.map(p => entry tableFieldFrom p._1)

		val iContext = InsertContext(table, fields, args)
		new Insert(entityName, iContext, context)
	}

	def exec: InsertResult = {
	  val table = iContext.table
	  val fields = iContext.fields
	  val args = iContext.args
		val sql = "INSERT INTO " + table + fields.mkString("(", ",", ")") +
							" VALUES " + fields.mkString("($", ", $", ")")

		implicit val conn = context.conn
		var result = SQL.insert(sql, args:_*)

		InsertResult(result, sql)
	}
}

case class InsertContext(
  table: String,
  fields:Seq[String],
	args:Seq[(String,Any)])

case class InsertResult(id: Option[Long], sql: String)

object Insert {
	def apply(entityName: String, context: Context): Insert =
		new Insert(entityName, InsertContext("", Nil, Nil), context)
}
