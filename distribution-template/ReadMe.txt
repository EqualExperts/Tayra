ReadMe.txt
==========

Tayra is an incremental backup and restore utility for MongoDB.

You can view Tayra as an external and persistent oplog that is stored on the 
file system instead of residing within MongoDB. The files generated can then be
used to restore the data incrementally to any target MongoDB instance, which can
be injected into a replica set in case of any event that threatens the
availability of service.

Initially, you can seed backup and subsequently back-up data in an incremental
fashion. At the time of crash, the backup process will switch over to the newly
elected master and will continue to save incremental backups.

You can also specify the number of files and size of those files to ensure
rotating output files, giving you a window to perform restore once max number
on the rotating file is reached.

Features Summary:
* Selective restore
* Rotating Logs in backup and restore
* Surviving master crash in a replica set
* Secured and unsecured backup/restore

Pre-Requisites
* It assumes that you have JDK1.6 or 1.7 installed and you have java
in your path.

Running Tayra
* After having exploded the Tayra zip -
  * In order to start backing up a MongoDB (participating in a ReplicaSet), you
    may use the backup command.
  * In order to restore a backed up file to a MongoDB (can be in standalone
  mode), you may use the restore command.
* When using rotating logs feature, the log with maximum index will have
  entries farthest in history.

Using Tayra:
  You could possibly be in one of the below scenarios. Choose the best
  applicable to you.

* Scenario 1: Taking backup from a "fresh" replica set (containing no data) and
restoring to a fresh Mongo.
  As a mongoDB admin,
    I want to back up a fresh replica set,
  So that, I can restore it at a later stage.
  * Mechanics:
  On Source MongoDB:
  Backup: Start backup utility on primary in tailable mode using:
    backup -s localhost --port=27017 -f backup.log -t
    The tool tails the primary oplog and backs up documents as operations are
    performed on primary.
    Should backup be aborted (using 'Ctrl + C'), the timestamp of last
    backed-up document is recorded and when backup resumes, it starts recording
    new documents.
  On Target MongoDB:
  Restore: Start restore utility on the target to replay the backup using:
    restore -d localhost --port=27020 -f backup.log
    The tool will replay the documents from the backup file "backup.log" into
    the target mongo.

  Caveat: In case the backup is aborted, in order to prevent data loss due to
  oplog falling off make sure that it is restarted in time.
  
  For sustained backups and restore, you may want to look at scenario 3.

* Scenario 2: Taking backup from an "existing" replica set (having some data)
and restoring to a fresh Mongo.
  As a mongoDB admin,
    I want to back up existing replica set having data
  So that, I can restore it at a later stage.
  For seeding purpose, snapshot the database using fsyncLock/fsyncUnlock.
  * Mechanics:
  On Source MongoDB:
  Taking Snapshot: Take a snapshot of primary into the file system using:
    db.fysncLock() on the source mongo, archive the dbpath files and
    db.fsyncUnlock() the source.
  Backup: Start backup utility on primary in tailable mode using:
    backup -s localhost --port=27017 -f backup.log -t
    The tool will catch up with the primary oplog and tail it to backup
    documents as operations are performed.
  On Target MongoDB:
  Seeding: Use snapshot on the target mongo to get it in consistent state.
    Start the target mongod with archived files in its dbpath using:
    mongod --port 27021 --dbpath "location of archive/snapshot"
  Restore: Start restore utility on the target to replay the backup using:
    restore -d "localhost" --port=27021 -f backup.log
    The tool will replay the documents from the backup file "backup.log" into
    the target mongo.

  Note: Alternatively, seeding can also be done by using 'mongodump' and
  'mongorestore' utility.

  For sustained backups and restore, you may want to look at scenario 3.

* Scenario 3: Sustaining periodic backups and restore.
  As a mongoDB admin,
    I want to streamline backup and restore process
  So that, it takes less effort to monitor the mongoDB.
  * Mechanics:
  On Source MongoDB:
  Backup: Start backup utility on primary in tailable mode using the command:
    backup -s localhost --port=27017 -f backup.log -t --fMax=4 --fSize=1MB
    The tool will refer to "timestamp.out" and resume backup from the last
    backed up document in oplog. It will backup a total of 4MB data into
    4 files in the above case.
  Archive: The files generated are- 'backup.log', 'backup.log.1',
    'backup.log.2', 'backup.log.3' and 'backup.log.4'. As soon as backup.log.4
    is created, move all 4 files (except backup.log - which is the running
    backup file) to a separate directory for consumption by restore utility.
  On Target MongoDB:
  Restore: Start restore utility to replay from the backup across multiple 
    files:
    restore -d localhost --port=27017 -f backup.log --fAll
    The tool will replay all the documents.
    
  Should you wish to perform selective restore, use the --sDb and/or --sUntil
  options.
  Usage details can be obtained by running the restore command with no options.


What will Future releases include?
* Allow more granular control over restore (e.g multiple DBs, collections).
* Selective backup to take backup of only selected DBs or collections.
* Streaming data being backed up directly to restore.
