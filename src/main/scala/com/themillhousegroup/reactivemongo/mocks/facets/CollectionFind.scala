package com.themillhousegroup.reactivemongo.mocks.facets

import reactivemongo.api.{Cursor, FailoverStrategy}
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import scala.collection.generic.CanBuildFrom

//// Reactive Mongo plugin
import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

trait CollectionFind extends MongoMockFacet {

  /* Requires the use of a Mockito spy, due to the Self-typing on sort(). */
  private def givenMongoCollectionFindReturns[T[J] <: Traversable[J]](targetCollection:JSONCollection,
                                                findMatcher: =>JsObject,
                                                results:T[JsObject]):JSONQueryBuilder = {

    val spiedQB = spy(new JSONQueryBuilder(targetCollection, mock[FailoverStrategy]))

    setupQueryBuilder(spiedQB)
    setupOne(spiedQB, Future.successful(results))
    setupCursor(spiedQB, Future.successful(results))

    targetCollection.find(findMatcher)(anyJsWrites) answers { _ =>
      logger.debug(s"Returning queryBuilder that returns $results in response to find request")
      spiedQB
    }
    spiedQB
  }

  // Return "self" in all cases
  private def setupQueryBuilder(spiedQB:JSONQueryBuilder) = {
    val returnSelf = { a:Any =>
      logger.trace(s"Returning queryBuilder that returns itself in response to sort/hint/projection request")
      spiedQB
    }

    spiedQB.sort(anyJs) answers returnSelf
    spiedQB.hint(anyJs) answers returnSelf
    spiedQB.projection(anyJs) answers returnSelf
  }

  private def setupOne[T[J] <: Traversable[J]](spiedQB:JSONQueryBuilder, futureResults:Future[T[JsObject]]) = {
    val oneAnswer = new Answer[Future[Option[JsObject]]] {
      def answer(invocation: InvocationOnMock): Future[Option[JsObject]] = {

        futureResults.map { results =>
          logger.trace(s"Handling ${invocation.getMethod.getName} by returning ${results.headOption}")
          results.headOption
        }
      }
    }
    org.mockito.Mockito.doAnswer ( oneAnswer ).when(spiedQB).one[JsObject]
  }

  private def setupCursor[T[J] <: Traversable[J]](spiedQB:JSONQueryBuilder, futureResults:Future[T[JsObject]]) = {

    val mockCursor = mock[Cursor[JsObject]]

    mockCursor.headOption answers { _ =>
      futureResults.map { results =>
        logger.debug(s"Returning ${results.headOption} as the cursor headOption")
        results.headOption
      }
    }

    // TODO: The Enumerator (iteratee) API of Cursor?


    mockCursor.collect[Traversable](
      anyInt, anyBoolean)(
      any[CanBuildFrom[Traversable[_], JsObject, Traversable[JsObject]]],
      anyEC) answers { upTo =>

      futureResults.map { results =>
        val size = upTo.asInstanceOf[Int]
        val subList = if (size == 0) { results } else { results.take(size) }
        logger.debug(s"Returning ${subList} as the cursor collect")
        subList
      }
    }


    val cursorAnswer = new Answer[Cursor[JsObject]] {
      def answer(invocation: InvocationOnMock): Cursor[JsObject] = {
        logger.trace(s"Handling ${invocation.getMethod.getName} by returning a Cursor around ${futureResults}")
        mockCursor
      }
    }

    org.mockito.Mockito.doAnswer ( cursorAnswer ).when(spiedQB).cursor[JsObject]
  }

  def givenMongoFindFailsWith(targetCollection:JSONCollection, throwable:Throwable) = {
    val qb = givenMongoFindAnyReturnsNone(targetCollection)
    setupOne(qb, Future.failed(throwable))
    setupCursor(qb, Future.failed(throwable))
  }

  protected def givenMongoCollectionFindAnyReturns[T[J] <: Traversable[J]](targetCollection:JSONCollection,
                                                   results:T[JsObject]):JSONQueryBuilder =
    givenMongoCollectionFindReturns(targetCollection, anyJs, results)

  def givenMongoFindAnyReturnsNone(targetCollection:JSONCollection) =
    givenMongoCollectionFindAnyReturns(targetCollection, Seq[JsObject]())
  def givenMongoFindAnyReturnsSome(targetCollection:JSONCollection, result:JsObject) =
    givenMongoCollectionFindAnyReturns(targetCollection, Seq(result))
  def givenMongoFindAnyReturns(targetCollection:JSONCollection, optResult:Option[JsObject]) =
    givenMongoCollectionFindAnyReturns(targetCollection, optResult.toSeq)
  def givenMongoFindAnyReturns[T[J] <: Traversable[J]](targetCollection:JSONCollection, results:T[JsObject]) =
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
