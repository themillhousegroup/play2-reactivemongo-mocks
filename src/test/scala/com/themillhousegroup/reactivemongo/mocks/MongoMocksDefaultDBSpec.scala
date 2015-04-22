package com.themillhousegroup.reactivemongo.mocks

import org.specs2.mutable.Specification
import scala.concurrent.duration.Duration
import com.themillhousegroup.reactivemongo.test.CommonMongoTests

class MongoMocksDefaultDBSpec extends Specification with CommonMongoTests {

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

  "The mockedCollection method" should {
    val testSpec = new Specification with MongoMocks

    "Indicate in the name that the collection is a mock" in {
      val myCollection = testSpec.mockedCollection("myCollection")
      myCollection.name must beEqualTo("myCollection (mock)")
    }

    "Indicate in the full collection name that the collection is a mock" in {
      val myCollection = testSpec.mockedCollection("myCollection")
      myCollection.fullCollectionName must beEqualTo("mock.myCollection")
    }

    "Refer back to the mock DB" in {
      val myCollection = testSpec.mockedCollection("myCollection")
      myCollection.db must beEqualTo(testSpec.mockDB)
    }
  }

}
