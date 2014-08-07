package com.themillhousegroup.reactivemongo.mocks

// Reactive Mongo imports

import reactivemongo.api.CollectionProducer
import reactivemongo.api.collections.GenericQueryBuilder
import reactivemongo.api.DefaultDB
import reactivemongo.api.FailoverStrategy

//// Reactive Mongo plugin
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
//
import org.specs2.mock.Mockito
//import play.api.test._
//import play.api.test.Helpers._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.core.commands.{LastError, GetLastError}


/**
 * Still to do:  save, remove, update, uncheckedInsert, uncheckedRemove, uncheckedUpdate
 */
trait MongoMocks extends Mockito {
  this: org.specs2.mutable.Specification =>

  val mockDB = mock[DefaultDB]

  /** Returns a mocked JSONCollection that can be used with the givenMongo... methods in MongoMocks */
  def mockedCollection(name:String):JSONCollection = {
    val mockCollection = mock[JSONCollection]
    mockDB.collection[JSONCollection](org.mockito.Matchers.eq(name), any[FailoverStrategy])(any[CollectionProducer[JSONCollection]]) returns mockCollection
    mockCollection
  }

  /* Requires the use of a Mockito spy, due to the Self-typing on sort(). */
  private def givenMongoCollectionFindReturns(targetCollection:JSONCollection, result:Option[JsValue]) = {
    val spiedQB = spy(new JSONQueryBuilder(targetCollection, mock[FailoverStrategy]))
    org.mockito.Mockito.doReturn(Future.successful (result)).when(spiedQB).one[JsObject]
    spiedQB.sort(any[JsObject]) returns spiedQB
    targetCollection.find(any[JsObject])(any[Writes[JsObject]]) returns spiedQB
  }

  def givenMongoFindReturnsNothing(targetCollection:JSONCollection) = givenMongoCollectionFindReturns(targetCollection, None)
  def givenMongoFindReturnsSome(targetCollection:JSONCollection, result:JsValue) = givenMongoCollectionFindReturns(targetCollection, Some(result))
  def givenMongoFindReturns(targetCollection:JSONCollection, optResult:Option[JsValue]) = givenMongoCollectionFindReturns(targetCollection, optResult)


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
