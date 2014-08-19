package com.themillhousegroup.reactivemongo.mocks

import com.themillhousegroup.reactivemongo.test.CommonMongoTests
import org.specs2.mutable.Specification

class CollectionUpdateSpec extends Specification with CommonMongoTests {

  "Insert mocking" should {
    "Allow an insert to be considered OK" in new MockedCollectionScope {
      testSpec.givenAnyMongoInsertIsOK(c)
      resultOf(c.insert(firstSingleFieldObject)) should beTrue
    }

    "Allow an unchecked insert and record it for verification" in new MockedCollectionScope {
      testSpec.givenAnyMongoInsertIsOK(c)

      testSpec.uncheckedInserts must beEmpty

      c.uncheckedInsert(firstSingleFieldObject)

      testSpec.uncheckedInserts must not beEmpty
    }

    "Allow a particular insert to fail, while others succeed" in new MockedCollectionScope {
      testSpec.givenMongoInsertIsOK(c, firstSingleFieldObject)
      testSpec.givenMongoInsertIsOK(c, secondSingleFieldObject, false)
      testSpec.givenMongoInsertIsOK(c, thirdSingleFieldObject, true)

      resultOf(c.insert(firstSingleFieldObject)) should beTrue
      resultOf(c.insert(secondSingleFieldObject)) should beFalse
      resultOf(c.insert(thirdSingleFieldObject)) should beTrue
    }
  }
}
