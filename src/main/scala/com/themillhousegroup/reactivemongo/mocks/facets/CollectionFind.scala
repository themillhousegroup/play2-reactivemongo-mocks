package com.themillhousegroup.reactivemongo.mocks.facets

import com.themillhousegroup.reactivemongo.mocks.MongoMocks
import reactivemongo.api.CollectionProducer
import reactivemongo.api.collections.GenericQueryBuilder
import reactivemongo.api.DefaultDB
import reactivemongo.api.FailoverStrategy

//// Reactive Mongo plugin
import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
//
import org.specs2.mock.Mockito

import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.core.commands.LastError

trait CollectionFind extends MongoMockFacet {
//  this: MongoMocks =>

  /* Requires the use of a Mockito spy, due to the Self-typing on sort(). */
  protected def givenMongoCollectionFindReturns(targetCollection:JSONCollection,
                                                findMatcher: =>JsObject,
                                                result:Option[JsValue]):JSONCollection = {
    val spiedQB = spy(new JSONQueryBuilder(targetCollection, mock[FailoverStrategy]))
    org.mockito.Mockito.doReturn(Future.successful (result)).when(spiedQB).one[JsObject]
    spiedQB.sort(findMatcher) answers { _ =>
      logger.debug(s"Returning queryBuilder that returns $result to sort request")
      spiedQB
    }
    targetCollection.find(findMatcher)(any[Writes[JsObject]]) answers { _ =>
      logger.debug(s"Returning queryBuilder that returns $result to find request")
      spiedQB
    }
    targetCollection
  }

  protected def givenMongoCollectionFindAnyReturns(targetCollection:JSONCollection,
                                                   result:Option[JsValue]):JSONCollection =
    givenMongoCollectionFindReturns(targetCollection, any[JsObject], result)

  def givenMongoFindReturnsNothing(targetCollection:JSONCollection) =
    givenMongoCollectionFindAnyReturns(targetCollection, None)
  def givenMongoFindReturnsSome(targetCollection:JSONCollection, result:JsValue) =
    givenMongoCollectionFindAnyReturns(targetCollection, Some(result))
  def givenMongoFindReturns(targetCollection:JSONCollection, optResult:Option[JsValue]) =
    givenMongoCollectionFindAnyReturns(targetCollection, optResult)

}
