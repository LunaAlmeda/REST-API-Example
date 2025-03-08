# Sample Rest API service

### Objectives
This is a service that aims to provide some clearly defined functionality and needs.
We can describe the following needs:

* H2 Database, that should implement the tables needed and also be prepopulated with some data
* Endpoints that with the provided data, are able to return the expected response
* Those task needs to be done while working with correct REST practices and following the correct architecture approach
* Exceptions should be handled by a 'ControllerAdvice'
* The code should have some specific test coverage
* Other test, and the use of tools like Jacoco, to monitor this coverage is welcomed

### Explanation
The exercise asked for correct REST implementation, even so, I decided to build a POST and GET endpoints that cover the
exact same use case. This is mainly because when we are working with endpoints that retrieve information we can find
situations were query parameters are not suitable for that specific request, due to the sensibility of those, or the length,
or many other factors. For that reason I am used in my day-to-day work to use POST endpoints with Json bodies. That is primarily
why I added that approach as well.

Jacoco was implemented, and you can find that it do not understand that the ResponseStatusException is not covered.
It is true that it is not covered on the Controller/Resource layer, but it is where it is triggered, in the service layer.

I decided to work directly work JPA, but I am also use to work with several libraries that make JPA work a bit easier, it
wasn't worth for this specific use case to implement those libraries because the query was not complex enough to need it.
In the same note, I worked with Lombok just to speed up some processes, the same can be achieved manually.

