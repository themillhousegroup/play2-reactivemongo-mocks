package com.themillhousegroup.reactivemongo.mocks

import org.specs2.mutable.Specification
import play.api.libs.json.{JsString, JsObject}
import play.modules.reactivemongo.json.collection.JSONCollection
import com.themillhousegroup.reactivemongo.test.CommonMongoTests
import org.specs2.specification.Scope
import scala.concurrent.Await

class CollectionFindSpec extends Specification with CommonMongoTests {

  "The mockedCollection facility" should {

    "return null if no named collection matches" in new MockedCollectionScope  {
      testSpec.mockDB.collection("bar") must beNull
    }

    "return a mock if named collection matches" in new MockedCollectionScope {
      testSpec.mockDB.collection[JSONCollection]("foo") must not beNull
    }

  }

  "find-any (one) support" should {

    "be able to mock the find-any call to return nothing" in new MockedCollectionScope {
      testSpec.givenMongoFindAnyReturnsNone(c)
      findOne(c, JsObject(Nil)) must beNone
    }

    "be able to mock the find-any call to return one thing" in new MockedCollectionScope {

      testSpec.givenMongoFindAnyReturnsSome(c, firstSingleFieldObject)
      findOne(c, JsObject(Nil))  must beSome(firstSingleFieldObject)
    }

    "be able to mock the find-any call to return an Option" in new MockedCollectionScope {
      val searchedOption = Some(firstSingleFieldObject)

      testSpec.givenMongoFindAnyReturns(c, searchedOption)
      findOne(c, JsObject(Nil)) must beEqualTo(searchedOption)
    }
  }

  "find-any (cursor) support" should {
    "be able to mock the find-any call to return a collection's headOption" in new MockedCollectionScope {
      val returnValues = List(firstSingleFieldObject, secondSingleFieldObject)

      testSpec.givenMongoFindAnyReturns(c, returnValues)
      findCursorHeadOption(c, JsObject(Nil)) must beEqualTo(returnValues.headOption)
    }

    "be able to mock the find-any call to return a collection via collect()" in new MockedCollectionScope {
      val returnValues = List(firstSingleFieldObject, secondSingleFieldObject)

      testSpec.givenMongoFindAnyReturns(c, returnValues)
      findCursorCollect(c, JsObject(Nil)) must beEqualTo(returnValues)
    }

    "be able to mock the find-any call to return a collection via collect() with upTo limit supplied" in new MockedCollectionScope {
      val returnValues = List(firstSingleFieldObject, secondSingleFieldObject, thirdSingleFieldObject)

      testSpec.givenMongoFindAnyReturns(c, returnValues)
      findCursorCollect(c, JsObject(Nil), 2, false) must beEqualTo(returnValues.take(2))
    }

    "be able to mock the find-any call to return a collection via collect() with upTo limit supplied as zero, meaning 'all'" in new MockedCollectionScope {
      val returnValues = List(firstSingleFieldObject, secondSingleFieldObject, thirdSingleFieldObject)

      testSpec.givenMongoFindAnyReturns(c, returnValues)
      findCursorCollect(c, JsObject(Nil), 0, false) must beEqualTo(returnValues)
    }
  }

  "find-exact support" should {

    "be able to mock the find-exact call to return nothing" in new MockedCollectionScope {
      testSpec.givenMongoFindExactReturnsNone(c, firstSingleFieldObject)
      findOne(c, firstSingleFieldObject) must beNone
    }

    "be able to mock the find-exact call to return itself" in new MockedCollectionScope {
      val searchedThing = JsString("thing")

      testSpec.givenMongoFindExactReturnsItself(c, firstSingleFieldObject)
      findOne(c, firstSingleFieldObject) must beSome(firstSingleFieldObject)
    }

    "be able to mock the find-exact call to return some other thing" in new MockedCollectionScope {
      val searchedThing = JsString("thing")

      testSpec.givenMongoFindExactReturnsSome(c, firstSingleFieldObject, secondSingleFieldObject)
      findOne(c, firstSingleFieldObject) must beSome(secondSingleFieldObject)
    }

    "be able to mock the find-exact call to return an Option" in new MockedCollectionScope {
      val searchedOption = Some(firstSingleFieldObject)

      testSpec.givenMongoFindExactReturns(c, firstSingleFieldObject, searchedOption)
      findOne(c, firstSingleFieldObject) must beEqualTo(searchedOption)
    }
  }

  "query builder support" should {

    class QBTestScope extends MockedCollectionScope {
      val qb = {
        testSpec.givenMongoFindExactReturnsNone(c, firstSingleFieldObject)
        c.find(firstSingleFieldObject)
      }
    }


    "permit a sort order to be specified without error" in new QBTestScope {
      qb.sort(secondSingleFieldObject) must beEqualTo(qb)
    }

    "permit a hint to be specified without error" in new QBTestScope {
      qb.hint(secondSingleFieldObject) must beEqualTo(qb)
    }

    "permit a projection to be specified without error" in new QBTestScope {
      qb.projection(secondSingleFieldObject) must beEqualTo(qb)
    }
  }

  "collection find failure support" should {
    "allow a find-one call to return a failed future" in new MockedCollectionScope {
      testSpec.givenMongoFindFailsWith(c, testThrowable)

      findOne(c, firstSingleFieldObject) must haveThrownTheTestThrowable
    }

    "allow a find-cursor-headOption call to return a failed future" in new MockedCollectionScope {
      testSpec.givenMongoFindFailsWith(c, testThrowable)

      findCursorHeadOption(c, firstSingleFieldObject) must haveThrownTheTestThrowable
    }

    "allow a find-cursor-collect call to return a failed future" in new MockedCollectionScope {
      testSpec.givenMongoFindFailsWith(c, testThrowable)

      findCursorCollect(c, firstSingleFieldObject) must haveThrownTheTestThrowable
    }
  }

  "setting multiple behaviours" should {
    "allow find methods to return None when something is not found" in new MockedCollectionScope {
      testSpec.givenMongoFindAnyReturnsNone(c)
      testSpec.givenMongoFindExactReturnsItself(c, firstSingleFieldObject)
      testSpec.givenMongoFindExactReturnsItself(c, thirdSingleFieldObject)

      findOne(c, secondSingleFieldObject) must beNone
      findOne(c, firstSingleFieldObject) must beSome(firstSingleFieldObject)
      findOne(c, thirdSingleFieldObject) must beSome(thirdSingleFieldObject)
    }

    "require behaviour to be specified in order from general to specific" in new MockedCollectionScope {

      testSpec.givenMongoFindExactReturnsItself(c, firstSingleFieldObject)
      testSpec.givenMongoFindExactReturnsItself(c, thirdSingleFieldObject)
      testSpec.givenMongoFindAnyReturnsNone(c)

      findOne(c, firstSingleFieldObject) must beNone
      findOne(c, secondSingleFieldObject) must beNone
      findOne(c, thirdSingleFieldObject) must beNone
    }
  }
}
