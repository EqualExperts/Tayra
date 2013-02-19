

import java.net.UnknownHostException;

import com.ee.tayra.fixtures.AssertMongoFixture;
import com.ee.tayra.fixtures.MongoFixture;
import com.objectmentor.fixtures.CommandLineFixture;

import fit.Fixture;
import fitlibrary.DoFixture;

public class WhenTheReplicaSetIsRunning extends DoFixture {
	public WhenTheReplicaSetIsRunning() {
	}
	
	public Fixture openTerminalAndRun() {
		return new CommandLineFixture();
	}
	
	public Fixture connectToMongoDBNodeOnPort(String host, int port) throws UnknownHostException {
		return new MongoFixture(host, port);
	}
	
	public Fixture forSourceNodeOnPortAndDestinationNodeOnPort(String srcHost, int srcPort, String destHost, int destPort) throws UnknownHostException {
		return new AssertMongoFixture(srcHost, srcPort, destHost, destPort);
	}
}
