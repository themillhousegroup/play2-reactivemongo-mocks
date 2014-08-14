play2-reactivemongo-mocks
=========================

Library to allow mocking of a Play-ReactiveMongo app's MongoDB back end.

The intention of this library is to facilitate the writing of tests which:
  - Cover DAO- or Service-level logic around backend database calls
  - Allow a level of component testing beyond simple unit tests
  - Execute far faster than conventional "seeded database" tests
  - Eliminate the potential issues around inconsistent and/or shared database state
  
  
This library is currently a work-in-progress and should not be considered useful until an artifact has been publicly published.

## A Note About Versioning
Due to the nature of mocking method calls, it is essential that the version of this library matches *exactly* the version of [https://github.com/ReactiveMongo/Play-ReactiveMongo].

## Usage
Two modes of usage exist - the first will be more familiar to those who have used mocking libraries such as [Mockito](https://code.google.com/p/mockito/) before. 
The second is a "higher-level" abstraction over the Mongo datastore, where the entire store (or at least, the part(s) being tested) are represented as a Map of Maps.
If you are executing complex multi-Collection join-like queries, it may end up being a more readable solution. 

#### Traditional Mock Definitions

#### The ```Map``` Abstraction

## Credits
Standing on the shoulders of giants:
  - [Play-ReactiveMongo](https://github.com/ReactiveMongo/Play-ReactiveMongo) 
  - [Mockito](https://code.google.com/p/mockito/) 
  - [Specs2](http://etorreborre.github.io/specs2/)


