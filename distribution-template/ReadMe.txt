ReadMe.txt
==========

Tayra is an incremental backup and restore utility for MongoDB.

You can view Tayra as an external and persistent oplog that is stored on the
file system instead of residing within MongoDB. The files generated can then be
used to restore the data incrementally to any target MongoDB instance, which can
be injected into a replica set in case of any event that threatens the
availability of service.

Initially, you can seed backup and subsequently back-up data in an incremental
fashion.

You can also specify the number of files and size of those files to ensure
rotating output files, giving you a window to perform restore once max number
on the rotating file is reached.

You can decide either to backup from primary or secondary of the replica set by
starting the process over that node. In the event of a node crash, Tayra will
automatically switch over to the next similar node (primary for primary and
secondary preferred for secondary) and will resume the backup from where it left
without user intervention.

Features Summary:
* Selective restore
* Rotating Logs in backup and restore
* Surviving node crash in a replica set
* Secured and unsecured backup/restore

Pre-Requisites
* It assumes that you have JDK1.6 or 1.7 installed and you have java
in your path.

Running Tayra
* After having exploded the Tayra zip -
  * For Unix machine, grant executable permissions to backup.sh and restore.sh
    scripts.
  * In order to start backing up a MongoDB (participating in a ReplicaSet), you
    may use the backup script.
  * In order to restore a backed up file to a MongoDB (can be in standalone
  mode), you may use the restore script.
* When using rotating logs feature, the log with maximum index will have
  entries farthest in history.

What may Future releases include?
* Allow more granular control over backup and restore
  (e.g multiple DBs, collections).
* Selective backup to take backup of only selected DBs or collections.
