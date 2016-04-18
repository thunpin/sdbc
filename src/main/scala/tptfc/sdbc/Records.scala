package tptfc.sdbc

import tptfc.sdbc.error.EntityNotFound

case class Record() {
	private var entries: Map[String, Entry] = Map()

	def entryFrom(entityName: String): Entry = entries get entityName match {
		case Some(entry: Entry) => entry
		case None => throw EntityNotFound(entityName)
	}
}
