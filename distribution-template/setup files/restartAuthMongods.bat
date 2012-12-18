SET basePath=c:\mongoRs
SET basePathSt=c:\mongoSt

START TITLE 27017-auth mongod --port 27017 --dbpath %basePath%\db --replSet rs0 --auth --keyFile %basePath%\keyfile
START TITLE 27018-auth mongod --port 27018 --dbpath %basePath%\db2 --replSet rs0 --auth --keyFile %basePath%\keyfile
START TITLE 27019-auth mongod --port 27019 --dbpath %basePath%\db3 --replSet rs0 --auth --keyFile %basePath%\keyfile
START TITLE 27020-auth mongod --port 27020 --dbpath %basePathSt%\20 --auth

START TITLE 27021 mongod --port 27021 --dbpath %basePathSt%\21 --smallfiles

START TITLE unsecured-17017 mongod --port 17017 --dbpath %basePath%\unsec1 --replSet rs1 --smallfiles
START TITLE unsecured-17018 mongod --port 17018 --dbpath %basePath%\unsec2 --replSet rs1 --smallfiles

