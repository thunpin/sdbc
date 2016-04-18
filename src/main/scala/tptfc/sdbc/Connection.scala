package tptfc.sdbc

import tptfc.sdbc.statement._

case class Connection(context: Context) {
	def insert(entity: String): Insert = Insert(entity, context)
	def update(entity: String): Update = Update(entity, context)
	def delete(entity: String): Delete = Delete(entity, context)
	def select(entity: String): Select = Select(entity, context)
}
