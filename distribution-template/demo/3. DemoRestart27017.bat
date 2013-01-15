SET basePath=mongoRs

START "Secure@27017" mongod --port 27017 --dbpath %basePath%\db --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles