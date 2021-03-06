package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context
import tptfc.sdbc.Result

class Select(
entityName: String,
columns: Seq[String],
where: Where,
joins: Seq[Join],
orderBy: Option[String],
context: Context) {
	def all[T](parse:Result=>T): SelectAllResult[T] = {
		val (sql, args) = buildSql
		implicit val conn = context.conn
		val result = SQL.select(sql, args:_*)(parse)
		SelectAllResult(result, sql)
	}

	def unique[T](parse:Result=>T): SelectUniqueResult[T] = {
		val (sql, args) = buildSql
		implicit val conn = context.conn
		val result = SQL.unique(sql, args:_*)(parse)
		SelectUniqueResult(result, sql)
	}

	def columns(column: String, columns: String*): Select = {
		val columnsList = column :: columns.toList
		val nColumns = columnsList.map(c => StatementUtil.parseSQL(c, context))
		new Select(entityName, nColumns, where, joins, orderBy, context)
	}

	def join(
	entityName: String,
	as: String,
	on:String,
	onArgs:(String,Any)*): Select = {
		val joinWhere = new Where("ON", on, onArgs:_*)
		val newJoin = new Join(entityName, as, joinWhere)
		val newJoins = joins.toList ::: (newJoin :: Nil)
		new Select(this.entityName, columns, where, newJoins, orderBy, context)
	}

	def join(entityName: String, on: String, onArgs: (String,Any)*): Select = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val as = "_" + table

		join(entityName, as, on, onArgs:_*)
	}

	def filter(filter: (String,Any), filters: (String,Any)*): Select = {
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

	def where(where:String, args: (String,Any)*): Select = {
		val newWhere = new Where("WHERE", where, args:_*)
		new Select(entityName, columns, newWhere, joins, orderBy, context)
	}

	def orderBy(order:String): Select = {
		val newOrderBy = " ORDER BY " + StatementUtil.parseSQL(order, context)
		new Select(entityName, columns, where, joins, Some(newOrderBy), context)
	}

	protected def buildSql:(String, Seq[(String, Any)]) = {
		val (whereSQL, whereArgs) = where.parse(context)
		val (joinsSQL, joinsArgs) = buildJoins
		val args = joinsArgs.toList ::: whereArgs.toList

		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName

		val sql =
			"SELECT " + columns.mkString(",") +
			" FROM " + table +
			" AS " + "_" + table + joinsSQL + whereSQL + orderBy.getOrElse("")

		(sql, args)
	}

	protected def buildJoins: (String, Seq[(String, Any)])  = {
		var sqlJoins:String = ""
		var args:List[(String, Any)] = Nil

		joins.foreach(join => {
			val (str, _args) = join.parse(context)
			args = args ::: _args.toList
			sqlJoins = sqlJoins + str
		})

		(sqlJoins, args)
	}
}

case class SelectAllResult[T](result:Seq[T], sql: String)
case class SelectUniqueResult[T](result:Option[T], sql: String)

object Select {
	def apply(entityName: String, context: Context): Select = {
		val record = context.record
		val entry = record entryFrom entityName
		val tableName = "_" + entry.tableName
		val columns = entry.tableAllFields.map(field => tableName + "." + field)

		new Select(entityName, columns, new EmptyWhere(), Nil, None, context)
	}
}
