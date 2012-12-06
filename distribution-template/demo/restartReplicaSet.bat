rm rs-0/mongod.lock rs-1/mongod.lock rs-2/mongod.lock

START mongod --dbpath ./rs-0 --port 27017 --replSet rs &
START mongod --dbpath ./rs-1 --port 27018 --replSet rs &
START mongod --dbpath ./rs-2 --port 27019 --replSet rs &

sleep 3
START mongo --eval "rs.initiate({ _id:'rs', members:[{_id: 0, host:'localhost:27017'}]})"
