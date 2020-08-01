package controllers;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import actors.UserProfileActorProtocol.*;
import actors.CountTweetsActor;
import actors.HashTagActor;
import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;
import javax.inject.Inject;
import javax.inject.Singleton;
import twitter.Twitter;
import actors.HashTagActorProtocol.*;
import actors.LocationActor;
import actors.CountTweetsActorProtocol.*;
import actors.MyWebSocketActor;
import actors.ProfileActor;
import actors.SentimentActor;
import actors.SentimentActorProtocol.SentimentHello;
import actors.UserProfileActor;
import akka.Done;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.ws.TextMessage;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Keep;
import play.mvc.*;
import play.mvc.Http.RequestHeader;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import services.Counter;
import twitter.Twitter;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import views.html.*;
import views.*;
import scala.compat.java8.FutureConverters;
import twitter4j.Query;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import play.libs.streams.ActorFlow;
import play.http.websocket.Message;
import play.libs.concurrent.HttpExecutionContext;
import actors.SentimentActorProtocol.SentimentHello;
import actors.LocationActorProtocol.locationHello;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
	private HttpExecutionContext httpExecutionContext;
	public static play.mvc.Http.Request request ;
    
    @Inject
    private ActorSystem actorSystem;
    @Inject
    private Materializer materializer;
   /* @Inject
	public HomeController(HttpExecutionContext ec) {

		this.httpExecutionContext = ec;
	}*/
  /**
     * Constructor method which creates the actor system for tweets and tweet handler.
     * @param system An actor system that handles the searches.
     * 
     */
    
    ActorRef helloActor;
    ActorRef CountTweetActor;
    ActorRef sentimentActor;
    ActorRef userActor;
    ActorRef locationActor;
    @Inject public HomeController(ActorSystem system) {
        system.actorOf(ProfileActor.props("") , "TwitterResultActor");
        CountTweetActor=system.actorOf(CountTweetsActor.getProps());
        helloActor=system.actorOf(HashTagActor.getProps());
        userActor=system.actorOf(UserProfileActor.getProps());
        locationActor=system.actorOf(LocationActor.getProps());
       sentimentActor=system.actorOf(SentimentActor.getProps());
        }
    
  /**
     * This method produces an index page in a HTML form.
     * @return A HTML page that tells our application is ready to use.
     */
    public Result index() {
    	
        return ok(index.render("Your new application is ready."));
    }
     /**
     * This method generates the JavaScript for webSocket test.
     * @return A HTML page that generates script.
     */
    public Result scripts() {
    	request = request();
    
        return ok(script.render(request()));
    }
    
    public HomeController() {


	}
    /**
     * This method directs us to the mainview page of our application that enables us to perform a search on a keyword.
     * @return The Search HTML page.
     */
 
    public Result Search() {   		
        return ok(mainview.render("Tweet Analytics")); 
       
    }
	/**
     * This method is called when the search is made on the person who made the tweet.
     * It contains an ArrayList that store the Person's information such as the no of Followers, no.of friend etc. 
     * and another List that contains the tweets that is made on the Application.
     * @param name The name of the person who has made that particular tweet.
     * @return A profile HTML page of the person.
     * @throws TwitterException An exception class that will be thrown when TwitterAPI calls are failed. In case the Twitter server returned HTTP error code, you can get the HTTP status code using getStatusCode() method.
     * @throws InterruptedException It is thrown when the thread is waiting or sleeping and another Thread interrupts it.
     * @throws ExecutionException Exception thrown when attempting to retrieve the result of a task that aborted by throwing an exception.
     */
    public CompletionStage<Result> profile(String name) throws TwitterException ,InterruptedException ,ExecutionException{    	

    	Twitter tweet = new Twitter(name);

    	CompletableFuture<String> ProfileInfo = (CompletableFuture<String>) tweet.getProfile();

    	List<String> Tweets = new ArrayList<>();
    	CompletableFuture<List<Status>> FutureList = (CompletableFuture<List<Status>>) tweet.getDetails();

    	 StringBuilder result = new StringBuilder();
    	FutureList.thenAccept(s ->  s.stream()
    			.map(f -> "\t"+f.getText())
    			.limit(10)
    			.forEach(Tweets::add));
    	
    	
    	ProfileInfo.thenAccept(s-> result.append(s));
    	
    	HashMap<String,List<String>> profileMap=new HashMap<>();
    	profileMap.put(result.toString(),Tweets);
    	return FutureConverters.toJava(ask(userActor, new profileHello(profileMap), 1000))
                .thenApply(response -> {
             
        		return ok(profile.render((HashMap<String,List<String>>) response));
  	      });


    }
    
    public CompletionStage<Result> location(String name) throws TwitterException ,InterruptedException ,ExecutionException{    	

		//  System.out.println("Geo location name:"+name);
		Twitter tweet = new Twitter(name);
		List<String> res =    new ArrayList<>();

		CompletableFuture<QueryResult> FutureLinks =  (CompletableFuture<QueryResult>) tweet.getLocationTweets();
		
		if( FutureLinks!=null)
			FutureLinks.thenAccept( r -> r.getTweets()
					.stream()
					.map(d ->   d.getText()==null?"no geo location data available...Sorry!!":d.getText())
					.limit(10)
					.forEach(res::add));

	//	return ok(location.render(res));
		return FutureConverters.toJava(ask(locationActor, new locationHello(res), 1000))
	               .thenApply(response -> {
	            
	       		return ok(location.render((List<String>) response));
	 	      });



	}
    
    
    
   public CompletionStage<Result> HashTagData(String key) throws TwitterException , InterruptedException ,ExecutionException{
		key="#"+key;
		//System.out.println("key--"+key);
	Twitter tweet = new Twitter(key);
		List<String> res =    new ArrayList<>();

		CompletableFuture<QueryResult> FutureLinks =  (CompletableFuture<QueryResult>) tweet.get();

		FutureLinks.thenAccept( r -> r.getTweets()
				.stream()
				.map(d ->   d.getText())
				.limit(10)
				.forEach(res::add));

		
		return FutureConverters.toJava(ask(helloActor, new SayHello(res), 1000))
               .thenApply(response -> {
            
       		return ok(hashTag.render((List<String>) response));
 	      });

	}
   
   public List<String> get_Data(String key) throws TwitterException, InterruptedException, ExecutionException  {

		Twitter tweet = new Twitter(key);
		List<String> res =    new ArrayList<>();

		CompletableFuture<QueryResult> FutureLinks =  (CompletableFuture<QueryResult>) tweet.get();

		FutureLinks.thenAccept( r -> r.getTweets()
				.stream()
				.map(d ->  d.getText().split("\\s+"))
				.limit(100)
				.flatMap(Arrays::stream)
				.forEach(res::add));

		return res;

	}
   
   public CompletionStage<Result> countTweets(String name) throws TwitterException , InterruptedException ,ExecutionException{
		
		List<String> res = get_Data(name);
		Map<String,Integer> countMap=res
				.stream()				
				.collect(Collectors.toMap(w -> w.toLowerCase(), w -> 1, Integer::sum));
		LinkedHashMap<String,Integer> sortedMap=countMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));

		
		
		return FutureConverters.toJava(ask(CountTweetActor, new counthello(sortedMap), 1000))
              .thenApply(response -> {
           
      		return ok(statistics.render((LinkedHashMap<String,Integer>) response));
	      });

	}
   
   public String TwitterSentiment(String key) throws Exception,TwitterException , InterruptedException ,ExecutionException,TimeoutException{
		key="#"+key;
		//System.out.println("key--"+key);
	Twitter tweet = new Twitter(key);
		List<String> res =    new ArrayList<>();
		String emotion=":-|";
		CompletableFuture<QueryResult> FutureLinks =  (CompletableFuture<QueryResult>) tweet.get();
       long happycount=0;
       long count=0;
		FutureLinks.thenAccept( r -> r.getTweets()
				.stream()
				.map(d ->   d.getText())
				.limit(100)
				.forEach(res::add));

		 Map <Object, Integer> wordCounter = res.stream().
		           collect(Collectors.toMap(w -> {
					try {
						return HappyTweetCount(w.toString());
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return w.toString();
				}, w -> 1, Integer::sum));
		    	
		//    System.out.println("wordcounter ::"+wordCounter.keySet().stream().map(s->(Long)s).mapToLong(Long::intValue).sum());
		    	 happycount=wordCounter.keySet().stream().map(s->(Long)s).mapToLong(Long::intValue).sum();
		    	 
		    	 Map <Object, Integer> wordCounter2 = res.stream().
		    	           collect(Collectors.toMap(w -> {
		    				try {
		    					return SadTweetCount(w.toString());
		    				} catch (UnsupportedEncodingException e) {
		    					// TODO Auto-generated catch block
		    					e.printStackTrace();
		    				}
		    				return w.toString();
		    			}, w -> 1, Integer::sum));
		    	    	
		    	  //  System.out.println("wordcounter ::"+wordCounter2.keySet().stream().map(s->(Long)s).mapToLong(Long::intValue).sum());
		    	    count=wordCounter2.keySet().stream().map(s->(Long)s).mapToLong(Long::intValue).sum();
		    	
		    	
		    	long totalEmoticonCount=count+happycount;
		    	double percentage=0.0;
		    	double sadpercentage=0.0;
		    	if(totalEmoticonCount>0) {
		    		percentage=happycount*100/totalEmoticonCount;
		    		sadpercentage=count*100/totalEmoticonCount;
		    		if(percentage>=70.0) {
		    			emotion= ":-)";
		    		}
		    		else if(sadpercentage>=70.0) {
		    			emotion= ":-(";
		    		}
		    		
		    	}
		    	String result=":-|";
		    	try {
		    	 result = (String) Await.result(ask(sentimentActor, new SentimentHello(emotion), 1000), Duration.create(5,TimeUnit.SECONDS));
		    	}
		    	catch (Exception e) {
	                throw new AssertionError(e);
	            }
		    	return result;
   }
  
  
  
 
public static long HappyTweetCount(String tweets) throws UnsupportedEncodingException {
	String regHappypattern="[\uD83D\uDE00+|\uD83D\uDE01+|\uD83D\uDE02+|\uD83D\uDE03+|\uD83D\uDE04+|\uD83D\uDE05+|\uD83D\uDE06+|\uD83D\uDE42+|\uD83D\uDD23+|\uD83D\uDE0A+|\uD83D\uDE0B+|\uD83D\uDE0D+|\uD83D\uDE0E+|\uD83D\uDD70+|\uD83D\uDE17+|\uD83D\uDE18+|\uD83D\uDE19+|\uD83D\uDE1A+|\u263A+]";
	
		long happycount=0;
		byte[] utf8=tweets.getBytes("UTF-8");
		String newTweet=new String(utf8,"UTF-8");
		
			
			Pattern pattern1=Pattern.compile(regHappypattern);
			
			
			Matcher matcher1=pattern1.matcher(newTweet);
			
			 while(matcher1.find()) {
 			  
				happycount=happycount+1;
				
				  
				
			}
			 
			 System.out.println(happycount);
			return happycount;
	
}

public static long SadTweetCount(String tweets) throws UnsupportedEncodingException {
	String regSadpattern="[\u2639+|\uD83D\uDE41+|\uD83D\uDE16+|\uD83D\uDE1E+|\uD83D\uDE1F+|\uD83D\uDE24+|\uD83D\uDE22+|\uD83D\uDE2D+|\uD83D\uDD26+|\uD83D\uDE27+|\uD83D\uDE28+|\uD83D\uDE29+|\uD83D\uDE2F+|\uD83D\uDD2C+|\uD83D\uDE30+]";
	
		long happycount=0;
		byte[] utf8=tweets.getBytes("UTF-8");
		String newTweet=new String(utf8,"UTF-8");
		
			
			Pattern pattern1=Pattern.compile(regSadpattern);
			
			
			Matcher matcher1=pattern1.matcher(newTweet);
			
			 while(matcher1.find()) {
 			  
				happycount=happycount+1;
				
				  
				
			}
			 
			 System.out.println(happycount);
			return happycount;
	
}
   
   
  /*public scala.concurrent.Future<Object> TwitterSentiment(String key) throws TwitterException , InterruptedException ,ExecutionException{
		key="#"+key;
		//System.out.println("key--"+key);
	Twitter tweet = new Twitter(key);
		List<String> res =    new ArrayList<>();
		String emotion=":-|";
		CompletableFuture<QueryResult> FutureLinks =  (CompletableFuture<QueryResult>) tweet.get();
long happycount=0;
long count=0;
		FutureLinks.thenAccept( r -> r.getTweets()
				.stream()
				.map(d ->   d.getText())
				.limit(100)
				.forEach(res::add));

		 Map <Object, Integer> wordCounter = res.stream().
		           collect(Collectors.toMap(w -> {
					try {
						return HappyTweetCount(w.toString());
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return w.toString();
				}, w -> 1, Integer::sum));
		    	
		//    System.out.println("wordcounter ::"+wordCounter.keySet().stream().map(s->(Long)s).mapToLong(Long::intValue).sum());
		    	 happycount=wordCounter.keySet().stream().map(s->(Long)s).mapToLong(Long::intValue).sum();
		    	 
		    	 Map <Object, Integer> wordCounter2 = res.stream().
		    	           collect(Collectors.toMap(w -> {
		    				try {
		    					return SadTweetCount(w.toString());
		    				} catch (UnsupportedEncodingException e) {
		    					// TODO Auto-generated catch block
		    					e.printStackTrace();
		    				}
		    				return w.toString();
		    			}, w -> 1, Integer::sum));
		    	    	
		    	  //  System.out.println("wordcounter ::"+wordCounter2.keySet().stream().map(s->(Long)s).mapToLong(Long::intValue).sum());
		    	    count=wordCounter2.keySet().stream().map(s->(Long)s).mapToLong(Long::intValue).sum();
		    	
		    	
		    	long totalEmoticonCount=count+happycount;
		    	double percentage=0.0;
		    	double sadpercentage=0.0;
		    	if(totalEmoticonCount>0) {
		    		percentage=happycount*100/totalEmoticonCount;
		    		sadpercentage=count*100/totalEmoticonCount;
		    		if(percentage>=70.0) {
		    			emotion= ":-)";
		    		}
		    		else if(sadpercentage>=70.0) {
		    			emotion= ":-(";
		    		}
		    		
		    	}
		    	
		    	//return ":-|";
		    	ActorSystem system;
		 //  Future<Object> future=akka.pattern.Patterns.pipe(future, system.dispatcher()).to(sentimentActor);
	
		    	return  ask(sentimentActor, new SentimentHello(emotion), 1000);
		    	return FutureConverters.toJava(ask(sentimentActor, new SentimentHello(emotion), 1000))
               .thenApply(response -> {
            
       		return (ok(script.render((String) response)));
 	      });

	}
   
   
   
  
public static long HappyTweetCount(String tweets) throws UnsupportedEncodingException {
	String regHappypattern="[\uD83D\uDE00+|\uD83D\uDE01+|\uD83D\uDE02+|\uD83D\uDE03+|\uD83D\uDE04+|\uD83D\uDE05+|\uD83D\uDE06+|\uD83D\uDE42+|\uD83D\uDD23+|\uD83D\uDE0A+|\uD83D\uDE0B+|\uD83D\uDE0D+|\uD83D\uDE0E+|\uD83D\uDD70+|\uD83D\uDE17+|\uD83D\uDE18+|\uD83D\uDE19+|\uD83D\uDE1A+|\u263A+]";
	
		long happycount=0;
		byte[] utf8=tweets.getBytes("UTF-8");
		String newTweet=new String(utf8,"UTF-8");
		
			
			Pattern pattern1=Pattern.compile(regHappypattern);
			
			
			Matcher matcher1=pattern1.matcher(newTweet);
			
			 while(matcher1.find()) {
  			  
				happycount=happycount+1;
				
				  
				
			}
			 
			 System.out.println(happycount);
			return happycount;
	
}

public static long SadTweetCount(String tweets) throws UnsupportedEncodingException {
	String regSadpattern="[\u2639+|\uD83D\uDE41+|\uD83D\uDE16+|\uD83D\uDE1E+|\uD83D\uDE1F+|\uD83D\uDE24+|\uD83D\uDE22+|\uD83D\uDE2D+|\uD83D\uDD26+|\uD83D\uDE27+|\uD83D\uDE28+|\uD83D\uDE29+|\uD83D\uDE2F+|\uD83D\uDD2C+|\uD83D\uDE30+]";
	
		long happycount=0;
		byte[] utf8=tweets.getBytes("UTF-8");
		String newTweet=new String(utf8,"UTF-8");
		
			
			Pattern pattern1=Pattern.compile(regSadpattern);
			
			
			Matcher matcher1=pattern1.matcher(newTweet);
			
			 while(matcher1.find()) {
  			  
				happycount=happycount+1;
				
				  
				
			}
			 
			 System.out.println(happycount);
			return happycount;
	
}
   */
   
     
    /**
     * This method accepts Web Socket requests and creates the connection.
     * @return Connection to perform the searches using actors.
     */
    public WebSocket socket() {
    	
        return  WebSocket.Json.accept(request ->
                ActorFlow.actorRef(MyWebSocketActor::props,
                        actorSystem, materializer));
    }
    


}