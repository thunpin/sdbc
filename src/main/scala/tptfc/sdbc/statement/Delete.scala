package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context

class Delete(entityName: String, context: Context) {
	def entity(obj: Any):DeleteResult = {
		val record = context.record
		val entry = record entryFrom entityName
		val whereFields = entry.tableKeys.map(field => field._1).toSeq
		val whereArgs = entry.getArgs(whereFields, obj)

		val whereSQL = whereFields.map(f => f + " = $" + f).mkString(" AND ")
		val where = new Where("WHERE", whereSQL, whereArgs:_*)

		exec(where, context)
	}

	def filter(filter: (String,Any), filters: (String,Any)*): DeleteResult = {
		val _filters = filter :: filters.toList
		var args:List[(String, Any)] = Nil
		var rules:List[String] = Nil

		var count = 0
		_filters.foreach(filter => {
			val key = "_w_arg" + count + ""
			val rule = filter._1 + " = $" + key

			rules = rule :: rules
			args = (key -> filter._2) :: args
			count = count + 1
		})

		val sql = rules.mkString(" AND ")
		where(sql, args:_*)
	}

	def where(where: String, args: (String, Any)*): DeleteResult = {
		val newWhere = new Where("WHERE", where, args:_*)
		exec(newWhere, context)
	}

	protected def exec(where: Where, context: Context): DeleteResult = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val (whereSQL, whereArgs) = where.parse(context)

		val sql = "DELETE FROM " + table + " AS _" + table + whereSQL
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
