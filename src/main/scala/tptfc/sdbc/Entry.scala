package tptfc.sdbc

import tptfc.sdbc.error.FieldNotFound
import scala.reflect.runtime.universe._

case class Entry(
	tableName: String,
	tableKeys: Map[String, Boolean],
	tableFields: List[String],
	tableAllFields: List[String],
	entityFields: List[String],
	tableFieldToEntityField: Map[String, String]) {

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
