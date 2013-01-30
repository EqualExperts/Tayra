# Tayra <a href="http://www.w3schools.com"><img src="http://www.equalexperts.com/asset/images/EE-Labs-Logo-200x121px.jpg" height="90" width="120" align="right"></a>

##Incremental backup tool for MongoDB
* Use Cases
 * [Re-Syncing](http://www.kchodorow.com/blog/2010/10/14/getting-to-know-your-oplog/) 
 * [Rollback](http://www.mongodb.org/display/DOCS/Replica+Sets+-+Rollbacks)<br>
<br>

* Overview<br>

 Tayra can be viewed as an external and persistent oplog that is stored on the 
 file system instead of residing within MongoDB. The files generated can then be
 used to restore the data incrementally to any target MongoDB instance, which can
 be injected into a replica set in case of any event that threatens the
 availability of service.<br>

* Features Summary
 * Selective restore
 * Rotating Logs in backup and restore
 * Surviving node crash in a replica set
 * Secured and unsecured backup/restore
<br>

## Build Info
We are using Gradle 1.2 for our builds.  You can download it [here](http://services.gradle.org/distributions/gradle-1.2-bin.zip)
Please do not checkin Eclipse or Intellij or any IDE specific files.  For Idea or Eclipse they
can be generated using
* `gradle eclipse`
* `gradle idea`

## Project Versioning
We will be following [JBoss Versioning Convention](https://community.jboss.org/wiki/JBossProjectVersioning?_sscc=t)
* `major.minor.micro.Alpha[n]`
* `major.minor.micro.Beta[n]`
* `major.minor.micro.CR[n]`
Please refer to `AppConfig.groovy` - a single place of change for all the project settings
