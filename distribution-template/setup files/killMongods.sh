#!/bin/sh
kill -2 $(ps aux | grep 'mongod' | awk '{print $2}')
