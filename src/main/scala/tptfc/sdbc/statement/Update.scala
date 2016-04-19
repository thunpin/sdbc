package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context

class Update(entityName: String, context: Context) {

	def entity(obj: Any): UpdateResult = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val fields = entry.tableAllFields
		val args = entry.getArgs(fields, obj)
		val whereFields = entry.tableKeys.map(field => field._1).toSeq
		val whereArgs = entry.getArgs(whereFields, obj)

		val where = whereFields.mkString("$", " AND $", "")

		Update.exec(table, fields, args, where, whereArgs, context)
	}

	def values(args: (String, Any)*):UpdateWhere = {
		new UpdateWhere(entityName, args, context)
	}
}

class UpdateWhere(
entityName: String,
args:Seq[(String, Any)],
context: Context) {

	def where(where: String, whereArgs: (String,Any)*): UpdateResult = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val fields = args.map(arg => arg._1)
		Update.exec(table, fields, args, where, whereArgs, context)
	}
}

case class UpdateResult(result: Long, sql: String)

object Update {
	def apply(entityName: String, context: Context): Update =
		new Update(entityName, context)

	def exec(table: String,
	fields:Seq[String],
	args:Seq[(String, Any)],
	where: String,
	whereArgs:Seq[(String, Any)],
	context: Context): UpdateResult = {
		val sql =
		"UPDATE " + table + " SET " +
		fields.mkString("$", ", $", "") + where

		val allArgs = args.toList ::: whereArgs.toList

		implicit val conn = context.conn
		val result = SQL.executeUpdate(sql, allArgs:_*)
		UpdateResult(result, sql)
	}
}
