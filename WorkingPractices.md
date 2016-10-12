# Small Business Commissioner working practices

The following measures and practices have been put in place to ensure the highest standard of service delivery:

### 5. Build a service that can be iterated and improved on a frequent basis and make sure that you have the capacity, resources and technical flexibility to do so.

The owners of this service will be outside the government, as the Small Business Commissioner is a separate legal entity. 

Operations up to and including Beta are owned by the delivery team; this will then be handed over to the office of the Small Business Commissioner, when this office has been formed.

We are committed to continuous user-test-driven improvement during the Alpha and Beta phases, and are taking the following measures to ensure an agile and maintainable code base:

* SOLID class design
* Stateless web service design to allow for load balancing
* Widely-known standard technologies
* 100% coverage by automated tests
* Guardian tests that remind developers of updating all relevant code when adding, removing or updating model fields
* VisualTest page to aid exploratory UI testing

### 6.  Evaluate what tools and systems will be used to build, host, operate and measure the service, and how to procure them.

The service will be delivered in three components: A CMS and frontpage for static informational content; a triaging workflow assisting users to determine the best course of action in the event of a dispute and (if appropriate) to file a complaint with the small business commissioner; and a back-office case management system to be used by the small business commissioner.
 
We are planning to use Whitehall as the CMS.

The triaging workflow will be a micro-service on GOV.UK implemented using the following technologies:
* Java, tested with JUnit; 
* Typesafe Play MVC framework (including Twirl templating engine and SASS); 
* PostgreSQL, using Flyway database migrations
* git and GitHub for version control

They each fulfil the following requirements:
* widespread industry standard with widely available expertise
* precedence of use in the department (except Flyway)
* open source (except Java)
* no known relevant limitations

The case-management back-office solution is currently being investigated. Due to the sensitive nature of the data held, and due to the fact that the Small Business Commissioner will likely be based on government premises and can therefore not self-host, it will be hosted in Government-grade (but not Government-controlled) data centers.

**TODO** find service or software on G-Cloud, 

### 7. Evaluate what user data and information the digital service will be providing or storing and address the security level, legal responsibilities, privacy issues and risks associated with the service (consulting with experts where appropriate).

The information held will be extremely sensitive for the following reasons:
* It will be narrative, relating to payment disputes between a small and large business
* It will be submitted under confidentiality
* It must be accessed by a non-Governmental entity, but be inaccessible by anyone else, including the Government
* It is personally-identifiable
* It is commercially sensitive
* It is financially explicit
* It will be largely free-form, i.e. the exact information stored is not pre-determined and will vary from case to case

Moreover, the owner of the data, the Small Business Commissioner, is not in office yet. 

Based on informal discussions with data security experts at BEIS, we require a data security expert to choose appropriate data processing and storage technologies on behalf of the future small business commissioner

**TODO** appoint a data security expert to choose appropriate data processing and storage technology to the project 
**TODO** clarify legal responsibilities of BEIS vs SBC regarding vs our security expert the security of case data

### 8. Make all new source code open and reusable, and publish it under appropriate licences (or provide a convincing explanation as to why this can't be done for specific subsets of the source code).

Whitehall is open source and published under OGL.

Source code for the triaging workflow is modularised under SOLID principles. Our working agreements include that if a distinct reusable module of functionality emerges, we publish it as a separate repository.

Code is published under OGL in the BisDigital Github organization. The repository will be made public once we enter alpha.

### 9. Use open standards and common government platforms where available.

Whitehall is open source and published under OGL.

The triaging workflow will be a micro-service on GOV.UK implemented using the following technologies:
* Java, tested with JUnit; 
* Typesafe Play MVC framework (including Twirl templating engine and SASS); 
* PostgreSQL, using Flyway database migrations
* git and GitHub for version control

We anticipate that the case-management backoffice solution will be a closed proprietary solution, as security requirements will likely limit the suitability of open standards and platforms. 

### 10. Be able to test the end-to-end service in an environment identical to that of the live version, including on all common browsers and devices, and using dummy accounts and a representative sample of users.

We will employ continuous integration and representative load testing procedures during alpha. In addition, manual end-to-end testing will be performed by the delivery team throughout.

**TODO** get a CI and Test environment
**TODO** implement load tests
**TODO** schedule bug hunts

### 11. Make a plan for the event of the digital service being taken offline temporarily

For the CMS component, this is covered by Whitehall's procedures.

For the triaging component, we will implement a static page to be served in the event of the service being offline

For the case-managemtn backoffice solution, our choice will ensure this is covered by the solution itself.

### 15. Use tools for analysis that collect performance data. Use this data to analyse the success of the service and to translate this into features and tasks for the next phase of development.

Given that the level of detail and number of actions on individual service pages is small, we propose that Google Analytics +Uptimerobot will be sufficient for the foreseeable future.

### 16. Identify performance indicators for the service, including the 4 mandatory key performance indicators (KPIs) defined in the manual. Establish a benchmark for each metric and make a plan to enable improvements.

We propose the following indicators:
* Cost per transaction
* User satisfaction
* Digital take-up
* Completion rate
* Stumble rate
* uptime

We will determine these numbers by measuring directly:
* (Whitehall) number of unique users viewing an article
* (guugle analytics) number of unique users viewing a triaging answer page
* (form) A feedback link in the triaging process
* (google analytics) number of times the triaging start-button is clicked
* (google analytics) number of users submitting an SBC complaint
* (manual) maintenance cap ex + pro-rata Whitehall cost + SBC office cost
* (manual) number of times complaint is filed non-digitally
* (google analytics) number of times a user goes through the triaging process multiple times
* (google analytics) number of times a user interrupts the triage
* (google analytics) technical failure rate of the triaging process (exceptions thrown + service bounces)
* (uptimerobot) uptime of triaging component

**TODO**: Google analytics
**TODO**: Uptimerobot
**TODO**: Non-digital fallback
**TODO**: feedback form

### 17. Report performance data on performance platform

As the Small Business Commissioner is a separate legal entity, we seek exemption from reporting on the performance platform
**TODO** Seek exemption 


# Prompt Payments Working practices
 
The following measures and practices have been put in place to ensure the highest standard of service delivery:

### 5. Build a service that can be iterated and improved on a frequent basis and make sure that you have the capacity, resources and technical flexibility to do so.

We are committed to continuous user-test-driven improvement during the Alpha and Beta phases. Due to the simplicity of the service, we are not planning to have a dedicated team for continous improvement during live; however, crucial defects and snags can be addressed by the proposed BEIS standing team.

Operations of the Beta and Live services will be handled by the BEIS LITE team.

We are taking the following measures to ensure an agile and maintainable code base:

* SOLID class design
* Stateless web service design to allow for load balancing
* Widely-known standard technologies
* 100% coverage by automated tests
* Guardian tests that remind developers of updating all relevant code when adding, removing or updating model fields
* VisualTest page to aid exploratory UI testing

**TODO** Confirm that BEIS LITE team will handle the operations and scaling of the live deployment.

### 6.  Evaluate what tools and systems will be used to build, host, operate and measure the service, and how to procure them.

The following standard technologies will be used:
* Java, tested with JUnit; 
* Typesafe Play MVC framework (including Twirl templating engine and SASS); 
* PostgreSQL, using Flyway database migrations
* git and GitHub for version control

They each fulfil the following requirements:
* widespread industry standard with widely available expertise
* precedence of use in the department (except Flyway)
* open source (except Java)
* no known relevant limitations

Hosting and operational measurement will be done through AWS IaaS, in order to be conveniently operated alongside Java. Due to the statelessness and simplicity of the service, we assume that basic disaster recovery measures (restarting of web service, restoring of database) will be all that is required of the ops team.
 
There will be no development team dedicated full-time to the service once Live. We plan for occasional updates and code changes to be handled by a general BEIS standing development team, when required. Due to the simplicity of the service, we believe that a shared standing team will be sufficient; Their efficiency will be supported by good test coverage.

**TODO** liaise with LITE team - pick up converstaion about hosting & confirm that their ops strategy can absorb our requirements

### 7. Evaluate what user data and information the digital service will be providing or storing and address the security level, legal responsibilities, privacy issues and risks associated with the service (consulting with experts where appropriate).

Only public information will be stored. Authentication is proposed to go via Companies House oAuth, therefore no credentials or other personally identifiable information need to be stored.

Users will authenticate before they may submit (public) data. The thread model of man-in-the-middle attacks and impersonator attacks is limited to injecting false data.

Our security strategy comprises of preventing direct access to the database and using SSL.

**TODO** liaise with information asset owner about the legal responsibility regarding the availability and accuracy of information

### 8. Make all new source code open and reusable, and publish it under appropriate licences (or provide a convincing explanation as to why this can't be done for specific subsets of the source code).

Source code is modularised under SOLID principles. Our working agreements include that if a distinct reusable module of functionality emerges, we publish it as a separate repository.

Code is published under OGL in the BisDigital Github organization. The repository will be made public once we enter alpha.

**TODO** switch repo to public once we enter alpha

### 9. Use open standards and common government platforms where available.

The following standard technologies will be used:
* Java, tested with JUnit; 
* Typesafe Play MVC framework (including Twirl templating engine and SASS); 
* PostgreSQL, using Flyway database migrations
* git and GitHub for version control

We use the standard GOV.UK templates, elements and design principles.

We are proposing to use Companies House oAuth and API.

No closed-source proprietary solutions are employed.

### 10. Be able to test the end-to-end service in an environment identical to that of the live version, including on all common browsers and devices, and using dummy accounts and a representative sample of users.

We will employ continuous integration and representative load testing procedures during alpha. In addition, manual end-to-end testing will be performed by the delivery team throughout.

**TODO** get a CI and Test environment
**TODO** implement load tests
**TODO** schedule bug hunts

### 11. Make a plan for the event of the digital service being taken offline temporarily

We will make available a non-digital fallback solution for filing payment reports. We will create a static web page to be served in the case of the service being offline

**TODO** offline page to be served

### 15. Use tools for analysis that collect performance data. Use this data to analyse the success of the service and to translate this into features and tasks for the next phase of development.

Given that the level of detail and number of actions on individual service pages is small, we propose that Google Analytics will be sufficient for the foreseeable future.

A full-stack APM such as NewRelic may be appropriate in the future.

**TODO** implement google analytics, including workflow completion, failures and dashboards
**TODO** does google measure exceptions? Can it measure validation fails?

### 16. Identify performance indicators for the service, including the 4 mandatory key performance indicators (KPIs) defined in the manual. Establish a benchmark for each metric and make a plan to enable improvements.

We propose the following indicators:
* Cost per transaction
* User satisfaction
* Digital take-up
* Completion rate
* Stumble rate
* uptime

We will determine these numbers by measuring directly:
* (form) Satisfaction at the end of filing a report
* (form) A feedback link at the report page
* (google analytics) number of times the search start-button is clicked
* (google analytics) number of times the file-start button is clicked
* (google analytics) number of times a report is viewed
* (manual) number of non-digital report filings
* (manual) maintenance cap ex + pro-rata standing development team cost + a percentage of ops team expense
* (google analytics) number of times a form validation fails
* (google analytics) number of times a user interrupts their workflow to "go back"
* (google analytics) technical failure rate (exceptions thrown + service bounces)
* (uptimerobot) uptime

**TODO**: Uptime robot
**TODO**: non-digital fallback story
**TODO**: feedback form

### 17. Report performance data on performance platform

We are committed to publishing to the performance platform.

* **TODO** push data to the performance platform