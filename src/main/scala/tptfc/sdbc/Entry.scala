package tptfc.sdbc

import tptfc.sdbc.error.FieldNotFound
import scala.reflect.runtime.universe._

class Entry(
	val tableName: String,
	val tableKeys: Map[String, Boolean],
	val tableFields: List[String],
	val tableAllFields: List[String],
	val entityFields: List[String],
	val tableFieldToEntityField: Map[String, String]) {

	def getArgs(tableFields: Seq[String], entity: Any): Seq[(String, Any)] = {
		var args: List[(String, Any)] = Nil
		val entityType = getTypeFrom(entity)
		val mirrorEntity = runtimeMirror(getClass.getClassLoader)
		val reflectEntity = mirrorEntity reflect entity

		tableFields.foreach(name => {
			val entityField = tableFieldToEntityField.getOrElse(name, name)
			val term = entityType.decl(TermName(name)).asTerm
			val value = reflectEntity.reflectField(term).get
			args = (name -> value) :: args
		})

		args
	}

	def entityFieldFrom(tableField: String): String = {
		tableFieldToEntityField get tableField match {
			case Some(name:String) => name
			case None => throw new FieldNotFound(tableField)
		}
	}

	private def getTypeFrom[T: TypeTag](obj: T) = typeOf[T]
}

object Entry {
	def apply(
	tableName: String,
	tableKeys: Map[String, Boolean],
	tableFields: List[String],
	tableAllFields: List[String],
	entityFields: List[String],
	tableFieldToEntityField: Map[String, String]) =
		new Entry(
			tableName,
			tableKeys,
			tableFields,
			tableAllFields,
			entityFields,
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
		var tableFieldToEntityField:Map[String, String] = Map()

		_fields.foreach(field => {
			val keysSimbol:List[String] = "$" :: "#" :: Nil
			val isAutoKey = field.contains(keysSimbol(0))
			val isKey = field.contains(keysSimbol(1))
			val _field = field.filterNot(keysSimbol.toSet)

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
			tableFieldToEntityField += (tableField -> entityField)
		})

		Entry(
			table,
			tableKeys,
			tableFields,
			tableAllFields,
			entityFields,
			tableFieldToEntityField)
	}
}
