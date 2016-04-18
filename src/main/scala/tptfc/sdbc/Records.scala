package tptfc.sdbc

import tptfc.sdbc.error.EntityNotFound

case class Record() {
	private var entries: Map[String, Entry] = Map()

	def register(reg: (String, Entry)):Unit = entries += reg

	def entryFrom(entityName: String): Entry = entries get entityName match {
		case Some(entry: Entry) => entry
		case None => throw EntityNotFound(entityName)
	}
}
