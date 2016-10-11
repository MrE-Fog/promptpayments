# Prompt Payments Working practices
 
The following measures and practices have been put in place to ensure the highest standard of service delivery:

### 5. Build a service that can be iterated and improved on a frequent basis and make sure that you have the capacity, resources and technical flexibility to do so.

Measures:

* SOLID class design
* Stateless web service design to allow for load balancing
* Use of widely-known standard technologies (see below)
* 100% coverage by automated tests
* VisualTest page to aid exploratory UI testing

Automatic tests and type-safety are employed to facilitate the following anticipated extension points:

* Adding, removing or updating report fields

Operations ownership:

* **TODO** Confirm that BEIS LITE team will handle the operations and scaling of the live deployment.

### 6.  Evaluate what tools and systems will be used to build, host, operate and measure the service, and how to procure them.

The following standard technologies will be used:
* Java, tested with JUnit; 
* Typesafe Play MVC framework (including Twirl templating engine); 
* PostgreSQL, using Flyway database migrations

They each fulfil the following requirements:
* widespread industry standard with widely available expertise
* precedence of use in the department (except Flyway)
* open source (except Java)
* no known relevant limitations

Hosting and operational measurement will be done through AWS IaaS, in order to be conveniently operated alongside Java. Due to the statelessness and simplicity of the service, we assume that basic disaster recovery measures (restarting of web service, restoring of database) will be all that is required for Operation.
 
There will be no standing development team indefintely dedicated to the service after release. Occasional updates and code changes will be handled by a general BEIS standing development team, when required, in a timely fashion. Due to the simplicity of the service, we believe that a shared standing team will be sufficient; Their efficiency will be supported by good test coverage

**TODO** liaise with LITE team - pick up converstaion about hosting & confirm that their ops strategy can absorb our requirements

### 7. Evaluate what user data and information the digital service will be providing or storing and address the security level, legal responsibilities, privacy issues and risks associated with the service (consulting with experts where appropriate).

Only public information will be stored. Authentication is proposed to go via Companies House oAuth, therefore no credentials need to be stored.

Users will authenticate before they may submit (public) data. The thread model of man-in-the-middle attacks and impersonator attacks amounts to injecting false data.

* **TODO** liaise with information as

### 8. Make all new source code open and reusable, and publish it under appropriate licences (or provide a convincing explanation as to why this can't be done for specific subsets of the source code).

Source code is modularised under SOLID principles. Our working agreements include that if a distinct reusable module of functionality emerges, we publish it as a separate repository.

Code is published under OGL in the BisDigital Github organizatoin. This is currently private.

* **TODO** Confirm that BisDigital will be swtiched to public at some point, OR switch repo to public

### 9. Use open standards and common government platforms where available.

For open standards used, see point 6.

We use the standard GOV.UK templates, elements and design principles.

We are proposing to use Companies House oAuth and API.

### 10. Be able to test the end-to-end service in an environment identical to that of the live version, including on all common browsers and devices, and using dummy accounts and a representative sample of users.

* **TODO** get a CI and Test environment
* **TODO** implement load tests

### 15. Use tools for analysis that collect performance data. Use this data to analyse the success of the service and to translate this into features and tasks for the next phase of development.

Given that the level of detail and number of actions on individual service pages is small, we propose that Google Analytics will be sufficient for the foreseeable future.

A full-stack APM such as NewRelic may be appropriate in the future

* **TODO** implement google analytics, including workflow completion and dashboards
* **TODO** does google measure exceptions? Can it measure validation fails?

### 16. Identify performance indicators for the service, including the 4 mandatory key performance indicators (KPIs) defined in the manual. Establish a benchmark for each metric and make a plan to enable improvements.

We propose the following indicators:
* Cost per transaction
* User satisfaction
* Digital take-up
* Completion rate
* Stumble rate
* uptime

We will determine these numbers by measuring directly
* Satisfaction at the end of filing a report
* A feedback link at the report page
* number of times the search start-button is clicked
* number of times the file-start button is clicked
* number of times a report is filed
* number of times a report is viewed
* (manual) number of non-digital report filings
* (manual) maintenance cap ex + pro-rata standing development team cost + a percentage of ops team expense
* number of times a form validation fails
* number of times a user interrupts their workflow to "go back"
* technical failure rate (exceptions thrown + service bounces)
* uptime

* **TODO**: non-digital fallback story
* **TODO**: feedback form
