package twitter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.* ;
/**
 * This class contains the Twitter API which is called by the HomeController whenever a Search is made on an Application.
 * @author Sanghavi Kartigayan
 *
 */
public class Twitter {
	String data;
	
	public ConfigurationBuilder ConfigurationBuilderAccessor() throws TwitterException{
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		return cb.setDebugEnabled(true)
				.setOAuthConsumerKey("oNTa99N1ou44xEULzXoMcEGlP")
				.setOAuthConsumerSecret("DFTQPEv1IJHLvCl3I0IfzivlY7icVGIjLNovjufSc4HUiB4O5W")
				.setOAuthAccessToken("1021553562734739461-o3WvGmye7dbCtKuWbGpivOuD316nnx")
				.setOAuthAccessTokenSecret("tpdlEyJ7iPsZUC5ibzIcNY8w5LOcc80APiL7Q3rcrlbg4");
		/*return cb.setDebugEnabled(true)
				.setOAuthConsumerKey("iuh1dBIa8bXvOjBSRLpIF7e40")
				.setOAuthConsumerSecret("GAupJH5iFkWycp9r72dN44Tvd0pO14Tkoi4WSsoMt8dSN4GB3E")
				.setOAuthAccessToken("972273228046569473-4GTgsikGCKHXse3RxzTwqSUk23cEhe8")
				.setOAuthAccessTokenSecret("xctHGp1WG295EARuD7uWKuWJuAI9hgPxmDI0IxmK0ZtAI");*/
		
	}
	/**
	 * This method constructs the keyword that is passed for searching in our application.
	 * @param SearchString The keyword on which a search has to be made.
	 */

	public Twitter(String SearchString) {
		data = SearchString;
	}
		
	/**
	 * This method is called when a HomeController class performs a search on any word.
	 * The API will return the results in the CompletionStage form stored in an array which is later truncated in HomeController class.
	 * @return The tweets that matches that search.
	 * @throws TwitterException An exception class that will be thrown when TwitterAPI calls are failed. In case the Twitter server returned HTTP error code, you can get the HTTP status code using getStatusCode() method.
	 */
	@SuppressWarnings("unchecked")
	public CompletionStage<QueryResult> get() throws TwitterException  {
		// TODO Auto-generated method stub
		CompletableFuture<QueryResult> futureResult = new CompletableFuture<>();
		ConfigurationBuilder cb = ConfigurationBuilderAccessor();
		      

		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = tf.getInstance();
		Query query = new Query(data).resultType(Query.RECENT);
	  //  query.setCount(10);
		return futureResult.completedFuture(twitter.search(query));
		    
	}
	
	@SuppressWarnings("unchecked")
	public CompletionStage<QueryResult> getSenti() throws TwitterException  {
		// TODO Auto-generated method stub
		CompletableFuture<QueryResult> futureResult = new CompletableFuture<>();
		ConfigurationBuilder cb = new ConfigurationBuilder();
		      
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("oNTa99N1ou44xEULzXoMcEGlP")
		.setOAuthConsumerSecret("DFTQPEv1IJHLvCl3I0IfzivlY7icVGIjLNovjufSc4HUiB4O5W")
		.setOAuthAccessToken("1021553562734739461-o3WvGmye7dbCtKuWbGpivOuD316nnx")
		.setOAuthAccessTokenSecret("tpdlEyJ7iPsZUC5ibzIcNY8w5LOcc80APiL7Q3rcrlbg4");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = tf.getInstance();
		Query query = new Query(data).resultType(Query.RECENT);
	  //  query.setCount(10);
		return futureResult.completedFuture(twitter.search(query));
		    
	}
	
	@SuppressWarnings("unchecked")
	public Status getLatestTweet() throws TwitterException  {
		// TODO Auto-generated method stub
		CompletableFuture<QueryResult> futureResult = new CompletableFuture<>();
		ConfigurationBuilder cb = ConfigurationBuilderAccessor();
		      

		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = tf.getInstance();
		Query query = new Query(data).resultType(Query.RECENT);
	   
		return twitter.search(query).getTweets().get(0);
		    
	}
	/**
	 * This method is called by the HomeContrroller when a Search is made on the Person who has tweeted for that particular keyword.
	 * @return The Person's basic TimeLine information such as the Followers count,Friends Count, Location, Description and the ScreenNmae in a CompletionStage form.
	 * @throws TwitterException An exception class that will be thrown when TwitterAPI calls are failed. In case the Twitter server returned HTTP error code, you can get the HTTP status code using getStatusCode() method.
	 */
	public CompletionStage<String>  getProfile() throws TwitterException  {
		// TODO Auto-generated method stub
		
		CompletableFuture<String> futureProfile = new CompletableFuture<>();
		ConfigurationBuilder cb = ConfigurationBuilderAccessor();
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = tf.getInstance();
	    
		User user = twitter.showUser(data);
	   return	futureProfile.completedFuture(user.getFollowersCount() + " "+ user.getFriendsCount() + " "+ user.getLocation() + " "+ user.getDescription() + " "+ user.getScreenName());		 			    
		    
	}
		/**
	 * This method is called to retrieve the person's profile details that contains all the tweets that he has made on the application so far.
	 * @return The tweet details of the person in a Completion Stage form.
	 * @throws TwitterException An exception class that will be thrown when TwitterAPI calls are failed. In case the Twitter server returned HTTP error code, you can get the HTTP status code using getStatusCode() method.
	 */
	public CompletableFuture<List<Status>> getDetails() throws TwitterException{
		//ConfigurationBuilder cb = new ConfigurationBuilder();
		CompletableFuture<List<Status>> futureStatus = new CompletableFuture<>();

		ConfigurationBuilder cb = ConfigurationBuilderAccessor();
		TwitterFactory tf = new TwitterFactory(cb.build());


				twitter4j.Twitter twitter = tf.getInstance();
				futureStatus.complete(twitter.getUserTimeline(data));
		

		return futureStatus;

	}

	@SuppressWarnings("unchecked")
	public CompletionStage<QueryResult> getLocationTweets() throws TwitterException   {
		// TODO Auto-generated method stub
		CompletableFuture<QueryResult> futureResult = new CompletableFuture<>();
		ConfigurationBuilder cb = ConfigurationBuilderAccessor();


		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = tf.getInstance();
		//if(data !=null || !data.isEmpty()) {
		String latitude= data==null?"":data;
		double lat=Double.parseDouble(latitude.split(",")[0]);
		double longi=Double.parseDouble(latitude.split(",")[1]);
		//System.out.println("Is it going to LocationTweets??");
		Query query = new Query();
		query.geoCode(new GeoLocation(lat, longi), 1000.0, "mi");

		//System.out.println("Is it working fine??"+twitter.search(query));
		return futureResult.completedFuture(twitter.search(query));



	}
}
