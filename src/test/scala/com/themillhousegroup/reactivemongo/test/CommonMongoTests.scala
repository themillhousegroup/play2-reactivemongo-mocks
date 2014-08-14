package com.themillhousegroup.reactivemongo.test

import scala.concurrent.duration.Duration
import com.themillhousegroup.reactivemongo.mocks.facets.Logging

trait CommonMongoTests extends Logging {
  val shortWait = Duration(100L, "millis")

}
