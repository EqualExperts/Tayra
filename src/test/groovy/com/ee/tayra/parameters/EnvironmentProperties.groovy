package com.ee.tayra.parameters

import com.ee.tayra.Environment
import com.ee.tayra.NamedParameters

class EnvironmentProperties {
	def static NamedParameters parameters = Environment.settings()
	def static secureSrcNode = parameters.get("{secureSrcNode}")
	def static secureSrcPort = Integer.parseInt(parameters.get("{secureSrcPort}"))
	def static unsecureSrcNode = parameters.get("{unsecureSrcNode}")
	def static unsecureSrcPort = Integer.parseInt(parameters.get("{unsecureSrcPort}"))
	def static unsecureStandaloneNode = parameters.get("{unsecureTgtNode}")
	def static unsecureStandalonePort = Integer.parseInt(parameters.get("{unsecureTgtPort}"))
	def static secureStandaloneNode = parameters.get("{secureTgtNode}")
	def static secureStandalonePort = Integer.parseInt(parameters.get("{secureTgtPort}"))
	def static username = parameters.get("{username}")
	def static password = parameters.get("{password}")
	def static backupFile = "test.out"
}
