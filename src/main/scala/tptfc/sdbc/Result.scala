package tptfc.sdbc

import java.sql.ResultSet

/**
 * Created by tptfc on 4/5/14.
 *
 */
case class Result(result:ResultSet) {
  private var pos:Int = 1

  def any = toDefault{result.getObject(pos)}
  def blob = toDefault{result.getBlob(pos)}
  def boolean = toDefault{result.getBoolean(pos)}
  def byte = toDefault{result.getByte(pos)}
  def date = toDefault{result.getDate(pos)}
  def double = toDefault{result.getDouble(pos)}
  def float = toDefault{result.getFloat(pos)}
  def int = toDefault{result.getInt(pos)}
  def long = toDefault{result.getLong(pos)}
  def string = toDefault{result.getString(pos)}


  def optAny = toOption{result.getObject(pos)}
  def optBlob = toOption{result.getByte(pos)}
  def optBoolean = toOption{result.getBoolean(pos)}
  def optByte = toOption{result.getByte(pos)}
  def optDate = toOption{result.getDate(pos)}
  def optDouble = toOption{result.getDouble(pos)}
  def optFloat = toOption{result.getFloat(pos)}
  def optInt = toOption{result.getInt(pos)}
  def optLong = toOption{result.getLong(pos)}
  def optString = toOption{result.getString(pos)}


  private def toDefault[A](exec: => A):A = {
    val result = exec
    pos = pos + 1
    result
  }

  private def toOption[A](exec: => A):Option[A] = {
    val result =
    try {
      Some(exec)
    } catch {
      case e:Exception => None
    }

    pos = pos + 1
    result
  }
}
