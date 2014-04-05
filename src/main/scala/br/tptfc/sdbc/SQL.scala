package br.tptfc.sdbc

import java.sql.{PreparedStatement, ResultSet, Statement, Connection}

/**
 * Created by tptfc on 4/5/14.
 *
 */
object SQL {
  /**
   * insert SQL command
   * @param sql sql insert query
   * @param args query args
   * @param c JDBC connection
   * @return insert unique id
   */
  def insert(sql:String, args:(String,Any)*)(implicit c:Connection):Option[Long] = {
    val (query,params) = convert(sql,args:_*)
    val pStmt = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)

    try {
      pStmt.executeUpdate()
      addParams(pStmt, params)

      val rs:ResultSet = pStmt.getGeneratedKeys

      if (rs != null && rs.next()) {
        Some(rs.getLong(1))
      } else {
        None
      }
    } finally {
      pStmt.close()
    }
  }

  /**
   * update SQL command
   *
   * @param sql sql update query
   * @param args query arguments
   * @param c connection
   * @return number of changed rows
   */
  def update(sql:String, args:(String,Any)*)(implicit c:Connection):Int = {
    val (query,params) = convert(sql,args:_*)
    val pStmt = c.prepareStatement(query)
    try {
      addParams(pStmt,params)
      pStmt.executeUpdate()
    } finally {
      pStmt.close()
    }
  }

  /**
   * execute sql
   * @param sql general sql
   * @param args query arguments
   * @param c sql connection
   */
  def execute(sql:String, args:(String,Any)*)(implicit c:Connection):Unit = {
    val (query,params) = convert(sql,args:_*)
    val pStmt = c.prepareStatement(query)

    try {
      addParams(pStmt,params)
      pStmt.executeQuery()
    } finally {
      pStmt.close()
    }
  }

  /**
   * execute query with return
   *
   * @param sql sql
   * @param args sql arguments
   * @param parse convert to object
   * @param c database connection
   * @tparam A object type
   */
  def execute[A](sql:String, args:(String,Any)*)(parse:SDBCResult=>A)(implicit c:Connection):Seq[A] = {
    val (query,params) = convert(sql,args:_*)
    val pStmt = c.prepareStatement(query)

    try {
      addParams(pStmt,params)
      val r = pStmt.executeQuery()
      var result:List[A] = Nil

      while (r.next()) {
        result = result ::: parse(SDBCResult(r)) :: Nil
      }

      r.close()

      result
    } finally {
      pStmt.close()
    }
  }

  /**
   * add arguments in prepare statement
   * @param pStmt prepare statement
   * @param args arguments
   */
  protected def addParams(pStmt:PreparedStatement, args:Seq[Any]) {
    var i = 0
    args.foreach {
      a =>
        pStmt.setObject(i,a)
        i = i + 1
    }
  }

  /**
   * parse SDBC sql and arguments to JDBC sql and arguments
   * @param sql SDBC sql
   * @param args SDBC arguments
   * @return JDBC sql and arguments
   */
  protected def convert(sql:String, args:(String,Any)*):(String,List[Any]) = {
    val query = sql.replaceAll("{[\\w\\d]+}","?")
    if (args.isEmpty) {
      query->Nil
    } else {
      val params = sql.split("}")

      if (params.length > 1) {
        val seq = new Array[Any](params.length - 2)
        val mapArgs = args.map({ t => (t._1, t._2)}).toMap
        var i = 0
        while (i < params.length - 2) {
          seq(i) = mapArgs.get(params(i).split("{")(1).trim).get
          i = i + 1
        }
        query->seq.toList
      } else {
        query->Nil
      }
    }
  }
}
