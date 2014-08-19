package com.themillhousegroup.reactivemongo.mocks.facets

import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
import org.specs2.mock.Mockito
import reactivemongo.core.commands.LastError
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

trait CollectionUpdate extends MongoMockFacet {

  var uncheckedUpdates = Seq[Any]()

  private def setupMongoUpdates( targetCollection:JSONCollection,
                                 selectorMatcher: =>JsObject,
                                 updateMatcher: =>JsObject,
                                 ok:Boolean) = {

    // Nothing to mock an answer for - it's unchecked - but we record the update to be useful
    targetCollection.uncheckedUpdate(
      selectorMatcher, updateMatcher, anyBoolean, anyBoolean)(
      anyJsWrites, anyJsWrites) answers { o =>
      uncheckedUpdates = uncheckedUpdates :+ o
      logger.debug(s"unchecked update of $o, recorded for verification (${uncheckedUpdates.size})")
    }

    targetCollection.update(
      selectorMatcher, updateMatcher, anyWriteConcern, anyBoolean, anyBoolean)(
      anyJsWrites, anyJsWrites, any[ExecutionContext]) answers { o =>
      logger.debug(s"Update of object $o will be considered a ${bool2Success(ok)}")
      Future.successful(mockResult(ok))
    }
  }

  def givenAnyMongoUpdateIsOK(targetCollection:JSONCollection, ok:Boolean = true) = {
    setupMongoUpdates(
      targetCollection,
      any[JsObject],
      any[JsObject],
      ok)
  }


  def givenMongoUpdateIsOK(targetCollection:JSONCollection, selector:JsObject, ok:Boolean = true) = {
    setupMongoUpdates(
      targetCollection,
      org.mockito.Matchers.eq(selector),
      any[JsObject],
      ok)
  }

}
