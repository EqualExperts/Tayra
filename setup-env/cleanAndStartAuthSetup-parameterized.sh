#!/bin/sh
#sudo service mongodb stop
#if [ -z "$MONGO_HOME" ]; then
 
  MONGO_HOME=$1
  PORT_PREFIX=$2
#else
#  MONGO_HOME='$MONGO_HOME'
#fi
mongoData=/var/tmp/mongo-data
mongoBase=$mongoData/$PORT_PREFIX
basePath=$mongoBase/replSetData
basePathSt=$mongoBase/standaloneData
rm -rf $mongoBase/ 
mkdir $mongoData/ $mongoBase/ $basePath/ $basePathSt/
mkdir $basePath/db $basePath/db2 $basePath/db3 $basePathSt/20 $basePathSt/21 $basePath/unsec1 $basePath/unsec2
echo Zom89ZAH > $basePath/keyfile
chmod 700 $basePath/keyfile

#
# Secure Replica Set
################################################################
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""17 --dbpath $basePath/db --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &

$MONGO_HOME/bin/mongod --port $PORT_PREFIX""18 --dbpath $basePath/db2 --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""19 --dbpath $basePath/db3 --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &

#
# Secure Target 
################################################################3
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""20 --dbpath $basePathSt/20 --auth --smallfiles &

#
# Unsecure Target 
################################################################3
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""52 --dbpath $basePathSt/21 --smallfiles &
 
#
# Unsecure Replica Set
################################################################3
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""50 --dbpath $basePath/unsec1 --replSet rs1 --smallfiles &
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""51 --dbpath $basePath/unsec2 --replSet rs1 --smallfiles &

sleep 60
echo "Initiating ReplicaSets..."

#
# Initiate on Secure and Unsecure Replica Set  
################################################################3
$MONGO_HOME/bin/mongo --port $PORT_PREFIX""17 --eval "rs.initiate({ _id:'rs0', members:[{_id: 0, host:'localhost:$PORT_PREFIX""17', priority:10}, {_id: 1, host:'localhost:$PORT_PREFIX""18', priority:3}, {_id: 2, host:'localhost:$PORT_PREFIX""19', priority:3}]})" &

$MONGO_HOME/bin/mongo --port $PORT_PREFIX""50 --eval "rs.initiate({ _id:'rs1', members:[{_id: 0, host:'localhost:$PORT_PREFIX""50', priority:10}, {_id: 1, host:'localhost:$PORT_PREFIX""51', priority:3}]})" &

sleep 60

echo "Creating Users..."
#
# Create user on Secure Replica Set and Target 
################################################################3
$MONGO_HOME/bin/mongo --port $PORT_PREFIX""20 admin ./createAdmin.js &
$MONGO_HOME/bin/mongo --port $PORT_PREFIX""17 admin ./createAdmin.js &
wait
