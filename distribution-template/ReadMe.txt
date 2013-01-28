ReadMe.txt
==========

Tayra is an incremental backup and restore utility for MongoDB.

You can view Tayra as an external and persistent oplog that is stored on the file system instead of
residing within MongoDB. The files generated can then be used to restore the data incrementally
to any target MongoDB instance which can be injected into a replica set in event of any event that
threatens the availability of service.

Initially, you can seed backup and subsequently back-up data in an incremental fashion. At the time of crash,
 the backup process will switch over to the newly elected master and will continue to save incremental backups.

You can also specify the number of files and size of those files to ensure rotating output files, giving
you a window to perform restore once max number on the rotating file is reached.

Features Summary:
* Selective restore
* Rotating Logs backup and restore
* Surviving master crash in a replica set
* Secured and unsecured backup/restore

Pre-Requisites
* It assumes that you have JDK1.6 or 1.7 installed and you have java in your path

Running Tayra
* After having exploded the Tayra zip -
  * In order to start backing up a MongoDB (participating in a ReplicaSet), you
    may use the backup command
  * In order to restore a backed up file to a MongoDB (can be in standalone mode), you
    may use the restore command
* When using rotating logs feature, the log with maximum index will have entries farthest in history.

Using Tayra:
    You could possibly be in one of the below scenarios. Choose the best applicable to you.

* Scenario 1: Taking backup from a "fresh" replica set and restoring to a fresh Mongo.
  As a mongoDB admin,
    I have a fresh replica set (primary and a few secondaries),
    and I wish to back it up so that I can restore it at a later stage.
  * Mechanics:
   Backup: Start backup utility on primary in tailable mode:
     backup -s localhost --port=27017 -f backup.log -t
     The tool will tail the primary oplog and backup documents as operations are performed on primary.
     Should backup be aborted (using 'Ctrl + C'), the timestamp of last backed-up document is recorded and
     when backup resumes, it records only new documents.
   Restore: Start restore utility on the target to replay the backup:
     restore -d localhost --port=27020 -f backup.log
     The tool will replay the documents from the backup file "backup.log" into the target mongo.

* Scenario 2: Taking backup from an "existing" replica set and restoring to a fresh Mongo.
  As a mongoDB admin,
    I have an existing replica set (primary and a few secondaries),
    and I wish to back it up so that I can restore it at a later stage.
    To achieve this, use 'mongodump' or snapshot the database soon after you start backup in tailable mode.
    While restoring, make sure to seed the target with "mongorestore" or from the snapshot,
    before starting the restore utility.
  * Mechanics:
   Backup: Start backup utility on primary in tailable mode:
     backup -s localhost --port=27017 -f backup.log -t
     The tool will catch up with the primary oplog and tail it to backup documents as operations are performed.
   Dump/Snapshot: Take "mongodump" or a snapshot of primary into the file system:
     mongodump --host localhost --port 27017 --out dump/mongodump-2013-01-24
   Seeding: Perform "mongorestore" or use snapshot on the target mongo to get it in consistent state:
     mongorestore --host localhost --port 27021 dump/mongodump-2013-01-24
   Restore: Start restore utility on the target to replay the backup:
     restore -d "localhost" --port=27021 -f backup.log
     The tool will replay the documents from the backup file "backup.log" into the target mongo.

     Note: While using the restore utility, some of the documents may not get replayed and
     may be moved to "exception.documents". This behavior might occur because Tayra prefers
     to rely on caution rather than cause the corruption of data. So, it might try to replay
     the operations on documents that might have been deleted or removed before taking mongodump.
     However, it will maintain the consistency of data.

* Scenario 3: Taking backup across multiple files and then performing selective restore.
  As a mongoDB admin,
    I wish to take backup across multiple files
    and restore only a selected database till a certain point in time
  * Mechanics:
   Backup: Start backup utility on primary in tailable mode:
     backup -s localhost --port=27017 -f backup.log -t --fMax=4 --fSize=1MB
     Unit of fSize can be KB or GB as well.
     The tool will refer to "timestamp.out" and resume backup from the last backed up oplog.
     It will backup a total of 4MB data into 4 files,
     backup.log.1, backup.log.2, backup.log.3, backup.log.4.
   Archive: As soon as backup.log.4 gets filled to its max capacity, move all 4 files to a separate directory.
   Restore: Start restore utility in a new cmd to replay from the backup across multiple files:
     restore -d localhost --port=27017 -f backup.log --fAll --sDb=testDB --sUntil=2012-12-26T15:15:00Z
     The tool will replay documents belonging to testDB database from the backup files,
     till the specified point in time.

  
What will Future releases include?
* Allow more granular control over restore (e.g a collection or few docs in a collections etc..)
* Selective backup that will give option to take backup of only selected DBs or collections
* Streaming data being backed up readily to restore.
