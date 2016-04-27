package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context

class Update(entityName: String, uContext: UpdateContext, context: Context) {

	def entity(obj: Any): Update = {
		val record = context.record
		val entry = record entryFrom entityName
		val fields = entry.tableAllFields
		val args = entry.getArgs(fields, obj)
		val whereFields = entry.tableKeys.map(field => field._1).toSeq
		val whereArgs = entry.getArgs(whereFields, obj)

		val where = whereFields.map(f => f + " = $" + f).mkString(" AND ")
		val objWhere = new Where("WHERE", where, whereArgs:_*)

    val uContext = new UpdateContext(fields, args, objWhere)
		new Update(entityName, uContext, context)
	}

	def values(args: (String, Any)*):Update = {
	  val fields = args.map(arg => arg._1)
	  val newUContext = new UpdateContext(fields, args, uContext.where)
	  new Update(entityName, newUContext, context)
	}

	def filter(filter: (String,Any), filters: (String,Any)*): Update = {
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

	def where(where: String, whereArgs: (String,Any)*): Update = {
		val record = context.record
		val entry = record entryFrom entityName
		val objWhere = new Where("WHERE", where, whereArgs:_*)
		val nUContext = new UpdateContext(uContext.fields, uContext.args, objWhere)
	  new Update(entityName, nUContext, context)
	}

	def result: UpdateResult = {
	  val record = context.record
	  val entry = record entryFrom entityName
		val table = entry.tableName
		val fields = uContext.fields
		val args = uContext.args
		val where = uContext.where

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

class UpdateContext(
  val fields:Seq[String],
  val args:Seq[(String, Any)],
	val where: Where)

case class EmptyUpdateContext() extends UpdateContext(Nil, Nil, EmptyWhere())

case class UpdateResult(result: Long, sql: String)

object Update {
	def apply(entityName: String, context: Context): Update =
		new Update(entityName, EmptyUpdateContext(), context)
}
