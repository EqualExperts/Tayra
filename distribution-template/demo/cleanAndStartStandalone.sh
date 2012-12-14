#!/bin/sh
rm -rf standalone standaloneTwo
mkdir standalone standaloneTwo

mongod --dbpath ./standalone --port 27020 --fork --logpath ./standalone/log.0
mongod --dbpath ./standaloneTwo --port 27021 --fork --logpath ./standalone/log.1

mongo localhost:27020 createAdmin.js
"
