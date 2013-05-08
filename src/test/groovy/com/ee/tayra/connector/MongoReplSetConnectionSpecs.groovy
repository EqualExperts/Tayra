package com.ee.tayra.connector

import static com.ee.tayra.ConnectionFactory.*
import spock.lang.*

import com.mongodb.MongoClient
import com.mongodb.MongoException

public class MongoReplSetConnectionSpecs extends Specification {

  private String source
  private int port
  private MongoReplSetConnection mongoReplsetConnection
  private PrintWriter console

  def setup() {
    source = secureSrcNode
    port = secureSrcPort
    console = new PrintWriter(System.out,true)
    mongoReplsetConnection = new MongoReplSetConnection(source, port, true, console)
  }

  def cleanup() {
    mongoReplsetConnection = null
  }

  def loansTheMongoConnectionOnceAvailable() {
    given: 'the closure which needs the mongo connection'
      def actual = null
      def execute = { mongo -> actual = mongo }

    when: 'using the connection'
      mongoReplsetConnection.using(execute)

    then: 'ensure mongo connection is available'
      actual instanceof MongoClient
  }

  def allowsUserOperationBetweenNodeCrashAndReelectionAttempt() {
    given: 'the node crashes'
      def called = false
      def execute = {
        if(!called) {
          throw new MongoException.Network('The Node Crashed', new IOException())
        }
      }

    and: 'a retry closure'
      def retry = { called = true }

    when: 'using the connection'
      mongoReplsetConnection.using(execute, retry)

    then: 'ensure retry was invoked'
      called
  }

  def doesNotReactToAnyFailureOtherThanNodeCrash() {
    given: 'a problem other than node crash occurs'
      def notCalled = true
      def execute = {
        if(notCalled) {
          throw new MongoException('Non Node-Crash Exception')
        }
      }

    and: 'a retry closure'
      def retry = { notCalled = false }

    when: 'using the connection'
      mongoReplsetConnection.using(execute, retry)

    then: 'ensure retry was not invoked'
      notCalled
      thrown(MongoException)
  }

  def doesNotSurviveNodeCrashWhenRetryableIsFalse() {
    given: 'Mongo replica set connection with retryable as false'
      mongoReplsetConnection = new MongoReplSetConnection(source, port, false)
      and: 'node crashes'
      def called = false
      def execute = {
        if(!called) {
          throw new MongoException.Network('Node Crashed', new IOException())
        }
      }
    and: 'a retry closure'
      def retry = { called = true }

    when: 'using the connection'
      mongoReplsetConnection.using(execute, retry)

    then: 'ensure retry was not invoked'
      !called
  }

  def connectsToASingleNode() {
    given: 'Mongo replica set connection to a single node with retryable as false'
      mongoReplsetConnection = new MongoReplSetConnection(unsecureTgtNode, unsecureTgtPort, false)
      def executeCalled = false
      def retryCalled = false
      def execute = { executeCalled = true  }

    and: 'a retry closure'
      def retry = { retryCalled = true }

    when: 'using the connection'
      mongoReplsetConnection.using(execute, retry)

    then: 'ensure execute was invoked '
      executeCalled
      
    and: 'retry was not invoked'
      !retryCalled
  }
}
