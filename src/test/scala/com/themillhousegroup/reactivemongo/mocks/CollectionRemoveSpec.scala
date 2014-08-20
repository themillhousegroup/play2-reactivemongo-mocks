package com.themillhousegroup.reactivemongo.mocks

import com.themillhousegroup.reactivemongo.test.CommonMongoTests
import org.specs2.mutable.Specification

class CollectionRemoveSpec extends Specification with CommonMongoTests {

  "Remove mocking" should {
    "Allow a remove to be considered OK" in new MockedCollectionScope {
      testSpec.givenAnyMongoRemoveIsOK(c)
      resultOf(c.remove(firstSingleFieldObject)) should beTrue
    }

    "Allow an unchecked remove and record it for verification" in new MockedCollectionScope {
      testSpec.givenAnyMongoRemoveIsOK(c)

      testSpec.uncheckedRemoves must beEmpty

      c.uncheckedRemove(firstSingleFieldObject)

      testSpec.uncheckedRemoves must not beEmpty
    }

    "Allow a particular remove to fail, while others succeed" in new MockedCollectionScope {
      testSpec.givenMongoRemoveIsOK(c, firstSingleFieldObject)
      testSpec.givenMongoRemoveIsOK(c, secondSingleFieldObject, false)
      testSpec.givenMongoRemoveIsOK(c, thirdSingleFieldObject, true)

      resultOf(c.remove(firstSingleFieldObject)) should beTrue
      resultOf(c.remove(secondSingleFieldObject)) should beFalse
      resultOf(c.remove(thirdSingleFieldObject)) should beTrue
    }
  }
}
