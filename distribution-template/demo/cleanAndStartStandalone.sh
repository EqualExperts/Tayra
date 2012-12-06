#!/bin/sh
rm -rf standalone
mkdir standalone

mongod --dbpath ./standalone --port 27020 --fork --logpath ./standalone/log.0
