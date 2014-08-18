package com.themillhousegroup.reactivemongo.mocks.facets

import reactivemongo.api.{Cursor, FailoverStrategy}
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock

//// Reactive Mongo plugin
import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
import play.api.libs.json._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait CollectionFind extends MongoMockFacet {

  /* Requires the use of a Mockito spy, due to the Self-typing on sort(). */
  private def givenMongoCollectionFindReturns[T[J] <: Traversable[J]](targetCollection:JSONCollection,
                                                findMatcher: =>JsObject,
                                                results:T[JsObject]):JSONQueryBuilder = {

    val spiedQB = spy(new JSONQueryBuilder(targetCollection, mock[FailoverStrategy]))

    setupOne(spiedQB, results)
    setupCursor(spiedQB, results)

    spiedQB.sort(any[JsObject]) answers { _ =>
      logger.debug(s"Returning queryBuilder that returns itself in response to sort request")
      spiedQB
    }

    targetCollection.find(findMatcher)(any[Writes[JsObject]]) answers { _ =>
      logger.debug(s"Returning queryBuilder that returns $results in response to find request")
      spiedQB
    }
    spiedQB
  }

  private def setupOne[T[J] <: Traversable[J]](spiedQB:JSONQueryBuilder, results:T[JsObject]) = {
    val oneAnswer = new Answer[Future[Option[JsObject]]] {
      def answer(invocation: InvocationOnMock): Future[Option[JsObject]] = {
        logger.trace(s"Handling ${invocation.getMethod.getName} by returning ${results.headOption}")
        Future.successful (results.headOption)
      }
    }
    org.mockito.Mockito.doAnswer ( oneAnswer ).when(spiedQB).one[JsObject]
  }

  private def setupCursor[T[J] <: Traversable[J]](spiedQB:JSONQueryBuilder, results:T[JsObject]) = {

    val mockCursor = mock[Cursor[JsObject]]
    mockCursor.headOption answers { _ =>
      logger.debug(s"Returning ${results.headOption} as the cursor headOption")
      Future.successful(results.headOption)
    }

//    // TODO: The Enumerator (iteratee) API of Cursor?
//    mockCursor.collect[T](anyInt, anyBoolean) answers { case (upTo:Int, stopOnErr) =>
//      val subList = results.take(upTo)
//      logger.debug(s"Returning ${subList} as the cursor collect")
//      Future.successful(subList)
//    }


    val cursorAnswer = new Answer[Cursor[JsObject]] {
      def answer(invocation: InvocationOnMock): Cursor[JsObject] = {
        logger.trace(s"Handling ${invocation.getMethod.getName} by returning ${results}")
        mockCursor
      }
    }

    org.mockito.Mockito.doAnswer ( cursorAnswer ).when(spiedQB).cursor[JsObject]
  }

  protected def givenMongoCollectionFindAnyReturns[T[J] <: Traversable[J]](targetCollection:JSONCollection,
                                                   results:T[JsObject]):JSONQueryBuilder =
    givenMongoCollectionFindReturns(targetCollection, any[JsObject], results)

  def givenMongoFindAnyReturnsNone(targetCollection:JSONCollection) =
    givenMongoCollectionFindAnyReturns(targetCollection, Seq[JsObject]())
  def givenMongoFindAnyReturnsSome(targetCollection:JSONCollection, result:JsObject) =
    givenMongoCollectionFindAnyReturns(targetCollection, Seq(result))
  def givenMongoFindAnyReturns(targetCollection:JSONCollection, optResult:Option[JsObject]) =
    givenMongoCollectionFindAnyReturns(targetCollection, optResult.toSeq)
  def givenMongoFindAnyReturns[T[X] <: Traversable[X]](targetCollection:JSONCollection, results:T[JsObject]) =
    givenMongoCollectionFindAnyReturns(targetCollection, results)


  protected def givenMongoCollectionFindExactReturns( targetCollection:JSONCollection,
                                                      exactMatch:JsObject,
                                                      result:Option[JsObject]):JSONQueryBuilder =
    givenMongoCollectionFindReturns(targetCollection, org.mockito.Matchers.eq(exactMatch), result.toSeq)

  def givenMongoFindExactReturnsNone(targetCollection:JSONCollection, exactMatch:JsObject) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, None)
  def givenMongoFindExactReturnsItself(targetCollection:JSONCollection, exactMatch:JsObject) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, Some(exactMatch))
  def givenMongoFindExactReturnsSome(targetCollection:JSONCollection, exactMatch:JsObject, result:JsObject) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, Some(result))
  def givenMongoFindExactReturns(targetCollection:JSONCollection, exactMatch:JsObject, optResult:Option[JsObject]) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, optResult)

}
