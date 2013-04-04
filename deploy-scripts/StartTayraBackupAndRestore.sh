#!/bin/sh 

HOME=/home/lion/tayra
GROOVY_HOME=/home/lion/Documents/bin/groovy-2.0.6

backupFiles=$HOME/data_files/backup
notRestoredFiles=$HOME/data_files/notRestored
restoredFiles=$HOME/data_files/restored
toBeRestoredFiles=$HOME/data_files/toBeRestored
deployFiles=$HOME/deployed/current

backupFile=test.out
target=localhost
target_port=22320
source=localhost
source_port=22317
username=admin  
password=admin

echo "Cleaning existing directories"
rm -rf  $backupFiles/ $notRestoredFiles/ $restoredFiles/ $toBeRestoredFiles/

echo "Making new directories"
mkdir $backupFiles/ $notRestoredFiles/ $restoredFiles/  $toBeRestoredFiles/

gnome-terminal -t "Generator" -x $GROOVY_HOME/bin/groovy -cp ./libs/*: ./DataGenerator.groovy --source=$source --port=$source_port -u $username -p $password --feedInterval=5 &

echo "DataGenerator Started"
gnome-terminal -t "Watcher" -x $GROOVY_HOME/bin/groovy -cp ./libs/*: ./FileWatcher.groovy -f $backupFiles/$backupFile --target=$target  --port=$target_port -u $username -p $password &

echo "FileWatcher Started"
gnome-terminal -t "Backup" -x ./backup.sh -s $source --port=$source_port -f $backupFiles/$backupFile -u $username -p $password -t --fSize=500KB --fMax=1 &
