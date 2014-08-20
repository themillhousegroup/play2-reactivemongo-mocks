package com.themillhousegroup.reactivemongo.mocks

// Reactive Mongo imports

import reactivemongo.api.CollectionProducer
import reactivemongo.api.collections.GenericQueryBuilder
import reactivemongo.api.DefaultDB
import reactivemongo.api.FailoverStrategy
import com.themillhousegroup.reactivemongo.mocks.facets._

//// Reactive Mongo plugin
import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
import org.specs2.mock.Mockito

import scala.concurrent.{ExecutionContext, Future}


/**
 * Still to do: remove, uncheckedRemove
 */
trait MongoMocks extends Mockito  with Logging
                                  with CollectionFind
                                  with CollectionInsert
                                  with CollectionSave
                                  with CollectionUpdate {
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
}
