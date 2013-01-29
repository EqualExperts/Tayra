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

What will Future releases include?
* Allow more granular control over restore (e.g multiple DBs, collections).
* Selective backup to take backup of only selected DBs or collections.
* Streaming data being backed up directly to restore.
