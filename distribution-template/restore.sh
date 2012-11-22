#!/bin/sh
java -cp libs/commons-cli-1.1.jar:libs/functionaljava-3.1.jar:libs/groovy-all-2.0.4.jar:libs/mongo-java-driver-2.7.3.jar:libs/MongoBeaver-0.3.0.Alpha1.jar com.ee.beaver.runner.Runner "restore" $@
