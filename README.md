# Tayra  <a href="http://www.eelabs.co.uk/projects/tayra/"><img src="http://www.equalexperts.com/resources/img/eelogo.png" align="right"></a>
***Incremental backup tool for MongoDB***
##Overview

 Tayra can be viewed as an external and persistent oplog that is stored on the 
 file system instead of residing within MongoDB. The files generated can then be
 used to restore the data incrementally to any target MongoDB instance, which can
 be injected into a replica set in case of any event that threatens the
 availability of service.<br>

* Features Summary
 * Selective Restore
 * Selective Backup
 * Rotating Logs in backup and restore
 * Surviving node crash in a replica set
 * Secured and unsecured backup/restore
 * Analyse documents to restore using Dry Run 
<br>

## Documentation
* You can view the Youtube video on Tayra [here](http://youtu.be/CoKkoxF984I)
* Please find the documentation [here](http://htmlpreview.github.com/?https://github.com/EqualExperts/Tayra/blob/master/acceptance-tests/index.html) for usage. Additionally, you can refer to the `ReadMe.txt` within binary distribution for features.
* [Approach, Design Overview] (http://www.slideshare.net/DhavalDalal/tayra-demo) 

## Binary Distributions
**[Download from here](http://www.eelabs.co.uk/projects/tayra/)**.  

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
Please refer to `AppConfig.groovy` - a single place of change for all the project get

## License
**Tayra is licensed under the terms of the [FreeBSD License](http://en.wikipedia.org/wiki/BSD_licenses)**

## Acknowledgment
This product is developed using **IntelliJ IDEA 12.1.4 Ultimate Edition** under free **open source license** granted by **JetBrains**
