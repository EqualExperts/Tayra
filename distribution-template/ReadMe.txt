ReadMe.txt
==========
What problem does this tool solve?
   With MongoDB, you can have huge volume of data and using the mongodump and mongorestore will be quite time-consuming.  This is where MongoBeaver can help, its a incremental backup tool that can help to restore data incrementally.

Pre-Requisites
* It assumes that you have JDK1.6 or 1.7 installed and you have java in your path

Running MongoBeaver
* After having exploded the MongoBeaver zip -
  * In order to start backing up a MongoDB (participating in a ReplicaSet), you 
    may use the backup command
  * In order to restore a backed up file to a MongoDB (can be in standalone mode), you
    may use the restore command

What will Future releases include? 
* Point-in-time restore
* Allow more granular control over restore (e.g a collection or few docs in a collections etc..)

