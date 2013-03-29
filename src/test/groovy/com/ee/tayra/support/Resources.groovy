package com.ee.tayra.support

import com.ee.tayra.ConnectionData

class Resources {
	def static ConnectionData connection = ConnectionData.instance()
	def static secureSrcNode = connection.secureSrcNode
	def static secureSrcPort = Integer.parseInt(connection.secureSrcPort)
	def static unsecureSrcNode = connection.unsecureSrcNode
	def static unsecureSrcPort = Integer.parseInt(connection.unsecureSrcPort)
	def static unsecureStandaloneNode = connection.unsecureTgtNode
	def static unsecureStandalonePort = Integer.parseInt(connection.unsecureTgtPort)
	def static secureStandaloneNode = connection.secureTgtNode
	def static secureStandalonePort = Integer.parseInt(connection.secureTgtPort)
	def static username = connection.username
	def static password = connection.password
	def static backupFile = "test.out"
}
