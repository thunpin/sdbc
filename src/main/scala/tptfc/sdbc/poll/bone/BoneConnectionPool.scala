package tptfc.sdbc.poll.bone

import tptfc.sdbc.poll.ConnectionPool

/**
 * Created by tptfc on 4/5/14.
 *
 */
case class BoneConnectionPool(database:Database) extends ConnectionPool {
  def newConnection = database.connection
}
