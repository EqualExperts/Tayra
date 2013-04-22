import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.MongoClient
import com.mongodb.ServerAddress

def cli = new CliBuilder(usage:'DataGenerator --source=<MongoDB> [--port=number] [-u username] [-p password]')
cli.with {
  _  args:1, argName: 'MongoDB Host', longOpt:'source', 'Source IP/Host (=localhost or ip)', optionalArg:true, required:true
  _  args:1, argName: 'port', longOpt:'port', 'Target MongoDB Port', optionalArg:true
  u  args:1, argName: 'username', longOpt:'username', 'username for authentication', optionalArg:true
  p  args:1, argName: 'password', longOpt:'password', 'password for authentication', optionalArg:true
  _  args:1, argName: 'data feed interval', longOpt:'feedInterval', 'Time Interval in secs to pause while feeding data', optionalArg:true
}

def options = cli.parse(args)

if(!options) {
  return
}

if(options.arguments()){
  console.println "Cannot Understand ${options.arguments()}"
  cli.usage()
  return
}

sourceMongoDB = options.source
srcPort = 27017
username = ""
password = ""
feedInterval = 60 //secs

if(options.port) {
  srcPort = Integer.parseInt(options.port)
}

if(options.username) {
  username = options.username
}

if(options.password) {
  password = options.password
}

if(options.feedInterval) {
  feedInterval = Integer.parseInt(options.feedInterval)
}

println "Connecting to mongo"

ServerAddress serverAddress = new ServerAddress(sourceMongoDB, srcPort)
MongoClient mongoClient = new MongoClient(serverAddress)

mongoClient.getDB('admin').authenticate(username, password.toCharArray())

DB db = mongoClient.getDB('blog')
DBCollection collection = db.getCollection('post')

def j=1
while (true) {
	if(j%100 == 0) {
		sleep feedInterval * (1 * 1000)
	}
	BasicDBObject entry = new BasicDBObject()
	entry.put("SequenceId", j++)
	entry.put("date", new Date())
	collection.insert(entry)
	println "inserted "+ entry
}