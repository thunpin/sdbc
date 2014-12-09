package br.tptfc.sdbc.poll

import java.sql.Connection
import java.util.Date

/**
 * Created by tptfc on 1/2/14.
 *
 * give defaults sql connection methods like new connection, rollback, etc.
 *
 */
trait ConnectionPool {
	protected def newConnection:Connection

	/**
	 * map used to store the opened connections
	 */
	protected var openedConnections:Map[String,Connection] = Map()
	protected var openedConnectionsInverse:Map[Connection,String] = Map()

	/**
	 * define a transaction scope
	 * commit and rollback will executed automaticaly
	 *
	 * @param action  command to be executed in transaction scope
	 */
	def transaction[T](action: (Connection) => T):T = {
		var connection:Option[Connection] = None
		var result:Option[T] = None

		try {
			connection = Some(newConnection)
			result = runIfThereIsConnection(connection, action)
			runIfThereIsConnection(connection, commit)
		} catch {
			case e:Exception =>
				runIfThereIsConnection(connection, rollback)
				throw e
		} finally {
			runIfThereIsConnection(connection, closeConnection)
		}

		result.get
	}

	/**
	 * a valid sql connection
	 * @return a valid sql connection
	 */
	def connection(implicit name:String = new Date().toString):Connection = synchronized {
		if (openedConnections.contains(name)) {
			openedConnections.get(name).get
		} else {
			val connection = newConnection
			connection.setAutoCommit(false)
			openedConnections += name -> connection
			openedConnectionsInverse += connection -> name
			connection
		}
	}

	/**
	 * execute a db rollback
	 * @param connection sql connection
	 */
	def rollback(connection:Connection):Unit =  {
		if (!connection.isClosed) connection.rollback()
	}

	/**
	 * commit the connection db transaction
	 * @param connection sql connection
	 */
	def commit(connection:Connection):Unit =  {
		if (!connection.isClosed) connection.commit()
	}

	/**
	 * - close the connection
	 * - remove the connection from opened connections map
	 *
	 * @param connection to close
	 */
	def closeConnection(connection:Connection):Unit =  {
		if (!connection.isClosed) {
			connection.close()
		}

		val key = openedConnectionsInverse.get(connection).get
		openedConnectionsInverse = openedConnectionsInverse - connection
		openedConnections = openedConnections - key
	}

	/**
	 * rollback all connection
	 */
	def rollbackAll()(implicit closeConnections:Boolean = true):Unit =  {
		openedConnections.values.foreach(connection => rollback(connection))
		if (closeConnections) closeAllConnection()
	}

	/**
	 * commit all opened connections
	 */
	def commitAll()(implicit closeConnections:Boolean = true):Unit =  {
		openedConnections.values.foreach(connection => commit(connection))
		if (closeConnections) closeAllConnection()
	}

	/**
	 * close all opened connections
	 */
	def closeAllConnection():Unit =  { synchronized {
		openedConnections.values.foreach(connection => closeConnection(connection))
	}}

	/**
	 * execute action if there is connection in the option
	 * @type {[type]}
	 */
	private def runIfThereIsConnection[T](conn:Option[Connection], action:Connection => T):Option[T] = conn match {
		case Some(connection:Connection) => Option(action(connection))
		case _ => None
	}
}
