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
    
* Scenario 1: Taking backup from a "fresh" replica set and restoring to a fresh standAlone Mongo.
  As a mongoDB admin,
    I have a fresh replica set (primary and a few secondaries),
    and I wish to back it up so that I can restore it at a later stage.
  * Mechanics:
   Backup: Start backup utility on primary in tailable mode: backup -s localhost --port=27017 -f backup.log -t
     The tool will tail the primary oplog and backup documents as operations are performed on primary.
     Should backup be aborted (using 'Ctrl + C'), the timestamp of last backed-up document is recorded and
     when backup resumes, it records only new documents.
   Restore: Start restore on the target: restore -d localhost --port=27020 -f backup.log
     The tool will replay the documents from the backup file "backup.log" into the target mongo.

* Scenario 2: Taking backup from "existing" replica set and restoring to a fresh standAlone Mongo.
  As a mongoDB admin,
    I have an existing replica set (primary and a few secondaries),
    and I wish to back it up so that I can restore it at a later stage.
    To achieve this, use 'mongodump' to preserve current database state. And then start 'backup' in tailable mode on existing replica set.
  * Mechanics:
   Backup: Start backup utility on primary in tailable mode: backup -s localhost --port=27017 -f backup.log -t
     The tool will backup from the start of oplog till the last oplog entry and tail the primary as and when operations are performed.
     Meaning, the tool will automatically catch up and start tailing the primary.
   --- Get the standAlone in consistent state before using the restore utility --- 
   Seed: Take "mongodump" of the primary into file system: mongodump --host localhost --port 27017 --out dump/mongodump-2013-01-24
     Perform "mongorestore" on the target mongo to get it in consistent state: mongorestore --host localhost --port 27021 dump/mongodump-2013-01-24
   --- Perform restore on consistent standAlone ---
   Restore: Start restore utility on target standAlone to replay the backup: restore -d "localhost" --port=27021 -f backup.log
     Mechanics: The tool will replay the documents from the backup file "backup.log" into the target mongo.

     Note: While using the restore utility, some of the documents may not get replayed and may be moved to "exception.documents".
     This behavior might occur because Tayra prefers to rely on caution rather than cause corruption of data.
     So, it might try to replay the operations on documents that might have been deleted or removed before taking mongodump.

* Use Case 3: Incremental Backup
  * Description:
      As a mongoDB admin, I had used backup utility earlier and stopped the backup.
    I wish to restart the backup from where I had left last time.
  * Steps:
   1. Start backup utility on primary in tailable mode: backup -s localhost --port=27017 -f backup.log -t
     Mechanics: The tool will refer to "timestamp.out" and resume backup from the last backed up oplog.
     
     Note: Here the user must ensure that the backup is restarted before the oplog tails off.

* Use Case 4: Restoring a DB to a point earlier in time.
  * Description:
      As a mongoDB admin, I had taken backup of existing replica set using tayra into backup.log
      I wish to restore a particular DB to an earlier point in time.
  * Steps:
   1. Start restore utility in tailable mode: restore -d localhost --port=27017 -f backup.log --sDb=testDB --sUntil={$ts:1234567890, $inc:1}
     Mechanics: The tool will replay documents from the backup till the specified point in time.

  
What will Future releases include?
* Allow more granular control over restore (e.g a collection or few docs in a collections etc..)
* Selective backup that will give option to take backup of only selected DBs or collections
* Streaming data being backed up readily to restore.
