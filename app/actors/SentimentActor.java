package actors;
import akka.japi.*;
import akka.actor.*;

import java.util.List;

import actors.HashTagActorProtocol.*;
import actors.SentimentActorProtocol.SentimentHello;
public class SentimentActor extends AbstractActor {
	
	public static Props getProps() {
		return Props.create(SentimentActor.class);
	}
	
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
		        .match(SentimentHello.class, hello -> {
		        	String reply=hello.name;
		            //String reply = "Hello, " + hello.name;
		            sender().tell(reply, self());
		        })
		        .build();
	}
	
	

}
