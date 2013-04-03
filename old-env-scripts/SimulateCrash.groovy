
class Simulator extends TimerTask{

	boolean onWinOs
	int port
	def os = System.getProperty("os.name")

	public void run() {
		try {
			onWinOs = (os ==~ /Win.*/)
			port = 27017
			def processIdToKill = findProcessIdFor(port)
			def fileToRun = onWinOs ? "./setup-env/restartAuthSetup-parameterized.bat"
				: "./setup-env/restartAuthSetup-parameterized.sh"
			if(processIdToKill != 0) {
				def killProcessCmd = onWinOs ? "taskkill /PID $processIdToKill"
						: "kill -9 $processIdToKill"
				println "Executing $killProcessCmd on $os"
				killProcessCmd.execute().in.eachLine {
					println it
				}
				sleep(20000)
			}
			println 'Resurrecting Mongo Node ' + port
			fileToRun.execute().consumeProcessOutput()
		}
		catch (Exception e) {
			println ('Error: ' + e.message)
		}
	}

	def findProcessIdFor(int port) {
		def listProcessCmd = onWinOs ? 'netstat -a -o' : 'ps -ef'
		println "Executing $listProcessCmd on $os for "+ port
		def process = listProcessCmd.execute()
		StringWriter result = new StringWriter()
		process.in.filterLine(result) {
			it =~ /(.*|[0-9]+.*):27017/
		}
		def killableProcesses = result.toString().split(
			System.getProperty("line.separator"))
		.collect {
			def proc = it.split()
			if (proc[1] ==~ /(.*|[0-9]+.*):27017/) {
				return proc[4]
			}
		}
		.findAll {
			it != null
		}
		.first()

		if(killableProcesses!='') {
			return killableProcesses
		}
		println "No such process to kill"
		return 0
	}
}

Timer timer = new Timer()
timer.schedule(new Simulator(), 2000, 60000)
