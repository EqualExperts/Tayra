package com.ee.beaver.command

import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.ServerAddress

public class MongoReplSetConnection {

	private MongoClient master
	private def nodes = []
	private boolean retryable

	public MongoReplSetConnection(String sourceMongoDB, int port, boolean retryable = true) {
		ServerAddress server = new ServerAddress(sourceMongoDB, port)
		master = new MongoClient(server)
		// TODO: Replace this API by different call
		String[] hosts = master.getDB("test").command("ismaster").get("hosts")
		println "Hosts are: $hosts"
		hosts.each {
			nodes.add(getServerAddress(it))
		}
		this.retryable = retryable
	}

	private ServerAddress getServerAddress(String host) {
		String[] addressInfo = host.split(":")
		String source = addressInfo[0]
		int port = Integer.valueOf(addressInfo[1])
		new ServerAddress(source, port)
	}

	def using(Closure runnable, Closure betweenRetry = {}) {
		def clonedRunnable = runnable.clone()
		while(retryable) {
			try {
				clonedRunnable(master)
				retryable = false
			} catch(MongoException problem) {
				println "Primary crashed. Re-stablishing Connection"
				betweenRetry.clone().call()
				sleep 4000
				def mongoClient = new MongoClient(nodes)
				sleep 4000
				String master = mongoClient.getDB("test").command("ismaster").get("primary")
				println("The new master is: $master")
				ServerAddress serverAdd = getServerAddress(master)
				println "Server is : $serverAdd"
				this.master = new MongoClient(serverAdd)
			}
		}
	}
}