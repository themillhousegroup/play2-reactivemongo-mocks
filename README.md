play2-reactivemongo-mocks
=========================

Library to allow mocking of a Play-ReactiveMongo app's MongoDB back end.

The intention of this library is to facilitate the writing of tests which:
  - Cover DAO- or Service-level logic around backend database calls
  - Allow a level of component testing beyond simple unit tests
  - Execute far faster than conventional "seeded database" tests
  - Eliminate the potential issues around inconsistent and/or shared database state
  
  
This library is currently a work-in-progress and should not be considered useful until an artifact has been publicly published.

Due to the nature of mocking method calls, it is essential that the version of this library matches *exactly* the version of [https://github.com/ReactiveMongo/Play-ReactiveMongo].

