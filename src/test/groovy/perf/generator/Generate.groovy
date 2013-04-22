package perf.generator

import static java.io.File.*

import java.io.File;
import java.io.FileSystem;
import java.security.AccessController;

import sun.security.action.GetPropertyAction;

def cli = new CliBuilder(usage:'generate -f <file> -s <size> [-u <unit>]')

cli.with {
	f args:1, argName: 'file', longOpt:'file', 'REQUIRED, output file', required: true
	s args:1, argName: 'size', longOpt:'size', 'REQUIRED, Size of output file', required: true
	u args:1, argName: 'unit', longOpt:'unit', 'REQUIRED, Default is KB, Valid Values M or MB, G or GB', optionalArg:true
}

def options = cli.parse(args)

if(!options) {
	return
}

fileName = options.f
fileSize = options.s
unit = 'K'
if(options.u) {
	unit = options.u
}
NEW_LINE = System.getProperty('line.separator')
println "Generating file $fileName with Size $fileSize $unit"
File tmpdir = new File(System.getProperty('java.io.tmpdir'))
def dataFile = new File(tmpdir, "$fileName")
dataFile.withWriter { writer ->
	def howMany = 1 * Integer.parseInt(fileSize)
	if(unit.startsWith('m') || unit.startsWith('M')) {
		howMany *= 1024
	}
	if(unit.startsWith('g') || unit.startsWith('G')) {
		howMany *= (1024 * 1024)
	}
	howMany.times {
		writeOneKB(writer)
	}
}
println "Generated file $dataFile.name"


def writeOneKB(writer) {
	8.times {
		writer.write '{ "ts" : { "$ts" : 1366288962 , "$inc" : 2} , "h" : 2763120522771994968 , "v" : 2 , "op" : "i" , "ns" : "things.items" , "o" : { "_id" : { "$oid" : "516fea4283eb9397cf8b6b55"} , "name" : "One"}}'
		writer.write NEW_LINE
	}
}

