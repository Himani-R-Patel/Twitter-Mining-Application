package actors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.util.concurrent.*;
import akka.NotUsed;
import akka.actor.*;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import play.libs.Json;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
//import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import twitter.SearchResults;
import twitter.Twitter;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import actors.SentimentActorProtocol.SentimentHello;
import static akka.pattern.PatternsCS.ask;
import java.util.stream.Collectors;
import akka.util.Timeout;
import ch.qos.logback.classic.net.SyslogAppender;
import controllers.HomeController;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import akka.dispatch.*;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Await;
import scala.concurrent.Awaitable;
import scala.concurrent.Promise;
import scala.concurrent.duration.Duration;
import scala.util.parsing.json.JSONArray;
import akka.util.Timeout;
//import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContext$;
/**
 * This class makes a web socket connection with the actor which thereby allows the two way full Duplex communication.
 * The client can send messages and the server can receive messages at any time, as long as there is an active WebSocket connection between the server and the client.
 */
public class MyWebSocketActor extends AbstractActor {

	List<String> dummy = new ArrayList<>();
	JsonNode SampleJson = null;
	HashMap<ActorRef,String> map = new HashMap<>();
	List<SearchResults> res = new ArrayList<>();

	int count = 0;
	Status s1=null;
	
	

	public static Props props(ActorRef out) {
		return Props.create(MyWebSocketActor.class, out);
	}

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final ActorRef out;
	//ActorSystem system;
	//ActorRef sentimentActor=system.actorOf(SentimentActor.getProps());
	
/**
	 * Constructor method which passes the Actor reference object as a parameter and adds dummy values to it in order to perform the actor test.
	 *@param out A reference object of an actor.
	 */
	public MyWebSocketActor(ActorRef out) {
		dummy.add("abc");
		dummy.add("bcd");
		SampleJson = Json.toJson(dummy);
		this.out = out;
	}
	/**
	 *  When a new UserActor is started, it has to register itself with the TimeActor. We do this by sending a RegisterMsg to the TimeActor.
	 */

	  @Override
	    public void preStart() {
	       	context().actorSelection("/user/TwitterResultActor/")
	                 .tell(new ProfileActor.RegisterMsg(), self());
	       	
	       	System.out.println("Started Server");
	    }
	   /**
	   * It handles messages from the TimeActor to push the new time information to the front-end through the WebSocket. For that, we need the message class.
	 
	   *
	   */
	  static public class TimeMessage {
		  
	      public final String res;
	       public TimeMessage(String result) {
	    	   System.out.println("Before tell");
	           this.res = result;
	       }
	   }
	  /**
	   * The sendTime method creates a new JSON object with the time from the message and sends it through the WebSocket.
	   * @param msg That accepts time as a parameter for creating connection. 
	   */
 
	 
	   
	//  final ExecutionContext ec = system.dispatcher();
	   
	  ActorSystem system;
	   private void sendTime(TimeMessage msg) throws Exception,TwitterException , InterruptedException ,ExecutionException {	  	
		  System.out.println("At tell");
		  System.out.println(msg.res.toString());
		 
				
		 // String result = (String) Await.result((Awaitable<String>) future, Duration.create(5, TimeUnit.SECONDS));
		  gettweets(msg.res.toString());
		//  gettweets(msg.res.toString());
	
	   }	
	   
	int count3=0;   
	   @SuppressWarnings("unused")
	private void gettweets(String searchword) throws TwitterException,Exception {
		   Twitter tweet = new Twitter(searchword);
		   CompletionStage<QueryResult> SearchResults1 = tweet.get() ;	
		   system=ActorSystem.create();  
			  system.actorOf(SentimentActor.getProps());
			 
			 HomeController control=new HomeController(system);
			 
			final String	 emotion	 =  control.TwitterSentiment(searchword);
						// TODO Auto-generated catch block
				
		   res=new ArrayList<SearchResults>();
		   final Set<String> q = new HashSet<>();
		   final ArrayNode array=Json.newArray();
		   SearchResults1.thenAccept( r -> {

			   //Stack<String> q = new Stack<>();
			//   Status stat=null;
			//   if (count == 0){
			//	   count++ ;
			   
				   System.out.println("Am i called jsut once ??");
				   SearchResults newres = null;
				   int c =0 ;
				   if (c < 9){
					   for (Status s : r.getTweets()){   
						   c++;
						   /*if(c==0) {
							   stat=s;
						   }*/
						    String location=s.getGeoLocation()==null?"":s.getGeoLocation().getLatitude()+","+s.getGeoLocation().getLongitude();
						   
						   SearchResults str=new SearchResults("@" +s.getUser().getScreenName() ,getHashTag("\t" + s.getText()), searchword,location);
						  
						   final ObjectNode eachTweet = Json.newObject(); 
						   eachTweet.put("handle", "@" +s.getUser().getScreenName());
						   eachTweet.put("tweets", "@" +getHashTag("\t" + s.getText()));
						   eachTweet.put("words", searchword);
						   eachTweet.put("location", location);
						   eachTweet.put("emotion",emotion);
						   
						// Convert Java Array into JSON
						   array.add(eachTweet);
						   res.add(str);
						   
						   
						  
						   
						   //	res.add(new SearchResults("@" +s.getUser().getScreenName() ,getHashTag("\t" + s.getText()), searchword));   
					   }



					  			   
				   }
				   
				  /* Gson gsonBuilder = new GsonBuilder().create();
				   String jsonFromJavaArrayList = gsonBuilder.toJson(res);
				   System.out.println("This is the array of 10 tweets"+jsonFromJavaArrayList);
				   final ObjectNode response = Json.newObject();
				   
				   response.put("response" , Json.toJson(jsonFromJavaArrayList));*/
				   out.tell(array, self()); 
				//   System.out.println(arra.size());
			
		   }); 
	   }
public static String getHashTag(String result) {

	StringBuilder htmlBuilder = new StringBuilder();
	htmlBuilder.append("<html>");
	
	String[] tokens=result.split("\\s+");
	for(int i=0;i<tokens.length;i++)
	{
		String token=tokens[i];
		if( token!=null && token.startsWith("#") )
		{
			//System.out.println("result----" +token);

			//htmlBuilder.append("<a href=\"http://www.w3schools.com\">"+ token +"</a>");
			String token2=token.replaceAll("#", "");
			htmlBuilder.append("<a href=\"http://localhost:9000/hashTag?key=" +token2+ "\">" + token +"</a>");

		}
		else
		{
			htmlBuilder.append(" "+token+" ");

		}


	}

	return (htmlBuilder.toString());

}



		     /** 
		  * This method builds the Receive by matching both methods properties.
		  * @return Connection
		  */
	@Override
	public Receive createReceive(){
		return receiveBuilder().match(TimeMessage.class, this::sendTime).build();
	}
}