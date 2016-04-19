package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context

class Delete(entityName: String, context: Context) {
	def entity(obj: Any):DeleteResult = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val whereFields = entry.tableKeys.map(field => field._1).toSeq
		val whereArgs = entry.getArgs(whereFields, obj)

		val where = whereFields.mkString("$", " AND $", "")

		exec(table, where, whereArgs, context)
	}

	def where(where: String, whereArgs: (String, Any)*): DeleteResult = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName

		exec(table, where, whereArgs, context)
	}

	protected def exec(
								table: String,
								where: String,
								whereArgs:Seq[(String, Any)],
								context: Context): DeleteResult = {
		val sql = "DELETE FROM " + table + " WHERE " + where
		implicit val conn = context.conn
		val result = SQL.executeUpdate(sql, whereArgs:_*)
		DeleteResult(result, sql)
	}
}

case class DeleteResult(result: Long, sql: String)

object Delete {
	def apply(entityName: String, context: Context): Delete =
		new Delete(entityName, context)
}
