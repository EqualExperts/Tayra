#!/bin/sh
sudo service mongodb stop

basePath=~/Desktop/data
basePathSt=~/Desktop/spike

rm -rf $basePath/ $basePathSt/
mkdir $basePath/ $basePathSt/
mkdir $basePath/db $basePath/db2 $basePath/db3 $basePathSt/20 $basePathSt/21 $basePath/unsec1 $basePath/unsec2
echo Zom89ZAH > $basePath/keyfile
chmod 700 $basePath/keyfile

gnome-terminal -t Secure@27017 -x mongod --port 27017 --dbpath $basePath/db --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &
gnome-terminal -t Secure@27018 -x mongod --port 27018 --dbpath $basePath/db2 --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &
gnome-terminal -t Secure@27019 -x mongod --port 27019 --dbpath $basePath/db3 --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &
gnome-terminal -t Secure@27020 -x mongod --port 27020 --dbpath $basePathSt/20 --auth --smallfiles &

gnome-terminal -t Unsecure@27021 -x mongod --port 27021 --dbpath $basePathSt/21 --smallfiles &

gnome-terminal -t Unsecure@17017 -x mongod --port 17017 --dbpath $basePath/unsec1 --replSet rs1 --smallfiles &
gnome-terminal -t Unsecure@17018 -x mongod --port 17018 --dbpath $basePath/unsec2 --replSet rs1 --smallfiles &

sleep 300
gnome-terminal -t mongo-17 -x mongo --eval "rs.initiate({ _id:'rs0', members:[{_id: 0, host:'Bhagyashree:27017', priority:10}, {_id: 1, host:'Bhagyashree:27018', priority:3}, {_id: 2, host:'Bhagyashree:27019', priority:3}]})" &

gnome-terminal -t unsec-17 -x mongo --port 17017 --eval "rs.initiate({ _id:'rs1', members:[{_id: 0, host:'Bhagyashree:17017', priority:10}, {_id: 1, host:'Bhagyashree:17018', priority:3}]})" &

sleep 60

gnome-terminal -t mongo-20 -x mongo --port 27020 admin createAdmin.js &
gnome-terminal -t mongo -x mongo admin createAdmin.js &
wait