#!/bin/sh
kill -9 $(ps aux | grep 'mongod' | awk '{print $2}')