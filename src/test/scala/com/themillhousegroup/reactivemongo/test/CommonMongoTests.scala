package com.themillhousegroup.reactivemongo.test

import scala.concurrent.duration.Duration
import com.themillhousegroup.reactivemongo.mocks.facets.Logging
import reactivemongo.api.commands.WriteResult

import scala.concurrent.{ Future, Await }
import org.specs2.matcher._
import org.specs2.specification.Scope
import org.specs2.mutable.Specification
import com.themillhousegroup.reactivemongo.mocks.MongoMocks
import reactivemongo.play.json.collection.{ JSONCollection }
import play.api.libs.json.JsObject
import play.modules.reactivemongo.json._
import reactivemongo.api.ReadPreference
import play.api.libs.iteratee.Enumerator

trait CommonMongoTests extends Logging
    with MustThrownMatchers
    with MongoTestFixtures {

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  val shortWait = Duration(100L, "millis")

  def waitFor[X](op: => Future[X]): X = Await.result(op, shortWait)

  protected class MockedCollectionScope extends Scope {
    val testSpec = new Specification with MongoMocks {
      val coll = mockedCollection("foo")(this.mockDB)
    }

    val c = testSpec.coll
  }

  def haveThrownTheTestThrowable = {
    throwAn[UnsupportedOperationException].like {
      case e: UnsupportedOperationException => e must beEqualTo(testThrowable)
    }
  }

  def findOne(c: JSONCollection, thingToFind: JsObject): Option[JsObject] = {
    val qb = c.find(thingToFind)
    waitFor(qb.one[JsObject])
  }

  def findCursorHeadOption(c: JSONCollection, thingToFind: JsObject): Option[JsObject] = {
    val qb = c.find(thingToFind)
    Await.result(qb.cursor[JsObject](ReadPreference.nearest).headOption, shortWait)
  }

  def findCursorCollect(c: JSONCollection, thingToFind: JsObject): List[JsObject] = {
    findCursorCollect(c, thingToFind, Int.MaxValue, true)
  }

  def findCursorCollect(c: JSONCollection, thingToFind: JsObject, upTo: Int, stopOnErr: Boolean): List[JsObject] = {
    val qb = c.find(thingToFind)
    Await.result(qb.cursor[JsObject](ReadPreference.nearest).collect[List](upTo, stopOnErr), shortWait)
  }

  def findCursorEnumerator(c: JSONCollection, thingToFind: JsObject, upTo: Int, stopOnErr: Boolean): Enumerator[JsObject] = {
    val qb = c.find(thingToFind)
    qb.cursor[JsObject](ReadPreference.nearest).enumerate(upTo, stopOnErr)
  }

  def resultOf(op: => Future[WriteResult]): Boolean = {
    waitFor(op).ok
  }
}
