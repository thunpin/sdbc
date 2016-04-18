package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context
import tptfc.sdbc.Entry

case class Update(entityName: String, context: Context)
extends Statement(entityName, context) {

	def entity(obj: Any): UpdateResult = {
		val record = context.record
		val entry = (record entryFrom entityName).get
		val table = entry.tableName
		val fields = entry.allFields
		val args = entry.getArgs(fields, obj)
		val whereFields = entry.tableKeys.map(field => field._1).toSeq
		val whereArgs = entry.getArgs(whereFields, obj)

		val where = whereFields.map(f => f + " = {" + f + "}").mkString(" AND ")

		Update.exec(table, fields, args, where, whereArgs, context)
	}

	def values(args: (String, Any)*):UpdateWhere = {
		UpdateWhere(entityName, args, context)
	}
}

case class UpdateWhere(
entityName: String,
args:Seq[(String, Any)],
context: Context) {

	def where(where: String, whereArgs: (String,Any)*): UpdateResult = {
		val record = context.record
		val entry = (record entryFrom entityName).get
		val table = entry.tableName
		val fields = args.map(arg => arg._1)
		Update.exec(table, fields, args, where, whereArgs, context)
	}
}

case class UpdateResult(result: Long, sql: String)

object Update {
	def exec(table: String,
	fields:Seq[String],
	args:Seq[(String, Any)],
	where: String,
	whereArgs:Seq[(String, Any)],
	context: Context): UpdateResult = {
		val sql =
		"UPDATE " + table + " SET " +
		fields.map(field => field + " = {" + field + "}").mkString(", ") + where

		val allArgs = args.toList ::: whereArgs.toList

		implicit val conn = context.conn
		val result = SQL.executeUpdate(sql, allArgs:_*)
		UpdateResult(result, sql)
	}
}
