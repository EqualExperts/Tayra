import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.DBCollection;
import com.mongodb.MongoClient
import com.mongodb.ServerAddress

def sourceMongoDB = "localhost"
int port = 27017

ServerAddress serverAddress = new ServerAddress(sourceMongoDB, port)
MongoClient mongoClient = new MongoClient(serverAddress)

mongoClient.getDB('admin').authenticate('admin', 'admin'.toCharArray())

DB db = mongoClient.getDB('blog')
DBCollection collection = db.getCollection('post')

def j=1
while (true) {
	if(j%100 == 0) {
		sleep 2 * (60 * 1000)
	}
	BasicDBObject entry = new BasicDBObject()
	entry.put("SequenceId", j++)
	entry.put("date", new Date())
	collection.insert(entry)
	println "inserted "+ entry
}