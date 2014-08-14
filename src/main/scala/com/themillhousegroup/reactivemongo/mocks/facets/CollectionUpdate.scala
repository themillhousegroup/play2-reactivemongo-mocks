package com.themillhousegroup.reactivemongo.mocks.facets

import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
import org.specs2.mock.Mockito
import reactivemongo.core.commands.LastError
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

trait CollectionUpdate extends MongoMockFacet {

//  def givenMongoUpdateIsOK(targetCollection:JSONCollection, ok:Boolean) = {
//
//    // Nothing to mock an answer for - it's unchecked
//    targetCollection.uncheckedUpdate(selector: S, update: U, upsert: Boolean, multi: Boolean)(implicit selectorWriter: play.api.libs.json.Writes[S], implicit updateWriter: play.api.libs.json.Writes[U])
//    (any[JsObject]) answers { o =>
//      logger.debug(s"Unchecked update of object $o")
//    }
//
//    targetCollection.update(
//      selector: S, update: U, writeConcern: reactivemongo.core.commands.GetLastError, upsert: Boolean, multi: Boolean)(implicit selectorWriter: play.api.libs.json.Writes[S], implicit updateWriter: play.api.libs.json.Writes[U], implicit ec: scala.concurrent.ExecutionContext)
//    (any[JsObject])(any[ExecutionContext]) answers { o =>
//      logger.debug(s"Update of object $o will be considered a ${bool2Success(ok)}")
//      Future.successful(mockResult(ok))
//    }
//  }

}
