# Prompt Payments Alpha

Published under OGL
 
This is based on [Play](http://www.playframework.com), a Scala+Java MVC web service architecture.

Code stolen shamelessly from [lite-play-common](https://github.com/BISDigital/lite-play-common) and modified to be a standalone module.

## How?!?!

You'll need to set the following environment variables for running the server or the tests:
- JDBC_DATABASE_URL, JDBC_DATABASE_USERNAME, JDBC_DATABASE_PASSWORD -- connection details to your postgres database
- COMPANIESHOUSE_API -- the API key for companies house
- COMPANIESHOUSE_CLIENTID and COMPANIESHOUSE_CLIENTSECRET -- the public and private identifiers for using Companies House as an OAuth 2.0 server
- GOVUKNOTIFY_API -- the API key for the GOV.UK notify service (used to send emails)

Your PC needs the following components

- Java Runtime Environment
- Java Development Kit
- SBT
- the Play Framework 

Setting this up in Windows turned out to be a right pain (the Play distribution is a bit broken) so make some time and read the hints below.

Views use scala.html framework - compilation of the rest of the module requires the views to be built first. So before editing/running this project in an IDE, you may need to run

> sbt compile

manually in the root folder of the project first. You may have to do this every time you add a new view too :/. Oh, you also have to make sure that the output folder for the code generated by the templates (/taret/scala-X-XX/twirl/main)is *marked as a source folder* in your project settings. 

Once the views are compiled, you can start the project. Create a build configuration that executes

> sbt run

In IntelliJ IDEA Community edition, you do this as follows:
1. Make sure the Scala Plugin is enabled (File -> Settings -> Plugins -> search "Scala" -> install if necessary)
2. Go to Run -> Edit Configurations... -> "Plus" button -> SBT Task. enter "run" into the Tasks field and apply 

## Making Play play nice with Windows

tl;dr, the *activator.bat* file is incompatible with Windows. You'll have to do the following:

1. Follow the [official instructions](https://playframework.com/documentation/2.5.x/Installing) to installing Play, making sure to use the *offline distribution*
2. Be sure that the installation is in a path without spaces, e.g. C:\activator, but not C:\Program Files (x86)\activator
3. Fix up some syntax errors in activator.bat and copy sbtconfig.txt, as described [here](http://stackoverflow.com/a/37153773).



