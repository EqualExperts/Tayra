# MongoBeaver


##Incremental backup tool for MongoDB
* Use Cases
 * [Re-syncing](http://www.kchodorow.com/blog/2010/10/14/getting-to-know-your-oplog/) 

## Build Info
we are using Gradle 1.2 for our builds.  You can download it [here](http://services.gradle.org/distributions/gradle-1.2-bin.zip)
Please do not checkin Eclipse or Intellij or any IDE specific files.  For Idea or Eclipse they
can be generated using
* `gradle eclipse`
* `gradle idea`

## Project Versioning
We will be following [JBoss Versioning Convention](https://community.jboss.org/wiki/JBossProjectVersioning?_sscc=t)
* major.minor.micro.Alpha[n]
* major.minor.micro.Beta[n]
* major.minor.micro.CR[n]
Please refer to `AppConfig.groovy` - a single place of change for all the project settings