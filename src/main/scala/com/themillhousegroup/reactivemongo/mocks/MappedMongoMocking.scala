package com.themillhousegroup.reactivemongo.mocks

import org.specs2.mock.Mockito
import reactivemongo.api.DefaultDB
import play.api.libs.json.JsObject
import reactivemongo.play.json.collection.JSONCollection

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
trait MappedMongoMocking extends MongoMocks {
  this: org.specs2.mutable.Specification =>

  val mockData: Map[String, Set[JsObject]]

  override lazy val mockDatabaseName = "mappedMongoMockDB"

  val collectionsByName: Map[String, JSONCollection] = mockData.map {
    case (k, v) =>
      k -> mockedCollection(k)(this.mockDB)
  }.toMap

  mockData.foreach {
    case (collName, contents) =>
      val coll = collectionsByName(collName)

      // Set up the default behaviour if we don't match: return a None
      givenMongoFindAnyReturnsNone(coll)

      // Now override with specific exact-match cases:
      contents.foreach(givenMongoFindExactReturnsItself(coll, _))
  }

}
