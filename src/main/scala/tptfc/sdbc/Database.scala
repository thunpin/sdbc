package tptfc.sdbc

trait Database {
	protected def newSqlConnection: java.sql.Connection
	protected val record = Record()

	/**
	 * transaction scope
	 */
	def transaction[T] (action: (Connection) => T ): T = {
		val sqlConnection = newSqlConnection
		sqlConnection.setAutoCommit(false)
		val context = Context(sqlConnection, record, Nil)
		val connection = Connection(context)

		try {
			val result = action(connection)
			sqlConnection.commit()
			return result
		} catch {
			case e:Exception =>
				sqlConnection.rollback()
				throw e
		}
	}
}
