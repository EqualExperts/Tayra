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

* Use Cases:
  Use Case 1: Taking backup from fresh replica set and restoring to a fresh standAlone Mongo.
  Description:
    As a mongoDB admin, I have a fresh replica set with a primary and a few secondaries,
  and I wish to take backup and restore to a new standAlone.	

  Steps:
  1. Start backup in tailable mode and point to the primary:	backup -s "localhost" --port=27017 -f backup.log -t
  Mechanics: The tool will tail the primary and keep on taking incremental backup as and when operations are performed on primary.
    If the backup is aborted using ctrl + C, the timestamp of last backed-up document is saved and next time the backup resumes from that particular document.
  2. Start restore and point to the target standAlone: restore -d "localhost" --port=27020 -f backup.log
  Mechanics: The tool will replay the documents from the backup file "backup.log" into the target mongo.

  Use Case 2: Taking backup from existing replica set and restoring to a fresh standAlone Mongo.
  Description:
    As a mongoDB admin, I have an existing replica set with a primary and a few secondaries, and I wish to take backup and restore to a new Mongo.
  To achieve this, start backup in tailable mode on existing replica set. Use mongodump to get

  Steps:
  1. Start backup in tailable mode and point to existing primary: backup -s "localhost" --port=27017 -f backup.log -t
  Mechanics: The tool will backup from the start of oplog till the last oplog entry and tail the primary as and when operations are performed.
    Meaning, the tool will automatically catch up and start tailing the primary.
  2. Start restore and point to the target standAlone: restore -d "localhost" --port=27020 -f backup.log
  Mechanics: The tool will replay the documents from the backup file "backup.log" into the target mongo.

  Use Case 3: Seeding the backup first time from mongo dump, and then tailing the replica set.


What will Future releases include?
* Allow more granular control over restore (e.g a collection or few docs in a collections etc..)
* Selective backup that will give option to take backup of only selected DBs or collections
* Streaming data being backed up readily to restore.

