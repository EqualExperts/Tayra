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

# Secure Replica Set
################################################################
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""17 --dbpath $basePath/db --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""18 --dbpath $basePath/db2 --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""19 --dbpath $basePath/db3 --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &

# Secure Target
################################################################3
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""20 --dbpath $basePathSt/20 --auth --smallfiles &

# Unsecure Target
################################################################3
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""52 --dbpath $basePathSt/21 --smallfiles &

# Unsecure Replica Set
################################################################3
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""50 --dbpath $basePath/unsec1 --replSet rs1 --smallfiles &
$MONGO_HOME/bin/mongod --port $PORT_PREFIX""51 --dbpath $basePath/unsec2 --replSet rs1 --smallfiles &
