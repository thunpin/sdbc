package tptfc.sdbc.statement

import tptfc.sdbc.Context
import scala.util.matching.Regex

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
		val wSql = StatementUtil.parseSQL(where, context)
		val sql = " " + prefix + " " + wSql
		(sql, args)
	}
}

protected class EmptyWhere() extends Where("", "")

protected object StatementUtil {
	def parseSQL(sql: String, context: Context): String = {
		val record = context.record
		val pattern = new Regex(""":([\w]+)\.{0,1}([\w]*)""", "entity", "field")

		pattern replaceAllIn (sql, m => {
			val entry = record entryFrom m.group("entity")

			if (m.group("field").isEmpty)
			  entry.tableName + " AS _" + entry.tableName
			else
			  "_" + entry.tableName + "." + (entry entityFieldFrom m.group("field"))
		})
	}
}
