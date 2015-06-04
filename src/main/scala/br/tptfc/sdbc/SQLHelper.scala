package br.tptfc.sdbc

import java.sql.Connection

object SQLHelper {
	/**
	 * persist the data in data base
	 * 
	 * @param  {String} table             [table name]
	 * @param {Map[String,Any]} fields		[map table fields -> values]
	 */
	def insert(table:String, fields:Map[String,Any])(implicit connection:Connection):Option[Long] = {
		val query = "insert into " + table + " " + fields.keySet.mkString("(", ",", ")") +
		" values " + fields.keySet.map(value => "{" + value + "}").mkString("(", ",", ")")

		SQL.insert(query, fields.toList:_*)
	}

	/**
	 * update the data
	 * 
	 * @param  {String} table             [table name]
	 * @param  {Map[String,Any]} fields		[fields table with your new value]
	 * @param  {Map[String,Any]} where		[where args]
	 */
	def update(table:String, fields:Map[String,Any], where:Map[String, Any])
	(implicit connection:Connection) {
		val query = "update " + table + " set " + 
			fields.keySet.map(value => value + "={" + value + "}").mkString(",") +
			" where "	+ where.keySet.map(value => value + "={" + value + "}").mkString(" and ")

		SQL.update(query, fields.toList:_*)
	}
}