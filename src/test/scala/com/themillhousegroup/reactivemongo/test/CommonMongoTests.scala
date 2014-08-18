package com.themillhousegroup.reactivemongo.test

import scala.concurrent.duration.Duration
import com.themillhousegroup.reactivemongo.mocks.facets.Logging
import play.api.libs.json.{JsNumber, JsObject}
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global


trait CommonMongoTests extends Logging {
  val shortWait = Duration(100L, "millis")

  val firstSingleFieldObject = JsObject(Seq("foo" -> JsNumber(1)))
  val secondSingleFieldObject = JsObject(Seq("bar" -> JsNumber(2)))
  val thirdSingleFieldObject = JsObject(Seq("baz" -> JsNumber(3)))


  def findOne(c:JSONCollection, thingToFind:JsObject):Option[JsObject] = {
    val qb = c.find(thingToFind)
    Await.result(qb.one[JsObject], shortWait)
  }

  def findCursorHeadOption(c:JSONCollection, thingToFind:JsObject):Option[JsObject] = {
    val qb = c.find(thingToFind)
    Await.result(qb.cursor[JsObject].headOption, shortWait)
  }

  def findCursorCollect(c:JSONCollection, thingToFind:JsObject):Future[List[JsObject]] = {
    val qb = c.find(thingToFind)
    qb.cursor[JsObject].collect[List](Int.MaxValue)
  }
}
