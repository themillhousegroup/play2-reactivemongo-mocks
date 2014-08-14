package com.themillhousegroup.reactivemongo.logging

import org.slf4j._

trait Logging {
  val logger:Logger = LoggerFactory.getLogger(this.getClass)
}
