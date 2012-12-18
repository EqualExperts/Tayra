#!/bin/sh
sudo service mongodb stop

basePath=~/Desktop/data
basePathSt=~/Desktop/spike

gnome-terminal -t 27017-auth -x mongod --port 27017 --dbpath $basePath/db --replSet rs0 --auth --keyFile $basePath/keyfile &
gnome-terminal -t 27018-auth -x mongod --port 27018 --dbpath $basePath/db2 --replSet rs0 --auth --keyFile $basePath/keyfile &
gnome-terminal -t 27019-auth -x mongod --port 27019 --dbpath $basePath/db3 --replSet rs0 --auth --keyFile $basePath/keyfile &
gnome-terminal -t 27020-auth -x mongod --port 27020 --dbpath $basePathSt/20 --auth &
gnome-terminal -t 27021 -x mongod --port 27021 --dbpath $basePathSt/21 --smallfiles &

gnome-terminal -t unsecured-17017 -x mongod --port 17017 --dbpath $basePath/unsec1 --replSet rs1 --smallfiles &
gnome-terminal -t unsecured-17018 -x mongod --port 17018 --dbpath $basePath/unsec2 --replSet rs1 --smallfiles &
wait
