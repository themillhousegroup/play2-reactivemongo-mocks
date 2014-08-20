package com.themillhousegroup.reactivemongo.mocks

import com.themillhousegroup.reactivemongo.test.CommonMongoTests
import org.specs2.mutable.Specification

class CollectionSaveSpec extends Specification with CommonMongoTests {

  "Save mocking" should {
    "Allow a save to be considered OK" in new MockedCollectionScope {
      testSpec.givenAnyMongoSaveIsOK(c)
      resultOf(c.save(firstSingleFieldObject)) should beTrue
    }

    "Allow a particular save to fail, while others succeed" in new MockedCollectionScope {
      testSpec.givenMongoSaveIsOK(c, firstSingleFieldObject)
      testSpec.givenMongoSaveIsOK(c, secondSingleFieldObject, false)
      testSpec.givenMongoSaveIsOK(c, thirdSingleFieldObject, true)

      resultOf(c.save(firstSingleFieldObject)) should beTrue
      resultOf(c.save(secondSingleFieldObject)) should beFalse
      resultOf(c.save(thirdSingleFieldObject)) should beTrue
    }
  }
}
