
if "%1"=="" (SET MONGO_HOME=%MONGO_HOME%\bin) else (SET MONGO_HOME=%1)
if "%2"=="" (SET PORT_PREFIX=270) else (SET PORT_PREFIX=%2)

SET mongoData=C:\ReleaseMongods\
SET mongoBase=%mongoData%\%PORT_PREFIX%\
SET basePath=%mongoBase%\replSetData
SET basePathSt=%mongoBase%\standaloneData

START "Secure@%PORT_PREFIX%17" mongod --port %PORT_PREFIX%""17 --dbpath %basePath%\db --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles &
START "Secure@%PORT_PREFIX%18" mongod --port %PORT_PREFIX%""18 --dbpath %basePath%\db2 --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles &
START "Secure@%PORT_PREFIX%19" mongod --port %PORT_PREFIX%""19 --dbpath %basePath%\db3 --replSet rs0 --auth --keyFile %basePath%\keyfile --smallfiles &

START "Secure@%PORT_PREFIX%20" mongod --port %PORT_PREFIX%""20 --dbpath %basePathSt%\20 --auth --smallfiles &
START "Unsecure@%PORT_PREFIX%52" mongod --port %PORT_PREFIX%""52 --dbpath %basePathSt%\52 --smallfiles &

START "Unsecure@%PORT_PREFIX%50" mongod --port %PORT_PREFIX%""50 --dbpath %basePath%\unsec1 --replSet rs1 --smallfiles &
START "Unsecure@%PORT_PREFIX%51" mongod --port %PORT_PREFIX%""51 --dbpath %basePath%\unsec2 --replSet rs1 --smallfiles &
