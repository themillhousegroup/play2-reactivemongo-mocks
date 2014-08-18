package com.themillhousegroup.reactivemongo.mocks.facets

import reactivemongo.api.FailoverStrategy
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock

//// Reactive Mongo plugin
import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
//

import play.api.libs.json._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait CollectionFind extends MongoMockFacet {

  /* Requires the use of a Mockito spy, due to the Self-typing on sort(). */
  private def givenMongoCollectionFindReturns(targetCollection:JSONCollection,
                                                findMatcher: =>JsObject,
                                                result:Option[JsValue]):JSONQueryBuilder = {

    val spiedQB = spy(new JSONQueryBuilder(targetCollection, mock[FailoverStrategy]))

    val oneAnswer = new Answer[Future[Option[JsValue]]] {
      def answer(invocation: InvocationOnMock): Future[Option[JsValue]] = {
        logger.trace(s"Handling ${invocation.getMethod.getName} by returning $result")
        Future.successful (result)
      }
    }

    org.mockito.Mockito.doAnswer ( oneAnswer ).when(spiedQB).one[JsObject]

    spiedQB.sort(any[JsObject]) answers { _ =>
      logger.debug(s"Returning queryBuilder that returns itself in response to sort request")
      spiedQB
    }

    targetCollection.find(findMatcher)(any[Writes[JsObject]]) answers { _ =>
      logger.debug(s"Returning queryBuilder that returns $result in response to find request")
      spiedQB
    }
    spiedQB
  }

  protected def givenMongoCollectionFindAnyReturns(targetCollection:JSONCollection,
                                                   result:Option[JsValue]):JSONQueryBuilder =
    givenMongoCollectionFindReturns(targetCollection, any[JsObject], result)

  def givenMongoFindAnyReturnsNone(targetCollection:JSONCollection) =
    givenMongoCollectionFindAnyReturns(targetCollection, None)
  def givenMongoFindAnyReturnsSome(targetCollection:JSONCollection, result:JsValue) =
    givenMongoCollectionFindAnyReturns(targetCollection, Some(result))
  def givenMongoFindAnyReturns(targetCollection:JSONCollection, optResult:Option[JsValue]) =
    givenMongoCollectionFindAnyReturns(targetCollection, optResult)


  protected def givenMongoCollectionFindExactReturns( targetCollection:JSONCollection,
                                                      exactMatch:JsObject,
                                                      result:Option[JsValue]):JSONQueryBuilder =
    givenMongoCollectionFindReturns(targetCollection, org.mockito.Matchers.eq(exactMatch), result)

  def givenMongoFindExactReturnsNone(targetCollection:JSONCollection, exactMatch:JsObject) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, None)
  def givenMongoFindExactReturnsItself(targetCollection:JSONCollection, exactMatch:JsObject) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, Some(exactMatch))
  def givenMongoFindExactReturnsSome(targetCollection:JSONCollection, exactMatch:JsObject, result:JsValue) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, Some(result))
  def givenMongoFindExactReturns(targetCollection:JSONCollection, exactMatch:JsObject, optResult:Option[JsValue]) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, optResult)

}
