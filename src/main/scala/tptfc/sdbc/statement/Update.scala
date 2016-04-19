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

		val where = whereFields.map(f => f + " = $" + f).mkString(" AND ")
		val _where = new Where("WHERE", where, whereArgs:_*)

		Update.exec(table, fields, args, _where, context)
	}

	def values(args: (String, Any)*):UpdateWhere = {
		new UpdateWhere(entityName, args, context)
	}
}

class UpdateWhere(
entityName: String,
args:Seq[(String, Any)],
context: Context) {

	def filter(filter: (String,Any), filters: (String,Any)*): UpdateResult = {
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

	def where(where: String, whereArgs: (String,Any)*): UpdateResult = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val fields = args.map(arg => arg._1)

		val _where = new Where("WHERE", where, whereArgs:_*)
		Update.exec(table, fields, args, _where, context)
	}
}

case class UpdateResult(result: Long, sql: String)

object Update {
	def apply(entityName: String, context: Context): Update =
		new Update(entityName, context)

	def exec(
					table: String,
					fields:Seq[String],
					args:Seq[(String, Any)],
					where: Where,
					context: Context): UpdateResult = {

		val (whereSQL, whereArgs) = where.parse(context)
		val sql =
		"UPDATE " + table + " AS _" + table + " SET " +
		fields.mkString("$", ", $", "") + whereSQL

		val allArgs = args.toList ::: whereArgs.toList

		implicit val conn = context.conn
		val result = SQL.executeUpdate(sql, allArgs:_*)
		UpdateResult(result, sql)
	}
}
