package tptfc.sdbc

import java.sql._
import scala.Some
import scala.Array
import scala.util.matching.Regex

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
  def insert(sql:String, args:(String,Any)*)
  (implicit c:java.sql.Connection):Option[Long] = {
    val (query,params) = convert(sql,args:_*)
    val pStmt = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)

    try {
      addParams(pStmt, params)
      pStmt.executeUpdate()

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
  def executeUpdate(sql:String, args:(String,Any)*)
  (implicit c:java.sql.Connection):Long = {
    val (query,params) = convert(sql,args:_*)
    val pStmt = c.prepareStatement(query)
    try {
      addParams(pStmt,params)
      pStmt.executeUpdate()
    } finally {
      Database close pStmt
    }
  }

  /**
   * execute sql
   * @param sql general sql
   * @param args query arguments
   * @param c sql connection
   */
  def execute(sql:String, args:(String,Any)*)
  (implicit c:java.sql.Connection):Unit = {
    val (query,params) = convert(sql,args:_*)
    val pStmt = c.prepareStatement(query)

    try {
      addParams(pStmt,params)
      pStmt.execute()
    } finally {
      Database close pStmt
    }
  }

  /**
   * execute select query
   *
   * @param sql sql
   * @param args sql arguments
   * @param parse convert to object
   * @param c database connection
   * @tparam A object type
   */
  def select[A](sql:String, args:(String,Any)*)(parse:Result=>A)
  (implicit c:java.sql.Connection):Seq[A] = {
    val (query,params) = convert(sql,args:_*)
    val pStmt = c.prepareStatement(query)

    try {
      addParams(pStmt,params)
      val r = pStmt.executeQuery()
      var result:List[A] = Nil

      while (r.next()) {
        result = result ::: parse(Result(r)) :: Nil
      }

      Database close r

      result
    } finally {
      Database close pStmt
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
  def unique[A](sql:String, args:(String,Any)*)(parse:Result=>A)
  (implicit c:java.sql.Connection):Option[A] = {
    val (query,params) = convert(sql,args:_*)
    val pStmt = c.prepareStatement(query)

    try {
      addParams(pStmt,params)
      val r = pStmt.executeQuery()

      val result:Option[A] =
      if (r.next()) {
        Some(parse(Result(r)))
      } else {
        None
      }

      Database close r

      result
    } finally {
      Database close pStmt
    }
  }

  /**
   * add arguments in prepare statement
   * @param pStmt prepare statement
   * @param args arguments
   */
  protected def addParams(pStmt:PreparedStatement, args:Seq[Any])
  (implicit connection:java.sql.Connection) {

    var i = 0
    args.foreach {
      arg =>
        i = i + 1

        arg match {
          case seq:Seq[_] =>
            i = i - 1
            seq.foreach(value => {
              i = i + 1
              pStmt.setObject(i, value)
            })
          case Some(value:Any) => pStmt.setObject(i,value)
          case None => pStmt.setObject(i,null)
          case _ => pStmt.setObject(i,arg)
        }
    }
  }

  /**
   * parse SDBC sql and arguments to JDBC sql and arguments
   * @param sql SDBC sql
   * @param args SDBC arguments
   * @return JDBC sql and arguments
   */
  protected def convert(sql:String, args:(String,Any)*):(String,List[Any]) = {
    if (args.isEmpty) {
      sql->Nil
    } else {
      var seq:List[Any] = Nil
      val mapArgs = args.map({ t => (t._1, t._2)}).toMap
      var query = sql

      val pattern = new Regex("""\$([\w]+)""", "key")
			query = pattern replaceAllIn (sql, m => {
				val key = m.group("key")
				val param = mapArgs.get(key).get
				seq = seq ::: (param :: Nil)

        param match {
          case (seq:Seq[_]) => seq.map(f => "?").mkString("(", ",", ")")
          case _ => "?"
        }
			})
    }
  }
}
