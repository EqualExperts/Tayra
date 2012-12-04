package generator
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
new File(fileName).withWriter { writer ->
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
	println "Generated file $fileName"
}


def writeOneKB(writer) {
	8.times {
		writer.write '{ "ts" : { "$ts" : 1353993475 , "$inc" : 1} , "h" : 0 , "v" : 2 , "op" : "n" , "ns" : "" , "o" : { "msg" : "initiating set."}}'
		writer.write NEW_LINE
	}
}

