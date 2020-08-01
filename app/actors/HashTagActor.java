package actors;
import akka.japi.*;
import akka.actor.*;

import java.util.List;

import actors.HashTagActorProtocol.*;
public class HashTagActor extends AbstractActor {
	
	public static Props getProps() {
		return Props.create(HashTagActor.class);
	}
	
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
		        .match(SayHello.class, hello -> {
		        	List<String> reply=hello.name;
		            //String reply = "Hello, " + hello.name;
		            sender().tell(reply, self());
		        })
		        .build();
	}
	
	

}
