package com.themillhousegroup.reactivemongo.mocks

import org.specs2.mutable.Specification


//class MongoMocksDefaultDBSpec extends Specification {
//
//
//  val bs = new Specification with MongoMocks
//
//  "The DefaultDB member of the MongoMocks trait" should {
//    "Support accessing a mocked DefaultDB" in {
//
//      bs.mockDB must not beNull
//    }
//
//    "Support accessing the mocked DefaultDB's name to help diagnose issues, with a default" in {
//      bs.mockDB.name must beEqualTo(bs.mockDatabaseName)
//    }
//  }
//}

class MongoMocksOverriddenNameDefaultDBSpec extends Specification {

  val testDBName = "new db name"

  val ns = new Specification with MongoMocks {
    override lazy val mockDatabaseName = "foo"
  }

  "The DefaultDB member of the MongoMocks trait, with name overridden" should {
    "Support accessing a mocked DefaultDB" in {
      ns.mockDB must not beNull
    }

    "Support accessing the mocked DefaultDB's overridden name to help diagnose issues" in {
      println(s"Yo name is ${ns.mockDB.name}")
      ns.mockDB.name must beEqualTo(testDBName)
    }
  }
}