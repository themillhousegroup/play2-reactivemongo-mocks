package com.themillhousegroup.reactivemongo.mocks.facets

import play.modules.reactivemongo.json.collection.{JSONQueryBuilder, JSONCollection}
import org.specs2.mock.Mockito
import reactivemongo.core.commands.LastError
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._




trait CollectionInsert extends MongoMockFacet {
  def givenMongoInsertIsOK(targetCollection:JSONCollection, ok:Boolean) = {
    val mockResult = mock[LastError]
    mockResult.ok returns ok
    targetCollection.insert(any[JsObject])(any[ExecutionContext]) returns Future.successful(mockResult)
  }
}
