package com.themillhousegroup.reactivemongo.mocks

import org.specs2.mutable.Specification
import reactivemongo.api.FailoverStrategy
import play.api.libs.json.JsObject
import scala.concurrent.Await
import scala.concurrent.duration.Duration


class MongoMocksDefaultDBSpec extends Specification {

  this
  val shortWait = Duration(100L, "millis")

  "The DefaultDB member of the MongoMocks trait" should {
    val testSpec = new Specification with MongoMocks
    
    "Support accessing a mocked DefaultDB" in {

      testSpec.mockDB must not beNull
    }

    "Support accessing the mocked DefaultDB's default name to help diagnose issues" in {
      testSpec.mockDB.name must beEqualTo(testSpec.mockDatabaseName)
    }
  }

  "The DefaultDB member of the MongoMocks trait, with name overridden" should {


    val testDBName = "new db name"

    val testSpec = new Specification with MongoMocks {
      override lazy val mockDatabaseName = testDBName
    }

    "Support accessing a mocked DefaultDB" in {
      testSpec.mockDB must not beNull
    }

    "Support accessing the mocked DefaultDB's overridden name to help diagnose issues" in {
      testSpec.mockDB.name must beEqualTo(testDBName)
    }
  }

  "The mockedCollection facility" should {

    val testSpec = new Specification with MongoMocks {
      mockedCollection("foo")
    }

    "return null if no named collection matches" in {
      testSpec.mockDB.collection("bar") must beNull
    }

    "return a mock if named collection matches" in {
      testSpec.mockDB.collection("foo", FailoverStrategy()) must not beNull
    }

  }

  "givenMongoCollectionFind" should {
    val testSpec = new Specification with MongoMocks {
      val coll = mockedCollection("foo")
    }

    "mock the find call to return nothing" in {
      testSpec.givenMongoFindReturnsNothing(testSpec.coll)
      val qb = testSpec.coll.find(JsObject(Nil))
      Await.result(qb.one, shortWait) must beNone
    }
  }
}