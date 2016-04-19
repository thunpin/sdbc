package tptfc.sdbc

import tptfc.sdbc.error.FieldNotFound

class Entry(
	val tableName: String,
	val tableKeys: Map[String, Boolean],
	val tableFields: List[String],
	val tableAllFields: List[String],
	val entityFields: List[String],
	val entityFieldToTableField: Map[String, String],
	val tableFieldToEntityField: Map[String, String]) {

	def getArgs[A](tableFields: Seq[String], entity: A): Seq[(String, Any)] = {
		var args: List[(String, Any)] = Nil

		tableFields.foreach(name => {
			val entityField = tableFieldToEntityField.getOrElse(name, name)
			println(entityField)
			val field = entity.getClass().getDeclaredField(entityField)
			field.setAccessible(true)
			val value = field.get(entity)
			args = (name -> value) :: args
		})

		args
	}

	def entityFieldFrom(tableField: String): String = {
		entityFieldToTableField get tableField match {
			case Some(name:String) => name
			case None => throw new FieldNotFound(tableField)
		}
	}
}

object Entry {
	def apply(
	tableName: String,
	tableKeys: Map[String, Boolean],
	tableFields: List[String],
	tableAllFields: List[String],
	entityFields: List[String],
	entityFieldToTableField: Map[String, String],
	tableFieldToEntityField: Map[String, String]) =
		new Entry(
			tableName,
			tableKeys,
			tableFields,
			tableAllFields,
			entityFields,
			entityFieldToTableField,
			tableFieldToEntityField)

	def build(
	entity: String,
	table: String,
	field: String,
	fields: String*):Entry = {
		val _fields = field :: fields.toList
		var tableKeys:Map[String, Boolean] = Map()
		var tableFields:List[String] = Nil
		var tableAllFields:List[String] = Nil
		var entityFields:List[String] = Nil
		var entityFieldToTableField:Map[String, String] = Map()
		var tableFieldToEntityField:Map[String, String] = Map()

		_fields.foreach(field => {
			val keysSimbol:List[String] = "$" :: "#" :: Nil
			val isAutoKey = field.contains(keysSimbol(0))
			val isKey = field.contains(keysSimbol(1))
			val _field = field.replace("$","").replace("#","")

			val splited = _field.split("::")
			val entityField = splited(0)
			val tableField = if (splited.length > 1) splited(1) else splited(0)

			if (isKey || isAutoKey) {
				tableKeys += (tableField -> isAutoKey)
			} else {
				tableFields = tableFields ::: (tableField :: Nil)
			}

			tableAllFields = tableAllFields ::: (tableField :: Nil)
			entityFields = entityFields ::: (entityField :: Nil)
			entityFieldToTableField += (entityField -> tableField)
			tableFieldToEntityField += (tableField -> entityField)
		})

		Entry(
			table,
			tableKeys,
			tableFields,
			tableAllFields,
			entityFields,
			entityFieldToTableField,
			tableFieldToEntityField)
	}
}
