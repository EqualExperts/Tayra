SET basePath=mongoRs
SET basePathSt=mongoSt

rm -rf %basePath% %basePathSt%
mkdir %basePath%  %basePathSt%
mkdir %basePath%\db %basePath%\db2 %basePath%\db3 %basePathSt%\20 %basePathSt%\21 %basePath%\unsec1 %basePath%\unsec2
echo Zom89ZAH > %basePath%\keyfile

START "Secure@27017" mongod --port 27017 --dbpath %basePath%\db --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles
START "Secure@27018" mongod --port 27018 --dbpath %basePath%\db2 --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles
START "Secure@27019" mongod --port 27019 --dbpath %basePath%\db3 --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles
START "Secure@27020" mongod --port 27020 --dbpath %basePathSt%\20 --auth --smallfiles

START "Unsecure@27021" mongod --port 27021 --dbpath %basePathSt%\21 --smallfiles

START "Unsecure@17017" mongod --port 17017 --dbpath %basePath%\unsec1 --replSet rs1 --smallfiles
START "Unsecure@17018" mongod --port 17018 --dbpath %basePath%\unsec2 --replSet rs1 --smallfiles

sleep 300
START "mongo-17" mongo --eval "rs.initiate({ _id:'rs0', members:[{_id: 0, host:'localhost:27017', priority:10}, {_id: 1, host:'localhost:27018', priority:3}, {_id: 2, host:'localhost:27019', priority:3}]})"

START "Unsec-17" mongo --port 17017 --eval "rs.initiate({ _id:'rs1', members:[{_id: 0, host:'localhost:17017', priority:10}, {_id: 1, host:'localhost:17018', priority:3}]})"

sleep 60

START mongo --port 27020 admin createAdmin.js
START mongo admin createAdmin.js