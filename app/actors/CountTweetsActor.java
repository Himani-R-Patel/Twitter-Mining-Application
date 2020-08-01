package actors;
import akka.japi.*;
import akka.actor.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import actors.CountTweetsActorProtocol.*;
public class CountTweetsActor extends AbstractActor {
	
	public static Props getProps() {
		return Props.create(CountTweetsActor.class);
	}
	
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
		        .match(counthello.class, hello -> {
		        	LinkedHashMap<String, Integer> reply=hello.name;
		            //String reply = "Hello, " + hello.name;
		            sender().tell(reply, self());
		        })
		        .build();
	}
	
	

}
