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

}