package tptfc.sdbc

import java.sql.Connection

object SQLHelper {

	/**
	 * persist the data in data base
	 *
	 * @param  {String} table             [table name]
	 * @param {Map[String,Any]} fields		[map table fields -> values]
	 */
	def insert(table:String, fields:Map[String,Any])(implicit connection:Connection):Option[Long] = {
		val keySet = fields.keySet.toList
		val query = "insert into " + table + " " + keySet.mkString("(", ",", ")") +
		" values " + keySet.map(value => "{" + value + "}").mkString("(", ",", ")")

		SQL.insert(query, fields.toList:_*)
	}

	/**
	 * delete the table content
	 * @param {String}	table 	[table name]
	 * @param {Map}		where 	[where arguments fields]
	 */
	def delete(table:String, where:Map[String, Any])(implicit connection:Connection):Unit = {
		val keySet = where.keySet.toList
		val sql = "delete from " + table + " where " +
			where.keySet.map(value => value + "={" + value + "}").mkString(" and ")

		SQL.execute(sql, where.toList:_*)
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
		val _where:Map[String, Any] = where.map(t => { ("#__where__" + t._1 -> t._2) })
		val query = "update " + table + " set " +
			fields.keySet.map(value => value + "={" + value + "}").mkString(",") +
			" where "	+ where.keySet.map(value => value + "={#__where__" + value + "}").mkString(" and ")

		val _fields:Map[String,Any] = fields ++ _where

		SQL.update(query, _fields.toList:_*)
	}

	/**
	 * count the number of rows
	 *
	 * @param table:String - select table
	 * @param whereArgs:(String, Any) - where arguments
	 */
	def count(table:String, whereArgs: Seq[(String, Any)]=Nil)(implicit connection:Connection):Option[Long] = {
		val sql = "select count (*) from " + table + (if (!whereArgs.isEmpty) {
			" where " + whereArgs.map(value => value._1 + "= {" + value._1 + "}").mkString("(", " and ", ")")
		} else {
			" "
		})

		try {
			SQL.unique(sql, whereArgs.map(value=> value._1 -> value._2):_*) (r => r.long)
		} catch {
			case e:Exception => None
		}

	}

	/**
	 * get a unique entity
	 *
	 * @param table:String - select table
	 * @param fields:EntityField - entity fields
	 * @param args:(String, Any) - query where fields->params
	 * @type entity:Data
	 */
	def unique[T]
	(
		table:String,
		fields:Seq[String],
		whereArgs: Seq[(String, Any)]=Nil
	)
	(parse:SDBCResult=>T)
	(implicit connection:Connection):Option[T] = {
		val sql = createSelect(table, fields:_*)(whereArgs.toList)
		SQL.unique(sql, whereArgs.map(value=> value._1 -> value._2):_*) (parse)
	}

	/**
	 * get a list of entities
	 *
	 * @param table:String - select table
	 * @param fields:EntityField - entity fields
	 * @param whereArgs:(EntityField, Any) - query where fields->params
	 * @param orderFields:Seq[EntityField] - order fields
	 * @param order:Option[String] - order type: "asc" or "desc"
	 *
	 * @return Seq[Data]
	 */
	def list[T]
	(
		table:String,
		fields:Seq[String],
		whereArgs: Seq[(String, Any)]=Nil,
		orderFields:Seq[String]=Nil,
		orderType:Option[String]=None
	)
	(parse:SDBCResult=>T)
	(implicit connection:Connection):Seq[T] = {
		val sql = createSelect(table, fields:_*)(whereArgs, orderFields, orderType)
		SQL.select(sql, whereArgs.map(value=> value._1 -> value._2):_*) (parse)
	}

	/**
	 * create select query
	 *
	 * @return String query
	 */
	private def createSelect
	(
		table:String,
		fields:String*
	)
	(
		args:Seq[(String,Any)]=Nil,
		orderFields:Seq[String]=Nil,
		order:Option[String]=None
	):String = {

		var sql =
		if (fields.isEmpty) { "select * "
		} else {
			"select " + fields.map(value => value).mkString(",")
		}

		sql = sql + " from " + table

		if (!args.isEmpty) {
			sql = sql + " where " +
				args.map(value => value._1 +
				"= {" + value._1 + "}").mkString("(", " and ", ")")
		}

		if (!orderFields.isEmpty) {
			sql = sql + " order by " +
			orderFields.map(value => value).mkString(",")
			order match {
				case Some(o:String) => sql = sql + " " + o
				case None => sql = sql + " asc "
			}
		}

		sql
	}
}
