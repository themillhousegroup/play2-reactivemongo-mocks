package com.themillhousegroup.reactivemongo.mocks.facets

import org.specs2.mock.Mockito
import reactivemongo.core.commands.{ GetLastError, LastError }
import org.mockito.Matchers
import play.api.libs.json.{ JsObject, Writes }
import scala.concurrent.ExecutionContext

trait MongoMockFacet extends Mockito with Logging {

  protected def mockResult(ok: Boolean): LastError = {
    val mockResult = mock[LastError]
    mockResult.ok returns ok
    mockResult
  }

  protected def bool2Success(ok: Boolean) = if (ok) "success" else "failure"

  // Useful additional matchers:

  def anyBoolean = Matchers.anyBoolean

  def anyWriteConcern = Matchers.any[GetLastError]

  def anyJsWrites = Matchers.any[Writes[JsObject]]

  def anyJs = Matchers.any[JsObject]

  def anyEC = Matchers.any[ExecutionContext]
}
