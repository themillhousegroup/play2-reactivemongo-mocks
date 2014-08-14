package com.themillhousegroup.reactivemongo.mocks.facets


trait Logging {
  val logger:Logger = LoggerFactory.getLogger(this.getClass)
}
