package com.themillhousegroup.reactivemongo.mocks.facets

import reactivemongo.play.json.collection.{ JSONCollection, JSONQueryBuilder }
import org.specs2.mock.Mockito
import scala.concurrent.{ ExecutionContext, Future }
import play.api.libs.json._
import org.mockito.stubbing.Answer
import reactivemongo.api.Cursor
import org.mockito.invocation.InvocationOnMock
import reactivemongo.core.commands.{ GetLastError, LastError }

trait CollectionSave extends MongoMockFacet {

  var uncheckedSaves = Seq[Any]()

  private def setupMongoSave(targetCollection: JSONCollection,
    saveMatcher: => JsObject,
    ok: Boolean) = {

    targetCollection.save(saveMatcher)(anyEC) answers { args =>
      val o: JsObject = firstArg(args)
      logger.debug(s"Save of object $o will be considered a ${bool2Success(ok)}")
      Future.successful(mockResult(ok))
    }
  }

  def givenAnyMongoSaveIsOK(targetCollection: JSONCollection, ok: Boolean = true) = {
    setupMongoSave(targetCollection, anyJs, ok)
  }

  def givenMongoSaveIsOK(targetCollection: JSONCollection, objectToSave: JsObject, ok: Boolean = true) = {
    setupMongoSave(targetCollection, org.mockito.Matchers.eq(objectToSave), ok)
  }
}
