package com.themillhousegroup.reactivemongo.mocks.facets

import org.specs2.mock.Mockito
import reactivemongo.core.commands.LastError
import org.mockito.Matchers


trait MongoMockFacet extends Mockito with Logging {

  protected def mockResult(ok:Boolean) = {
    val mockResult = mock[LastError]
    mockResult.ok returns ok
  }


  protected def bool2Success(ok:Boolean) = if (ok) "success" else "failure"

  def anyBoolean = Matchers.anyBoolean
}
