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
  def animalCollection: JSONCollection = db.collection[JSONCollection]("animals")

  def listAll = Action.async {
    listAllPersons.map { persons =>
      Ok(Json.arr(persons))
    }
  }

  def listAllPersons: Future[List[JsObject]] = {
    collection.find(Json.obj()).cursor[JsObject].collect[List]()
  }

  def listAllAnimals: Future[List[JsObject]] = {
    animalCollection.find(Json.obj()).cursor[JsObject].collect[List]()
  }
}

class MongoMocksDependencyInjectionSpec extends Specification with CommonMongoTests with MongoMocks {

  // This pair of tests make sure that behaviour can be specified once and reused in each test
  "Play 2.4 dependency injected-style" should {

    val mockedPersons = mockedCollection("persons")(this.mockDB)
    val mockedAnimals = mockedCollection("animals")(this.mockDB)
    val people = List(
      Json.obj("name" -> "Alice"),
      Json.obj("name" -> "Bob")
    )
    val animals = List(
      Json.obj("species" -> "Canis"),
      Json.obj("species" -> "Felis")
    )
    givenMongoCollectionFindAnyReturns(mockedPersons, people)
    givenMongoCollectionFindAnyReturns(mockedAnimals, animals)

    val testInstance = new TestController(mockReactiveMongoApi)

    "Allow a fake ReactiveMongoApi to be injected and return values from a collection" in {

      waitFor(testInstance.listAllPersons) must haveSize(2)
    }

    "Allow a fake ReactiveMongoApi to be injected and return values from another collection" in {

      waitFor(testInstance.listAllAnimals) must haveSize(2)
    }

    // CHeck the scope is independent:
    "Permit truly independent tests via the scope mechanism (1)" in new MongoMockScope {
      val scopeInstance = new TestController(reactiveMongoApi)
      val persons = mockedCollection("persons")
      givenMongoCollectionFindAnyReturns(persons, people)
      waitFor(scopeInstance.listAllPersons) must haveSize(2)
    }

    "Permit truly independent tests via the scope mechanism (2)" in new MongoMockScope {
      val scopeInstance = new TestController(reactiveMongoApi)
      val persons = mockedCollection("persons")
      givenMongoCollectionFindAnyReturns(persons, List[JsObject]())
      waitFor(scopeInstance.listAllPersons) must beEmpty
    }
  }

}
