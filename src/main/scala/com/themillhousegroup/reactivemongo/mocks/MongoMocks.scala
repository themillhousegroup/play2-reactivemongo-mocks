package com.themillhousegroup.reactivemongo.mocks

// Reactive Mongo imports

import reactivemongo.api.CollectionProducer
import reactivemongo.api.collections.GenericQueryBuilder
import reactivemongo.api.DefaultDB
import reactivemongo.api.FailoverStrategy
import com.themillhousegroup.reactivemongo.logging.Logging

//// Reactive Mongo plugin
import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
//
import org.specs2.mock.Mockito
//import play.api.test._
//import play.api.test.Helpers._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.core.commands.LastError


/**
 * Still to do:  save, remove, update, uncheckedInsert, uncheckedRemove, uncheckedUpdate
 */
trait MongoMocks extends Mockito with Logging {
  this: org.specs2.mutable.Specification =>

  lazy val mockDatabaseName = "mockDB"
  val mockDB = mock[DefaultDB]
  mockDB.name answers { _ =>
    logger.debug(s"Returning mocked $mockDatabaseName DB")
    mockDatabaseName
  }

  /** Returns a mocked JSONCollection that can be used with the givenMongo... methods in MongoMocks */
  def mockedCollection(name:String):JSONCollection = {
    val mockCollection = mock[JSONCollection]
    mockDB
      .collection[JSONCollection](
        org.mockito.Matchers.eq(name),
        any[FailoverStrategy])(
        any[CollectionProducer[JSONCollection]]) answers { _ =>
          logger.debug(s"Returning mocked $name collection")
          mockCollection
        }
    mockCollection
  }

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


  def givenMongoInsertIsOK(targetCollection:JSONCollection, ok:Boolean) = {
    val mockResult = mock[LastError]
    mockResult.ok returns ok
    targetCollection.insert(any[JsObject])(any[ExecutionContext]) returns Future.successful(mockResult)
  }

  def queryResponse(response:Option[JsObject]) = {
    val qb = mock[GenericQueryBuilder[JsObject, Reads, Writes]]
    qb.one[JsObject] returns Future.successful(response)
    qb
  }
}
