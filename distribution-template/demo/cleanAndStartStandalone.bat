rm -rf standalone
mkdir standalone

START mongod --dbpath ./standalone --port 27020 &
