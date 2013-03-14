# Tayra  <a href="http://www.equalexperts.com/eelabs/projects/tayra/"><img src="http://www.equalexperts.com/asset/images/EE-Labs-Logo-200x121px.jpg" height="90" width="120" align="right"></a>
***Incremental backup tool for MongoDB***
##Overview

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

## Binary Distributions
**[Download from here](http://www.equalexperts.com/eelabs/projects/tayra)**.  Please refer to the `ReadMe.txt` within it for Tayra's features and `Tayra Usage.rtf` for usage

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

## License
**Tayra is licensed under the terms of the [FreeBSD License](http://en.wikipedia.org/wiki/BSD_licenses)**


