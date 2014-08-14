package com.themillhousegroup.reactivemongo.mocks.facets

import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
import org.specs2.mock.Mockito
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._


trait CollectionInsert extends MongoMockFacet {

  private def givenMongoInsertIsOK( targetCollection:JSONCollection,
                                    insertMatcher: =>JsObject,
                                    ok:Boolean) = {

    // Nothing to mock an answer for - it's unchecked
    targetCollection.uncheckedInsert(insertMatcher) answers { o =>
      logger.debug(s"Unchecked insert of object $o")
    }

    targetCollection.insert(insertMatcher)(
                            any[ExecutionContext]) answers { o =>
      logger.debug(s"Insert of object $o will be considered a ${bool2Success(ok)}")
      Future.successful(mockResult(ok))
    }
  }

  def givenAnyMongoInsertIsOK(targetCollection:JSONCollection, ok:Boolean) = {
    givenMongoInsertIsOK(targetCollection, any[JsObject], ok)
  }

}
