package com.themillhousegroup.reactivemongo.test

import play.api.libs.json.{JsNumber, JsObject}

trait MongoTestFixtures {
  val testThrowable = new UnsupportedOperationException("Test error")

  val firstSingleFieldObject = JsObject(Seq("foo" -> JsNumber(1)))
  val secondSingleFieldObject = JsObject(Seq("bar" -> JsNumber(2)))
  val thirdSingleFieldObject = JsObject(Seq("baz" -> JsNumber(3)))

}
