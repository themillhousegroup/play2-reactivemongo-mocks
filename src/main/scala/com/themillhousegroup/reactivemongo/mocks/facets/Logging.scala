package com.themillhousegroup.reactivemongo.mocks.facets

import org.slf4j._

trait Logging {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
}
