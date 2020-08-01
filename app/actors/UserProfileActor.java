package actors;
import akka.japi.*;
import akka.actor.*;

import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import actors.UserProfileActorProtocol.*;
public class UserProfileActor extends AbstractActor {
	
	public static Props getProps() {
		return Props.create(UserProfileActor.class);
	}
	
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
		        .match(profileHello.class, hello -> {
		        	HashMap<String, List<String>> reply=hello.name;
		            //String reply = "Hello, " + hello.name;
		            sender().tell(reply, self());
		        })
		        .build();
	}
	
	

}
