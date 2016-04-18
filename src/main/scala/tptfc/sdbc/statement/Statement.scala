package tptfc.sdbc.statement

import tptfc.sdbc.SQL
import tptfc.sdbc.Context
import tptfc.sdbc.Entry
import tptfc.sdbc.error.EntityNotFound

abstract class Statement(entityName: String, context: Context) {
	validate()
	private def validate(): Unit = {
		val record = context.record
		record entryFrom entityName match {
			case None =>
				throw EntityNotFound()
			case _ =>
		}
	}
}
