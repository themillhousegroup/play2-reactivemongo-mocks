package com.themillhousegroup.reactivemongo.mocks

import com.themillhousegroup.reactivemongo.test.CommonMongoTests
import org.specs2.mutable.Specification
import play.modules.reactivemongo.json._

class CollectionUpdateSpec extends Specification with CommonMongoTests {

  "Update mocking" should {
    "Allow an update to be considered OK" in new MockedCollectionScope {
      testSpec.givenAnyMongoUpdateIsOK(c)
      resultOf(c.update(firstSingleFieldObject, secondSingleFieldObject)) should beTrue
    }

    "Allow an unchecked update and record it for verification" in new MockedCollectionScope {
      testSpec.givenAnyMongoUpdateIsOK(c)

      testSpec.uncheckedUpdates must beEmpty

      c.uncheckedUpdate(firstSingleFieldObject, secondSingleFieldObject)

      testSpec.uncheckedUpdates must not beEmpty
    }

    "Allow a particular update to fail, while others succeed" in new MockedCollectionScope {
      testSpec.givenMongoUpdateIsOK(c, firstSingleFieldObject)
      testSpec.givenMongoUpdateIsOK(c, secondSingleFieldObject, false)
      testSpec.givenMongoUpdateIsOK(c, thirdSingleFieldObject, true)

      resultOf(c.update(firstSingleFieldObject, firstSingleFieldObject)) should beTrue
      resultOf(c.update(secondSingleFieldObject, firstSingleFieldObject)) should beFalse
      resultOf(c.update(thirdSingleFieldObject, firstSingleFieldObject)) should beTrue
    }
  }
}
