# Sample Rest API service

### Objectives
This is a service that aims to provide some clearly defined functionality and needs.
We can describe the following needs:

* H2 Database, that should implement the tables needed and also be prepopulated with some data
* Endpoints that, with the provided data, can return the expected response
* Those task needs to be done while working with correct REST practices and following the correct architecture approach
* Exceptions should be handled by a 'ControllerAdvice'
* The code should have some specific test coverage
* Other tests, and the use of tools like Jacoco, to monitor this coverage is welcomed

### Explanation
The exercise asked for correct REST implementation, even so, I decided to build a POST and GET endpoints that cover the
same use case. This is mainly because when we are working with endpoints that retrieve information, we can find
situations where query parameters are not suitable for that specific request, due to the sensitivity of those, the length,
or many other factors. For that reason, I use POST endpoints with JSON bodies in my day-to-day work. That is primarily
why I added that approach as well.

Jacoco was implemented, and you can find that it does not understand that the ResponseStatusException is not covered.
It is true that it is not covered on the Controller/Resource layer, but it is where it is triggered, in the service layer.

I decided to work directly with JPA, but I am also used to work with several libraries that make JPA work a bit easier, it
wasn't worth it for this specific use case to implement those libraries because the query was not complex enough to need it.
On the same note, I worked with Lombok just to speed up some processes, which can be achieved manually.

