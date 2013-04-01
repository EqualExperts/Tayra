SET basePath=mongoRs
SET basePathSt=mongoSt

START "Secure@27017" mongod --port 27017 --dbpath %basePath%\db --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles
START "Secure@27018" mongod --port 27018 --dbpath %basePath%\db2 --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles
START "Secure@27019" mongod --port 27019 --dbpath %basePath%\db3 --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles
START "Secure@27020" mongod --port 27020 --dbpath %basePathSt%\20 --auth --smallfiles

START "Unsecure@27021" mongod --port 27021 --dbpath %basePathSt%\21 --smallfiles

START "Unsecure@17017" mongod --port 17017 --dbpath %basePath%\unsec1 --replSet rs1 --smallfiles
START "Unsecure@17018" mongod --port 17018 --dbpath %basePath%\unsec2 --replSet rs1 --smallfiles