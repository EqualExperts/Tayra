#!/bin/sh
rm -rf rs-0 rs-1 rs-2 
mkdir rs-0 rs-1 rs-2 

mongod --dbpath ./rs-0 --port 27017 --replSet rs --fork --logpath ./rs-0/log.0
mongod --dbpath ./rs-1 --port 27018 --replSet rs --fork --logpath ./rs-1/log.0
mongod --dbpath ./rs-2 --port 27019 --replSet rs --fork --logpath ./rs-2/log.0

sleep 3
mongo --eval "rs.initiate({ _id:'rs', members:[{_id: 0, host:'localhost:27017'}]})"
