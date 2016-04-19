package tptfc.sdbc.statement

import tptfc.sdbc.Context

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
