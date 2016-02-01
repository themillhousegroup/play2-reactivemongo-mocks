play2-reactivemongo-mocks
=========================

Library to allow mocking of a Play-ReactiveMongo app's MongoDB back end.

The intention of this library is to facilitate the writing of tests which:
  - Cover DAO- or Service-level logic around backend database calls
  - Allow a level of component testing beyond simple unit tests
  - Execute far faster than conventional "seeded database" tests
  - Eliminate the potential issues around inconsistent and/or shared database state
  

## A Note About Versioning
Due to the nature of mocking method calls, it is essential that the version of this library matches *exactly* the version of [https://github.com/ReactiveMongo/Play-ReactiveMongo].

### Installation

Bring in the library by adding the following to your Play project's ```build.sbt```. 

  - The release repository: 

```
   resolvers ++= Seq(
     "Millhouse Bintray"  at "http://dl.bintray.com/themillhousegroup/maven"
   )
```
  - The dependency itself: 

```
   libraryDependencies ++= Seq(
     "com.themillhousegroup" %% "play2-reactivemongo-mocks" % "0.11.9_0.4.24"
   )

```

The above version (the **0.4.x** family) is for **Play-ReactiveMongo 0.11.9** and **Play 2.4**. As Play 2.4 is for Scala 2.11, this library is correspondingly now **only for Scala 2.11**.

### Older Versions
If you are using **Play 2.3**, you can still use the older published **0.3.x** versions of this library, which target **Play-ReactiveMongo 0.10.5.0.akka23**, in both Scala 2.10 and Scala 2.11 flavours. Substitute the following dependency:

```
	"com.themillhousegroup" %% "play2-reactivemongo-mocks" % "0.10.5.0.akka23_0.3.11"
```

## Usage
Two modes of usage exist - the first will be more familiar to those who have used mocking libraries such as [Mockito](https://code.google.com/p/mockito/) before. 
The second is a "higher-level" abstraction over the Mongo datastore, where the entire store (or at least, the part(s) being tested) are represented as a Map of Maps.
If you are executing complex multi-Collection join-like queries, it may end up being a more readable solution. 

#### Traditional Mock Definitions


##### The code to be tested
Let's say you have a simple `Controller` that uses ReactiveMongo just like in [their sample tutorial](http://reactivemongo.org/releases/0.11/documentation/tutorial/play2.html):

```
class PersonController @Inject() (val reactiveMongoApi: ReactiveMongoApi) 
	extends Controller with MongoController with ReactiveMongoComponents {

	def collection: JSONCollection = db.collection[JSONCollection]("persons")

	def findByName(name: String) = Action.async {
    val cursor: Cursor[JsObject] = collection.
      find(Json.obj("name" -> name)).
      sort(Json.obj("created" -> -1)).
      cursor[JsObject]

    val futurePersonsList: Future[List[JsObject]] = cursor.collect[List]()

    val futurePersonsJsonArray: Future[JsArray] =
      futurePersonsList.map { persons => Json.arr(persons) }

    futurePersonsJsonArray.map { persons =>
      Ok(persons)
    }
  }
}
```
Great. You can run this against a real Mongo instance and it works fine. Now let's add some test coverage.

##### Define a Spec
Firstly, just mix in `MongoMocks` into our standard Specs2 specification:
```
import org.specs2.mutable.Specification
import com.themillhousegroup.reactivemongo.mocks.MongoMocks

class PersonControllerSpec extends Specification with MongoMocks {

}

```

##### Defining global behaviour
If you just want your mock persistence layer to always reply with meaningful results, use the `mockedCollection(name)` method together with one or more of the `givenMongo...` methods, like this:

```
class PersonControllerSpec extends Specification with MongoMocks {
    val mockedPersons = mockedCollection("persons")(this.mockDB)
    val people = List(
      Json.obj("name" -> "Alice", "created" -> 123456789L),
      Json.obj("name" -> "Bob", "created" -> 333333389L)
    )
    givenMongoCollectionFindAnyReturns(mockedPersons, people)

		val testController = new PersonController
		
		"some test of person finding" in {
			
		} 
}
```  




#### The ```Map``` Abstraction
Mix in ```MappedMongoMocking``` and define ```mockData``` - a ```Map[String, Set[JsObject]``` i.e. mapping CollectionName to its contents. That's it! All of your Mongo ```find()``` operations will now work against this data. 

## Credits
Standing on the shoulders of giants:
  - [Play-ReactiveMongo](https://github.com/ReactiveMongo/Play-ReactiveMongo) 
  - [Mockito](https://code.google.com/p/mockito/) 
  - [Specs2](http://etorreborre.github.io/specs2/)


