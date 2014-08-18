package com.themillhousegroup.reactivemongo.test

import scala.concurrent.duration.Duration
import com.themillhousegroup.reactivemongo.mocks.facets.Logging
import play.api.libs.json.{JsNumber, JsObject}
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import org.specs2.matcher._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber


trait CommonMongoTests extends Logging with MustThrownMatchers {
  val shortWait = Duration(100L, "millis")

  val testThrowable = new UnsupportedOperationException("Test error")

  val firstSingleFieldObject = JsObject(Seq("foo" -> JsNumber(1)))
  val secondSingleFieldObject = JsObject(Seq("bar" -> JsNumber(2)))
  val thirdSingleFieldObject = JsObject(Seq("baz" -> JsNumber(3)))

  def haveThrownTheTestThrowable = { throwAn[UnsupportedOperationException].like {
      case e:UnsupportedOperationException => e must beEqualTo(testThrowable)
    }
  }

  def findOne(c:JSONCollection, thingToFind:JsObject):Option[JsObject] = {
    val qb = c.find(thingToFind)
    Await.result(qb.one[JsObject], shortWait)
  }

  def findCursorHeadOption(c:JSONCollection, thingToFind:JsObject):Option[JsObject] = {
    val qb = c.find(thingToFind)
    Await.result(qb.cursor[JsObject].headOption, shortWait)
  }

  def findCursorCollect(c:JSONCollection, thingToFind:JsObject):List[JsObject] = {
    findCursorCollect(c, thingToFind, Int.MaxValue, true)
  }

  def findCursorCollect(c:JSONCollection, thingToFind:JsObject, upTo:Int, stopOnErr:Boolean):List[JsObject] = {
    val qb = c.find(thingToFind)
    Await.result(qb.cursor[JsObject].collect[List](upTo, stopOnErr), shortWait)
  }
}
