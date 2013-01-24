ReadMe.txt
==========
What problem does this tool solve?
   With MongoDB, you can have huge volume of data and using the mongodump and mongorestore will be quite time-consuming.  This is where Tayra can help, its a incremental backup tool that can help to restore data incrementally.

Features
* Selective restore
* Rotating Logs backup and restore
* Surviving replica set failover
* Secured and unsecured backup

Pre-Requisites
* It assumes that you have JDK1.6 or 1.7 installed and you have java in your path

Running Tayra
* After having exploded the Tayra zip -
  * In order to start backing up a MongoDB (participating in a ReplicaSet), you
    may use the backup command
  * In order to restore a backed up file to a MongoDB (can be in standalone mode), you
    may use the restore command

* Backup Options
  * Logs are created in the reverse order of Indexes. The log with maximum index will have the oldest entries ie will be farthest in history, and log with minimum index will have the latest entries.

Use Cases:
* Use Case 1: Taking backup from a "fresh" replica set and restoring to a fresh standAlone Mongo.
  * Description:
      As a mongoDB admin, I have a fresh replica set with a primary and a few secondaries,
    and I wish to take backup and restore to a new standAlone.
  * Steps:
   1. Start backup utility on primary in tailable mode: backup -s localhost --port=27017 -f backup.log -t
     Mechanics: The tool will tail the primary and keep on taking incremental backup as and when operations are performed on primary.
      If the backup is aborted using 'Ctrl + C', the timestamp of last backed-up document is saved and next time the backup resumes from that particular document onwards.
   2. Start restore on the target standAlone: restore -d localhost --port=27020 -f backup.log
     Mechanics: The tool will replay the documents from the backup file "backup.log" into the target mongo.

* Use Case 2: Taking backup from "existing" replica set and restoring to a fresh standAlone Mongo.
  * Description:
      As a mongoDB admin, I have an existing replica set with a primary and a few secondaries, and I wish to take backup and restore to a new Mongo.
    To achieve this, use 'mongodump' to preserve current database state. And then start 'backup' in tailable mode on existing replica set.
  * Steps:
   1. Start backup utility on primary in tailable mode: backup -s localhost --port=27017 -f backup.log -t
     Mechanics: The tool will backup from the start of oplog till the last oplog entry and tail the primary as and when operations are performed.
     Meaning, the tool will automatically catch up and start tailing the primary.
   --- Get the standAlone in consistent state before using the restore utility --- 
   2. Take "mongodump" of the primary into file system: mongodump --host localhost --port 27017 --out dump/mongodump-2013-01-24
   3. Perform "mongorestore" on the target mongo to get it in consistent state: mongorestore --host localhost --port 27021 dump/mongodump-2013-01-24
   --- Perform restore on consistent standAlone ---
   4. Start restore utility on target standAlone to replay the backup: restore -d "localhost" --port=27021 -f backup.log
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
     
* Use Case 4: Restoring the 
  
What will Future releases include?
* Allow more granular control over restore (e.g a collection or few docs in a collections etc..)
* Selective backup that will give option to take backup of only selected DBs or collections
* Streaming data being backed up readily to restore.
