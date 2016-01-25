package com.themillhousegroup.reactivemongo.mocks

import org.specs2.mutable.Specification
import scala.concurrent.duration.Duration
import com.themillhousegroup.reactivemongo.test.CommonMongoTests

import play.api.mvc.{ Action, Controller }
import play.api.libs.json._

// Reactive Mongo imports
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}
import reactivemongo.play.json._
import play.modules.reactivemongo.json.collection._
import scala.concurrent.Future

class TestController(val reactiveMongoApi: ReactiveMongoApi)
    extends Controller with MongoController with ReactiveMongoComponents {

  def collection: JSONCollection = db.collection[JSONCollection]("persons")

  def listAll = Action.async {
    listAllPersons.map { persons =>
      Ok(Json.arr(persons))
    }
  }

  def listAllPersons: Future[List[JsObject]] = {
    collection.find(Json.obj()).cursor[JsObject].collect[List]()
  }
}

class MongoMocksDependencyInjectionSpec extends Specification with CommonMongoTests with MongoMocks {

  // This pair of tests make sure that behaviour can be specified using the withCollection helper 
  //"Using the withCollection helper" should {
  // "Allow a fake ReactiveMongoApi to be injected and return empty" in {
  //
  //
  //		}
  //	}

  // This pair of tests make sure that behaviour can be specified once and reused in each test
  "Play 2.4 dependency injected-style" should {

    val mockedPersons = mockedCollection("persons")
    val people = List(
      Json.obj("name" -> "Alice"),
      Json.obj("name" -> "Bob")
    )
    givenMongoCollectionFindAnyReturns(mockedPersons, people)

    "Allow a fake ReactiveMongoApi to be injected and return empty" in {

      givenMongoCollectionFindAnyReturns(mockedPersons, List[JsObject]())
      val testInstance = new TestController(mockReactiveMongoApi)

      testInstance.collection must not beNull

      waitFor(testInstance.listAllPersons) must beEmpty
    }

    "Allow a fake ReactiveMongoApi to be injected and return values" in {

      val testInstance = new TestController(mockReactiveMongoApi)

      waitFor(testInstance.listAllPersons) must haveSize(2)
    }
  }

}
