package com.themillhousegroup.reactivemongo.mocks.facets

import reactivemongo.api.{ QueryOpts, Cursor, FailoverStrategy }
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import scala.collection.generic.CanBuildFrom
import org.mockito.Mockito._
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.Some
import play.modules.reactivemongo.json.collection.JSONQueryBuilder
import play.api.libs.json.JsObject
import play.api.libs.iteratee.Enumerator
import reactivemongo.core.protocol.Response
import reactivemongo.api.collections.BatchCommands
import reactivemongo.api.commands.CountCommand

//// Reactive Mongo plugin
import play.modules.reactivemongo.json.collection.{ JSONQueryBuilder, JSONCollection }
import play.api.libs.json._
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.ExecutionContext.Implicits.global

trait CollectionFind extends MongoMockFacet {

  /* Requires the use of a Mockito spy, due to the Self-typing on sort(). */
  private def givenMongoCollectionFindReturns[T[J] <: Traversable[J], H](targetCollection: JSONCollection,
    findMatcher: => JsObject,
    results: T[JsObject]): JSONQueryBuilder = {


    val spiedQB = spy(new JSONQueryBuilder(targetCollection, mock[FailoverStrategy]))

    setupQueryBuilder(spiedQB)
    setupOne(spiedQB, Right(results))
    setupCursor(spiedQB, Right(results))

    targetCollection.find(findMatcher)(anyPackWrites) answers { _ =>
      logger.debug(s"Returning queryBuilder that returns $results in response to find request")
      spiedQB
    }


    targetCollection.count[H](
      Some(findMatcher), anyInt, anyInt, any[Option[H]])(
      any[H => targetCollection.BatchCommands.CountCommand.Hint], anyEC
    ) returns Future.successful(results.size)

    spiedQB
  }

  // Return "self" in all cases
  private def setupQueryBuilder(spiedQB: JSONQueryBuilder) = {
    val returnSelf = { a: Any =>
      logger.trace(s"Returning queryBuilder that returns itself in response to sort/hint/projection etc request")
      spiedQB
    }

    spiedQB.sort(anyJs) answers returnSelf
    spiedQB.hint(anyJs) answers returnSelf
    spiedQB.projection(anyJs) answers returnSelf
    spiedQB.options(any[QueryOpts]) answers returnSelf
    spiedQB.snapshot(anyBoolean) answers returnSelf
    spiedQB.comment(anyString) answers returnSelf
    spiedQB.maxTimeMs(anyLong) answers returnSelf
  }

  private def setupOne[T[J] <: Traversable[J]](spiedQB: JSONQueryBuilder, throwableOrResults: Either[Throwable, T[JsObject]]) = {
    val futureResults = eitherToFutureResult(throwableOrResults)

    val oneAnswer = new Answer[Future[Option[JsObject]]] {
      def answer(invocation: InvocationOnMock): Future[Option[JsObject]] = {

        futureResults.map { results =>
          logger.trace(s"Handling ${invocation.getMethod.getName} by returning ${results.headOption}")
          results.headOption
        }
      }
    }
    org.mockito.Mockito.doAnswer(oneAnswer).when(spiedQB).one[JsObject](anyJsReads, anyEC)
    org.mockito.Mockito.doAnswer(oneAnswer).when(spiedQB).one[JsObject](anyReadPreference)(anyJsReads, anyEC)
  }

  private def setupCursor[T[J] <: Traversable[J]](spiedQB: JSONQueryBuilder, throwableOrResults: Either[Throwable, T[JsObject]]) = {
    val futureResults = eitherToFutureResult(throwableOrResults)
    val mockCursor = mock[Cursor[JsObject]]

    mockCursor.headOption answers { _ =>
      futureResults.map { results =>
        logger.debug(s"Returning ${results.headOption} as the cursor headOption")
        results.headOption
      }
    }

    // The Enumerator (iteratee) API of Cursor
    setupCursorEnumeratorMocks(mockCursor, throwableOrResults)

    mockCursor.collect[Traversable](
      anyInt, anyBoolean)(
        any[CanBuildFrom[Traversable[_], JsObject, Traversable[JsObject]]],
        anyEC) answers { args =>

          val size: Int = firstArg(args)
          futureResults.map { results =>
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

    org.mockito.Mockito.doAnswer(cursorAnswer).when(spiedQB).cursor[JsObject](anyPackReads, anyEC, anyCursorProducer)
    org.mockito.Mockito.doAnswer(cursorAnswer).when(spiedQB).cursor[JsObject](anyReadPreference, anyBoolean)(anyPackReads, anyEC, anyCursorProducer)
  }

  private def setupCursorEnumeratorMocks[T[J] <: Traversable[J]](mockCursor: Cursor[JsObject], throwableOrResults: Either[Throwable, T[JsObject]]): Unit = {
    val (enumerator: Enumerator[JsObject], bulkEnumerator: Enumerator[Iterator[JsObject]]) = if (throwableOrResults.isLeft) {
      // TODO throw the throwable when the Enumerator is accessed:
      (mock[Enumerator[JsObject]], mock[Enumerator[Iterator[JsObject]]])
    } else {
      val resultsProjection = throwableOrResults.right
      resultsProjection.foreach { results =>
        val iterableOfResults = Seq(results)
        (Enumerator.enumerate(results), Enumerator.enumerate(iterableOfResults))
      }
    }
    val responseEnumerator = mock[Enumerator[Response]] // Not mocking this one beyond the basics
    mockCursor.enumerate(anyInt, anyBoolean)(anyEC) returns enumerator
    mockCursor.enumerateBulks(anyInt, anyBoolean)(anyEC) returns bulkEnumerator
    mockCursor.enumerateResponses(anyInt, anyBoolean)(anyEC) returns responseEnumerator
    mockCursor.rawEnumerateResponses(anyInt)(anyEC) returns responseEnumerator
  }

  private def eitherToFutureResult[T[J] <: Traversable[J]](throwableOrResults: Either[Throwable, T[JsObject]]): Future[T[JsObject]] = {
    throwableOrResults.fold(t => Future.failed(t), coll => Future.successful(coll))
  }

  /**
   * Rather than blindly returning the same thing(s) in response to a find() call,
   * actually scan through "dataSource" in the same way as MongoDB would, finding matches to
   * the find argument.
   */
  def givenMongoFindOperatesOn(targetCollection: JSONCollection, dataSource: Traversable[JsObject]) = {

    val spiedQB = spy(new JSONQueryBuilder(targetCollection, mock[FailoverStrategy]))

    setupQueryBuilder(spiedQB)

//    targetCollection.count[H](Some(anyJs), anyInt, anyInt, any[Option[_]])(
//      any[H => targetCollection.BatchCommands.CountCommand.Hint], anyEC
//    ) returns Future.successful(dataSource.size)

    targetCollection.find(anyJs)(anyPackWrites) answers { args =>
      val criteria: JsObject = firstArg(args)
      val filteredContents = dataSource.filter { row =>
        criteria.fields.forall {
          case (fieldName, fieldValue) =>
            row.keys.contains(fieldName) &&
              row.value(fieldName) == fieldValue
        }
      }
      logger.debug(s"Returning queryBuilder that returns $filteredContents in response to find request using criteria $criteria")

      setupOne(spiedQB, Right(filteredContents))
      setupCursor(spiedQB, Right(filteredContents))
      spiedQB
    }

    spiedQB
  }

  def givenMongoFindFailsWith(targetCollection: JSONCollection, throwable: Throwable) = {
    val qb = givenMongoFindAnyReturnsNone(targetCollection)
    setupOne(qb, Left(throwable))
    setupCursor(qb, Left(throwable))
  }

  protected def givenMongoCollectionFindAnyReturns[T[J] <: Traversable[J]](targetCollection: JSONCollection,
    results: T[JsObject]): JSONQueryBuilder =
    givenMongoCollectionFindReturns(targetCollection, anyJs, results)

  def givenMongoFindAnyReturnsNone(targetCollection: JSONCollection) =
    givenMongoCollectionFindAnyReturns(targetCollection, Seq[JsObject]())
  def givenMongoFindAnyReturnsSome(targetCollection: JSONCollection, result: JsObject) =
    givenMongoCollectionFindAnyReturns(targetCollection, Seq(result))
  def givenMongoFindAnyReturns(targetCollection: JSONCollection, optResult: Option[JsObject]) =
    givenMongoCollectionFindAnyReturns(targetCollection, optResult.toSeq)
  def givenMongoFindAnyReturns[T[J] <: Traversable[J]](targetCollection: JSONCollection, results: T[JsObject]) =
    givenMongoCollectionFindAnyReturns(targetCollection, results)

  protected def givenMongoCollectionFindExactReturns(targetCollection: JSONCollection,
    exactMatch: JsObject,
    result: Option[JsObject]): JSONQueryBuilder =
    givenMongoCollectionFindReturns(targetCollection, org.mockito.Matchers.eq(exactMatch), result.toSeq)

  def givenMongoFindExactReturnsNone(targetCollection: JSONCollection, exactMatch: JsObject) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, None)
  def givenMongoFindExactReturnsItself(targetCollection: JSONCollection, exactMatch: JsObject) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, Some(exactMatch))
  def givenMongoFindExactReturnsSome(targetCollection: JSONCollection, exactMatch: JsObject, result: JsObject) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, Some(result))
  def givenMongoFindExactReturns(targetCollection: JSONCollection, exactMatch: JsObject, optResult: Option[JsObject]) =
    givenMongoCollectionFindExactReturns(targetCollection, exactMatch, optResult)

}
