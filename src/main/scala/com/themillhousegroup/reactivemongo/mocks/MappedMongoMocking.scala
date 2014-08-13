package com.themillhousegroup.reactivemongo.mocks

import org.specs2.mock.Mockito
import reactivemongo.api.DefaultDB
import play.api.libs.json.JsObject

/**
 * Permits an entire MongoDB database to be mocked as if it
 * were a:
 *
 * Map[String, Set[JsObject] ]
 *
 * Where the String is the collection name,
 * and the Set of JsObjects is the collection contents.
 *
 *
 */
trait MappedMongoMocking extends Mockito {
  this: org.specs2.mutable.Specification =>

  val mockData:Map[String, Set[JsObject]]

  lazy val mockDatabaseName = "mappedMongoMockDB"
  val mockDB = mock[DefaultDB]
  mockDB.name returns mockDatabaseName
  //mockDB.collection()

}
