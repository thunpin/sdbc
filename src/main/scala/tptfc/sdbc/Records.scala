package tptfc.sdbc

case class Record() {
	private var entries: Map[String, Entry] = Map()

	def entryFrom(entityName: String): Option[Entry] = entries get entityName
}
