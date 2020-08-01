package actors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.AbstractActor.Receive;
import akka.actor.AbstractActorWithTimers;
import akka.event.LoggingAdapter;
import controllers.HomeController;
import play.libs.Json;
import twitter.SearchResults;
import twitter.Twitter;
import twitter4j.QueryResult;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import static akka.pattern.PatternsCS.ask;
import java.util.concurrent.TimeUnit;
import scala.concurrent.*;
import akka.actor.AbstractActorWithTimers;
import twitter4j.Status;


import scala.concurrent.duration.Duration;

/**
 * This class creates an actor that handles all the Twitter results. It is the child of the Abstract actor with the Timers.
 */

public class ProfileActor extends AbstractActorWithTimers {
	
	public final String key ;
	int c = 0;
//	ConfigurationBuilder cb = new ConfigurationBuilder();
	List<ActorRef> userActors = new ArrayList<>();
	HashMap<ActorRef,String> map = new HashMap<>();
	List<SearchResults> res = new ArrayList<>();
	List<MyWebSocketActor.TimeMessage> g = new ArrayList<>();
	
	  
	    private static final class FirstTick {
	    }
	    
	    private static final class Tick {
	    }
	    
	    static public class RegisterMsg {
	    }
	    private static Object TICK_KEY = "TickKey";
		 /**
	     * This method sets the Timer so that it sends itself the tick() message periodically.
	     */
	    @Override
	    public void preStart() {
	        getTimers().startPeriodicTimer("Timer", new Tick(), Duration.create(5, TimeUnit.SECONDS));
	        
	    }
	     /**
	     * Creates a Twitter Result actor that produces the Tweets when keyword is passed.
	     * @param key keyword to perform twitter Search
	     * @return An actor that handles the Twitter Results.
	     */
    public static Props props(String key) {
        return Props.create(ProfileActor.class ,key ) ;
    }
    	 /**
     * Produces a twitter result for that particular keyword periodically.
     * @param key A keyword to perform search.
     */        
    public ProfileActor (String key) {
  	
        this.key = key;
        
        getTimers().startSingleTimer(TICK_KEY, new FirstTick(), 
                Duration.create(1000, TimeUnit.MILLISECONDS));

    }
    
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this); 
	   /**
	   * creates a receive for Actor 
	   @return Receive receive object
	   */
	    @Override
	    
	    public Receive createReceive() {
	    	//.match(RegisterMsg.class, msg -> {c++; map.put(sender(),c);})
	        return receiveBuilder()
	          .match(RegisterMsg.class , msg -> addusers())
	          .match(Tick.class, message -> {
	        	                    	    
                	    System.out.println("Before tell");
                		
                		for (HashMap.Entry<ActorRef,String> entry : map.entrySet()) {
                			//System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
                			MyWebSocketActor.TimeMessage tMsg = new MyWebSocketActor.TimeMessage(entry.getValue());
                			entry.getKey().tell(tMsg,self());
                		}
                	  
	          })  
	          .build();
	      }
		  /**
		  * add users to list of tweets
		  */
	    public void addusers(){
	    	map.put(sender(),HomeController.request.getQueryString("keyword"));
	    }

   }
	    

