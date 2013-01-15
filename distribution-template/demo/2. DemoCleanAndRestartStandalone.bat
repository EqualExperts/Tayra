SET basePathSt=mongoSt

rm -rf %basePathSt%\22 %basePathSt%\23 %basePathSt%\24 %basePathSt%\25
mkdir %basePathSt%\22 %basePathSt%\23 %basePathSt%\24 %basePathSt%\25

START "Secure@27022" mongod --port 27022 --dbpath %basePathSt%\22 --auth --smallfiles
START "Secure@27023" mongod --port 27023 --dbpath %basePathSt%\23 --auth --smallfiles
START "Secure@27024" mongod --port 27024 --dbpath %basePathSt%\24 --auth --smallfiles
START "Secure@27025" mongod --port 27025 --dbpath %basePathSt%\25 --auth --smallfiles

sleep 30

START mongo --port 27022 admin createAdmin.js
START mongo --port 27023 admin createAdmin.js
START mongo --port 27024 admin createAdmin.js
START mongo --port 27025 admin createAdmin.js
