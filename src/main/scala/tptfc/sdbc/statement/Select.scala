package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context
import tptfc.sdbc.Result
import scala.util.matching.Regex

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
		val nColumns = columnsList.map(c => SelectUtil.parseSQL(c, context))
		new Select(entityName, nColumns, where, joins, orderBy, context)
	}

	def join(
	entityName: String,
	as: String,
	on:String,
	onArgs:(String,Any)*): Select = {
		val where = new Where("ON", on, onArgs:_*)
		val newJoin = new Join(entityName, as, where)
		val newJoins = joins.toList ::: (newJoin :: Nil)
		new Select(entityName, columns, where, newJoins, orderBy, context)
	}

	def join(entityName: String, on: String, onArgs: (String,Any)*): Select = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName
		val as = "_" + table

		join(entityName, on, as, onArgs:_*)
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
		val newOrderBy = " ORDER BY " + SelectUtil.parseSQL(order, context)
		new Select(entityName, columns, where, joins, Some(newOrderBy), context)
	}

	protected def buildSql:(String, Seq[(String, Any)]) = {
		val (whereSQL, whereArgs) = where.parse(context)
		val (joinsSQL, joinsArgs) = buildJoins
		val args = joinsArgs.toList ::: whereArgs.toList

		val sql =
			"SELECT " + columns.mkString(",") +
			" FROM " + entityName +
			" AS " + "_" + entityName + joinsSQL + whereSQL + orderBy.getOrElse("")

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

protected class Join(entityName: String, as: String, on: Where) {
	def parse(context: Context): (String, Seq[(String, Any)]) = {
		val record = context.record
		val entry = record entryFrom entityName
		val table = entry.tableName

		val (onSql, args) = on.parse(context)
		val sql = " JOIN " + table + " AS " + as + onSql
		(sql, args)
	}
}

protected class Where(prefix: String, where: String, args: (String, Any)*) {
	def parse(context: Context): (String, Seq[(String, Any)]) = {
		val record = context.record
		val wSql = SelectUtil.parseSQL(where, context)
		val sql = " " + prefix + " " + wSql
		(sql, args)
	}
}

protected class EmptyWhere() extends Where("", "")

object Select {
	def apply(entityName: String, context: Context): Select = {
		val record = context.record
		val entry = record entryFrom entityName
		val tableName = "_" + entry.tableName
		val columns = entry.tableAllFields.map(field => tableName + "." + field)

		new Select(entityName, columns, new EmptyWhere(), Nil, None, context)
	}
}

protected object SelectUtil {
	def parseSQL(sql: String, context: Context): String = {
		val record = context.record
		val pattern = new Regex(""":([\w]+)\.([\w]+)""", "entity", "field")

		pattern replaceAllIn (sql, m => {
			val entry = record entryFrom m.group("entity")
			"_" + entry.tableName + "." + (entry entityFieldFrom m.group("field"))
		})
	}
}
