package com.themillhousegroup.reactivemongo.mocks

import org.specs2.mutable.Specification
import play.api.libs.json.{JsNumber, JsString, JsObject}
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Await
import com.themillhousegroup.reactivemongo.test.CommonMongoTests
import org.specs2.specification.Scope

// Get implicit conversions into scope
import play.modules.reactivemongo.json.collection._


class MappedMongoCollectionRetrievalSpec extends Specification  with CommonMongoTests {

  case class MSpec(val mockData:Map[String, Set[JsObject]]) extends Specification with MappedMongoMocking

  "The mockedCollection facility" should {

    class NamedCollectionScope(collectionNames:String*) extends Scope {
      val testSpec = MSpec(collectionNames.map ( _ -> Set[JsObject]()).toMap)
    }

    "return null if no named collection matches" in new NamedCollectionScope("foo") {
      testSpec.mockDB.collection("bar") must beNull
    }

    "return a mock if named collection matches" in new NamedCollectionScope("foo") {
      testSpec.mockDB.collection[JSONCollection]("foo") must not beNull
    }

    "return mocks if named collections match" in new NamedCollectionScope("foo", "bar") {
      testSpec.mockDB.collection[JSONCollection]("foo") must not beNull

      testSpec.mockDB.collection[JSONCollection]("bar") must not beNull

      testSpec.mockDB.collection[JSONCollection]("baz") must  beNull
    }

  }

  "find-in-collection" should {

    class NameValueCollectionScope(name:String, values:JsObject*) extends Scope {
      val testSpec = MSpec(Map(name -> values.toSet))
    }

    val testObject = JsObject(Seq("bar" -> JsNumber(1)))
    val otherObject = JsObject(Seq("baz" -> JsNumber(2)))

    "be able to mock the find call to return an exact match - positive case" in new NameValueCollectionScope(
      "foo", testObject) {

      val qb = testSpec.mockDB.collection("foo").find(testObject)
      Await.result(qb.one[JsObject], shortWait) must beSome(testObject)
    }

    "be able to mock the find call to return an exact match - negative case" in new NameValueCollectionScope(
      "foo", testObject) {

      val qb = testSpec.mockDB.collection("foo").find(otherObject)
      Await.result(qb.one[JsObject], shortWait) must beNone
    }

//    "be able to mock the find call to return something" in new MockedCollectionScope {
//      val searchedThing = JsString("thing")
//
//      testSpec.givenMongoFindReturnsSome(testSpec.coll, searchedThing)
//      val qb = testSpec.coll.find(JsObject(Nil))
//      Await.result(qb.one[JsObject], shortWait) must beSome(searchedThing)
//    }
//
//    "be able to mock the find call to return an Option" in new MockedCollectionScope {
//      val searchedOption = Some(JsString("thing"))
//
//      testSpec.givenMongoFindReturns(testSpec.coll, searchedOption)
//      val qb = testSpec.coll.find(JsObject(Nil))
//      Await.result(qb.one[JsObject], shortWait) must beEqualTo(searchedOption)
//    }
  }
}
