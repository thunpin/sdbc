package br.tptfc.sdbc

/**
 * Created by tptfc on 4/5/14.
 *
 */
object Parse {
  implicit def resultToBlob(result:SDBCResult) = result.blob
  implicit def resultToBoolean(result:SDBCResult) = result.boolean
  implicit def resultToByte(result:SDBCResult) = result.byte
  implicit def resultToDate(result:SDBCResult) = result.date
  implicit def resultToDouble(result:SDBCResult) = result.double
  implicit def resultToFloat(result:SDBCResult) = result.float
  implicit def resultToInt(result:SDBCResult) = result.int
  implicit def resultToLong(result:SDBCResult) = result.long
  implicit def resultToString(result:SDBCResult) = result.string

  implicit def resultToOptBlob(result:SDBCResult) = result.optBlob
  implicit def resultToOptBoolean(result:SDBCResult) = result.optBoolean
  implicit def resultToOptByte(result:SDBCResult) = result.optByte
  implicit def resultToOptDate(result:SDBCResult) = result.optDate
  implicit def resultToOptDouble(result:SDBCResult) = result.optDouble
  implicit def resultToOptFloat(result:SDBCResult) = result.optFloat
  implicit def resultToOptInt(result:SDBCResult) = result.optInt
  implicit def resultToOptLong(result:SDBCResult) = result.optLong
  implicit def resultToOptString(result:SDBCResult) = result.optString
}
