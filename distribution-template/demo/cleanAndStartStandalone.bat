rm -rf standalone standaloneTwo
mkdir standalone standaloneTwo

START mongod --dbpath ./standalone --port 27020 --auth &
START mongod --dbpath ./standaloneTwo --port 27021 &

START mongo localhost:27020 createAdmin.js --shell
