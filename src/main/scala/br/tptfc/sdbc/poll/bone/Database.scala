package br.tptfc.sdbc.poll.bone

import com.jolbox.bonecp.{BoneCP, BoneCPConfig}
import java.sql.Connection
import br.tptfc.sdbc.poll.ConnectionPool

/**
 * connection pool implemented with boneCP
 *
 * @param driver data base driver
 * @param url connection url
 * @param user user login
 * @param password user password
 * @param minPoolSize min pool size [default: 1]
 * @param maxPoolSize max pool size [default: 20]
 * @param acquireIncrement acquire increment [default: 5]
 * @param partitionCount partition count [default: 3]
 */
class Database(driver:String,
               url:String,
               user:String,
               password:String,
               minPoolSize:Int=1,
               maxPoolSize:Int=20,
               acquireIncrement:Int=5,
               partitionCount:Int=3) {
  
  protected lazy val boneCP = {
    Class.forName(driver)
    val config = new BoneCPConfig()
    config.setJdbcUrl(url)
    config.setUsername(user)
    config.setPassword(password)
    config.setMinConnectionsPerPartition(minPoolSize)
    config.setMaxConnectionsPerPartition(maxPoolSize)
    config.setAcquireIncrement(acquireIncrement)
    config.setPartitionCount(partitionCount)
    config.setIdleConnectionTestPeriodInMinutes(10)
    config.setConnectionTestStatement("/* ping */ SELECT 1")
    new BoneCP(config)
  }

  def shutDown():Unit =  {
    boneCP.close()
    boneCP.shutdown()
  }

  def pool:ConnectionPool = BoneConnectionPool(this)

  private[bone] def connection:Connection = boneCP.getConnection
}