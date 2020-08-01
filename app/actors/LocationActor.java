package actors;
import akka.japi.*;
import akka.actor.*;

import java.util.List;

import actors.HashTagActorProtocol.*;
import actors.LocationActorProtocol.locationHello;
public class LocationActor extends AbstractActor {
	
	public static Props getProps() {
		return Props.create(LocationActor.class);
	}
	
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
		        .match(locationHello.class, hello -> {
		        	List<String> reply=hello.name;
		            //String reply = "Hello, " + hello.name;
		            sender().tell(reply, self());
		        })
		        .build();
	}
	
	

}
