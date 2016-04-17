package tptfc.sdbc

import scala.reflect.runtime.universe._

case class Entry(
	tableName: String,
	tableKeys: Map[String, Boolean],
	tableFields: List[String],
	entityFields: List[String],
	tableFieldToEntityField: Map[String, String]) {

	val allFields: List[String] = tableKeys.keys.toList ::: tableFields

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

	private def getTypeFrom[T: TypeTag](obj: T) = typeOf[T]
}
