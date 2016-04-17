package tptfc.sdbc

case class Connection(context: Context) {
	def insert(entity: String): Boolean = false
	def update(entity: String): Int = 0
}
