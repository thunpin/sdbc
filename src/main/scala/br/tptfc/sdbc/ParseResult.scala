package br.tptfc.sdbc

/**
 * Created by tptfc on 4/5/14.
 *
 */
object ParseResult {
  implicit def resultToBlob(result:SDBCResult) = result.pBlob
  implicit def resultToBoolean(result:SDBCResult) = result.pBoolean
  implicit def resultToByte(result:SDBCResult) = result.pByte
  implicit def resultToDate(result:SDBCResult) = result.pDate
  implicit def resultToDouble(result:SDBCResult) = result.pDouble
  implicit def resultToFloat(result:SDBCResult) = result.pFloat
  implicit def resultToInt(result:SDBCResult) = result.pInt
  implicit def resultToLong(result:SDBCResult) = result.pLong
  implicit def resultToString(result:SDBCResult) = result.pString

  implicit def resultToOptBlob(result:SDBCResult) = result.pOptBlob
  implicit def resultToOptBoolean(result:SDBCResult) = result.pOptBoolean
  implicit def resultToOptByte(result:SDBCResult) = result.pOptByte
  implicit def resultToOptDate(result:SDBCResult) = result.pOptDate
  implicit def resultToOptDouble(result:SDBCResult) = result.pOptDouble
  implicit def resultToOptFloat(result:SDBCResult) = result.pOptFloat
  implicit def resultToOptInt(result:SDBCResult) = result.pOptInt
  implicit def resultToOptLong(result:SDBCResult) = result.pOptLong
  implicit def resultToOptString(result:SDBCResult) = result.pOptString
}
