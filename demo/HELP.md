# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.1/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.1/maven-plugin/build-image.html)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

ideas:
add possibility to assign task to lesson with max date of uploadin solution - date can be configurable by teacher or admin any time
possibility to add required or optional solution to task for lesson
add teacher panel where there will be possibility to edit posts written by teacher or add new ones, add lessons to classes that teacher is assigned to and generally manage classes and lessons there
add grades for solutions that were uploaded by users
add teacher role - aside of same permits as users also access to teacher panel 
make navbar's options into dropdown list
add statistics to admin panel
add better text adding like possibility to style text for example marking bolding text or changing color or also code formatting
lets add notifications once user will be logged in bell icon with if there are any new notifications for user there will be red number displayed on it with how many unread have user
add email for registering
google account login
instead of login different page just make it modal pop up

styling touches

approvale dla teacherow i dodtakowo dodac do classes dla userow wiecej info typu czy sa obowiazkowe taski do kiedy mozna sie zapisywac na klases

