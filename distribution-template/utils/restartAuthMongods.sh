#!/bin/sh
#sudo service mongodb stop

basePath=~/Desktop/data
basePathSt=~/Desktop/spike

gnome-terminal -t Secure@27017 -x mongod --port 27017 --dbpath $basePath/db --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &
gnome-terminal -t Secure@27018 -x mongod --port 27018 --dbpath $basePath/db2 --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &
gnome-terminal -t Secure@27019 -x mongod --port 27019 --dbpath $basePath/db3 --replSet rs0 --auth --keyFile $basePath/keyfile --smallfiles &
gnome-terminal -t Secure@27020 -x mongod --port 27020 --dbpath $basePathSt/20 --auth --smallfiles &
gnome-terminal -t Unsecure@27021 -x mongod --port 27021 --dbpath $basePathSt/21 --smallfiles &

gnome-terminal -t Unsecure@17017 -x mongod --port 17017 --dbpath $basePath/unsec1 --replSet rs1 --smallfiles &
gnome-terminal -t Unsecure@17018 -x mongod --port 17018 --dbpath $basePath/unsec2 --replSet rs1 --smallfiles &
wait