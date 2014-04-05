package br.tptfc.sdbc

import java.sql.ResultSet

/**
 * Created by tptfc on 4/5/14.
 *
 */
case class SDBCResult(result:ResultSet) {
  private var pos:Int = 1

  def blob = result.getBlob(pos)
  def pBlob = {
    val result = blob
    pos = pos + 1
    result
  }

  def boolean = result.getBoolean(pos)
  def pBoolean = {
    val result = boolean
    pos = pos + 1
    result
  }

  def byte = result.getByte(pos)
  def pByte = {
    val result = byte
    pos = pos + 1
    result
  }

  def date = result.getDate(pos)
  def pDate = {
    val result = date
    pos = pos + 1
    result
  }

  def double = result.getDouble(pos)
  def pDouble = {
    val result = double
    pos = pos + 1
    result
  }

  def float = result.getFloat(pos)
  def pFloat = {
    val result = float
    pos = pos + 1
    result
  }

  def int = result.getInt(pos)
  def pInt = {
    val result = int
    pos = pos + 1
    result
  }

  def long = result.getLong(pos)
  def pLong = {
    val result = long
    pos = pos + 1
    result
  }

  def string = result.getString(pos)
  def pString = {
    val result = string
    pos = pos + 1
    result
  }


  def optBlob = Some(result.getBlob(pos))
  def pOptBlob = {
    val result = optBlob
    pos = pos + 1
    result
  }

  def optBoolean = Some(result.getBoolean(pos))
  def pOptBoolean = {
    val result = optBoolean
    pos = pos + 1
    result
  }

  def optByte = Some(result.getByte(pos))
  def pOptByte = {
    val result = optByte
    pos = pos + 1
    result
  }

  def optDate = Some(result.getDate(pos))
  def pOptDate = {
    val result = optDate
    pos = pos + 1
    result
  }

  def optDouble = Some(result.getDouble(pos))
  def pOptDouble = {
    val result = optDouble
    pos = pos + 1
    result
  }

  def optFloat = Some(result.getFloat(pos))
  def pOptFloat = {
    val result = optFloat
    pos = pos + 1
    result
  }

  def optInt = Some(result.getInt(pos))
  def pOptInt = {
    val result = optInt
    pos = pos + 1
    result
  }

  def optLong = Some(result.getLong(pos))
  def pOptLong = {
    val result = optLong
    pos = pos + 1
    result
  }

  def optString = Some(result.getString(pos))
  def pOptString = {
    val result = optString
    pos = pos + 1
    result
  }
}
