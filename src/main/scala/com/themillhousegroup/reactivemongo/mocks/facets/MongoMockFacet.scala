package com.themillhousegroup.reactivemongo.mocks.facets

import org.specs2.mock.Mockito
import reactivemongo.core.commands.{ GetLastError, LastError }
import reactivemongo.api.commands.{ UpdateWriteResult, WriteResult, WriteConcern }
import reactivemongo.play.json.JSONSerializationPack

import play.api.libs.json.{ Reads, JsObject, Writes }
import scala.concurrent.ExecutionContext
import reactivemongo.api.{ CursorProducer, ReadPreference }
import org.mockito.Matchers

trait MongoMockFacet extends Mockito with Logging {

  protected def mockResult(ok: Boolean, maybeN: Option[Int] = None, maybeCode: Option[Int] = None): WriteResult = {
    val mockResult = mock[WriteResult]
    mockResult.ok returns ok
    mockResult.n returns maybeN.getOrElse(1)
    mockResult.code returns maybeCode
    mockResult
  }

  protected def mockUpdateResult(ok: Boolean): UpdateWriteResult = {
    val mockResult = mock[UpdateWriteResult]
    mockResult.ok returns ok
    mockResult
  }

  protected def bool2Success(ok: Boolean) = if (ok) "success" else "failure"

  /** Helper method; extracts the first arg from an `answers` param */
  protected def firstArg[T](args: Any): T = {
    val objs = args.asInstanceOf[Array[Object]]
    objs(0).asInstanceOf[T]
  }

  // Useful additional matchers:

  def anyReadPreference = Matchers.any[ReadPreference]

  def anyWriteConcern = Matchers.any[WriteConcern]

  def anyJsReads = Matchers.any[Reads[JsObject]]

  def anyPackReads = Matchers.any[JSONSerializationPack.Reader[JsObject]]

  def anyJsWrites = Matchers.any[Writes[JsObject]]

  def anyPackWrites = Matchers.any[JSONSerializationPack.Writer[JsObject]]

  def anyJs = Matchers.any[JsObject]

  def anyEC = Matchers.any[ExecutionContext]

  def anyCursorProducer = Matchers.any[CursorProducer[JsObject]]
}
