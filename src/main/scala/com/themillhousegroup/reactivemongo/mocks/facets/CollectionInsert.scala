package com.themillhousegroup.reactivemongo.mocks.facets

import reactivemongo.play.json.collection.{ JSONCollection, JSONQueryBuilder }
import org.specs2.mock.Mockito
import scala.concurrent.{ ExecutionContext, Future }
import play.api.libs.json._
import org.mockito.stubbing.Answer
import reactivemongo.api.Cursor
import org.mockito.invocation.InvocationOnMock
import reactivemongo.core.commands.{ GetLastError, LastError }

trait CollectionInsert extends MongoMockFacet {

  var uncheckedInserts = Seq[Any]()

  private def setupMongoInserts[T](targetCollection: JSONCollection,
    insertMatcher: => JsObject,
    ok: Boolean) = {

    // Nothing to mock an answer for - it's unchecked - but we record the insert to be useful
    targetCollection.uncheckedInsert(insertMatcher)(anyPackWrites) answers { args =>
      val o: T = firstArg(args)
      uncheckedInserts = uncheckedInserts :+ o
      logger.debug(s"unchecked insert of $o, recorded for verification (${uncheckedInserts.size})")
    }

    targetCollection.insert(insertMatcher, anyWriteConcern)(anyPackWrites, anyEC) answers { args =>
      val o: T = firstArg(args)
      logger.debug(s"Insert of object $o will be considered a ${bool2Success(ok)}")
      Future.successful(mockResult(ok))
    }
  }

  def givenAnyMongoInsertIsOK[T](targetCollection: JSONCollection, ok: Boolean = true) = {
    setupMongoInserts[T](targetCollection, anyJs, ok)
  }

  def givenMongoInsertIsOK[T](targetCollection: JSONCollection, objectToInsert: JsObject, ok: Boolean = true) = {
    setupMongoInserts[T](targetCollection, org.mockito.Matchers.eq(objectToInsert), ok)
  }
}
