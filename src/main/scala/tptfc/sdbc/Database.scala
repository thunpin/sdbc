package tptfc.sdbc

trait Database {
  protected def newSqlConnection: java.sql.Connection
  def shutDown():Unit

  protected val record = Record()

  /**
   * transaction scope
   */
  def transaction[T] (action: (Connection) => T ): T = {
    val sqlConnection = newSqlConnection
    sqlConnection.setAutoCommit(false)
    val context = Context(sqlConnection, record)
    val connection = Connection(context)

    try {
      val result = action(connection)
      sqlConnection.commit()
      return result
    } catch {
      case e:Exception =>
        sqlConnection.rollback()
        throw e
    } finally {
      Database close sqlConnection
    }
  }

  def register(entries: Entry*):Unit = entries.foreach(entry => {
    record.register(entry)
  })
}

object Database {
  def close(obj: java.sql.Connection):Unit = {
    try {
      obj.close()
    } catch {
      case e:Exception =>
    }
  }

  def close(resultSet: java.sql.ResultSet):Unit = {
    try {
      resultSet.close()
    } catch {
      case e:Exception =>
    }
  }

  def close(pStmt: java.sql.PreparedStatement):Unit = {
    try {
      pStmt.close()
    } catch {
      case e:Exception =>
    }
  }
}
