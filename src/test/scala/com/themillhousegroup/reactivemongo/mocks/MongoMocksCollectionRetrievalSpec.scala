package com.themillhousegroup.reactivemongo.mocks

import org.specs2.mutable.Specification
import play.api.libs.json.{JsString, JsObject}
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Await
import com.themillhousegroup.reactivemongo.test.CommonMongoTests
import org.specs2.specification.Scope

class MongoMocksCollectionRetrievalSpec extends Specification  with CommonMongoTests {

  private class MockedCollectionScope extends Scope {
    val testSpec = new Specification with MongoMocks {
      val coll = mockedCollection("foo")
    }
  }

  "The mockedCollection facility" should {

    "return null if no named collection matches" in new MockedCollectionScope  {
      testSpec.mockDB.collection("bar") must beNull
    }

    "return a mock if named collection matches" in new MockedCollectionScope {
      testSpec.mockDB.collection[JSONCollection]("foo") must not beNull
    }

  }

  "givenMongoCollectionFindReturns" should {

    "be able to mock the find call to return nothing" in new MockedCollectionScope {
      testSpec.givenMongoFindReturnsNothing(testSpec.coll)
      val qb = testSpec.coll.find(JsObject(Nil))
      Await.result(qb.one[JsObject], shortWait) must beNone
    }

    "be able to mock the find call to return something" in new MockedCollectionScope {
      val searchedThing = JsString("thing")

      testSpec.givenMongoFindReturnsSome(testSpec.coll, searchedThing)
      val qb = testSpec.coll.find(JsObject(Nil))
      Await.result(qb.one[JsObject], shortWait) must beSome(searchedThing)
    }

    "be able to mock the find call to return an Option" in new MockedCollectionScope {
      val searchedOption = Some(JsString("thing"))

      testSpec.givenMongoFindReturns(testSpec.coll, searchedOption)
      val qb = testSpec.coll.find(JsObject(Nil))
      Await.result(qb.one[JsObject], shortWait) must beEqualTo(searchedOption)
    }
  }
}
