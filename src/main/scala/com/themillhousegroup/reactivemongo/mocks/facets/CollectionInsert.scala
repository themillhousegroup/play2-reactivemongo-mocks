package com.themillhousegroup.reactivemongo.mocks.facets

import play.modules.reactivemongo.json.collection.{ JSONQueryBuilder, JSONCollection }
import org.specs2.mock.Mockito
import scala.concurrent.{ ExecutionContext, Future }
import play.api.libs.json._
import org.mockito.stubbing.Answer
import reactivemongo.api.Cursor
import org.mockito.invocation.InvocationOnMock
import reactivemongo.core.commands.{ GetLastError, LastError }

trait CollectionInsert extends MongoMockFacet {

  var uncheckedInserts = Seq[Any]()

  private def setupMongoInserts(targetCollection: JSONCollection,
    insertMatcher: => JsObject,
    ok: Boolean) = {

    // Nothing to mock an answer for - it's unchecked - but we record the insert to be useful
    targetCollection.uncheckedInsert(insertMatcher)(anyPackWrites) answers { o =>
      uncheckedInserts = uncheckedInserts :+ o
      logger.debug(s"unchecked insert of $o, recorded for verification (${uncheckedInserts.size})")
    }

    targetCollection.insert(insertMatcher)(anyPackWrites, anyEC) answers { o =>
      logger.debug(s"Insert of object $o will be considered a ${bool2Success(ok)}")
      Future.successful(mockResult(ok))
    }
  }

  def givenAnyMongoInsertIsOK(targetCollection: JSONCollection, ok: Boolean = true) = {
    setupMongoInserts(targetCollection, anyJs, ok)
  }

  def givenMongoInsertIsOK(targetCollection: JSONCollection, objectToInsert: JsObject, ok: Boolean = true) = {
    setupMongoInserts(targetCollection, org.mockito.Matchers.eq(objectToInsert), ok)
  }
}
