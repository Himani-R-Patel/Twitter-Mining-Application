import static org.junit.Assert.*;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import akka.testkit.javadsl.TestKit;
import twitter.*;
import akka.actor.ActorSystem;
import controllers.HomeController;
import play.api.test.Helpers;
import play.mvc.Result;
import play.twirl.api.Content;
import twitter4j.TwitterException;
import static org.awaitility.Awaitility.await;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import static org.mockito.Mockito.*;
import akka.actor.ActorSystem;
import controllers.AsyncController;
import controllers.CountController;
import controllers.HomeController;
import org.junit.Test;
import java.util.*;
//import Twitter.TwitterCustom;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;
import scala.concurrent.ExecutionContextExecutor;
import twitter4j.User;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.io.UnsupportedEncodingException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static play.test.Helpers.contentAsString;

import twitter4j.conf.ConfigurationBuilder;
import twitter4j.* ;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
//import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
//import static org.hamcrest.CoreMatchers.*;
import org.mockito.*;
import twitter4j.ResponseList;
import org.junit.Test;

import play.mvc.Result;
import play.twirl.api.Content;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
public class ProfileViewsTesting   {

	/**
	  * @author Misbahuddin Adil Syed
	  */
   static ActorSystem system;
   public static play.mvc.Http.Request request ;
   
   /**
    * Creates an Actor System
    */
   @BeforeClass
   public static void setup() {
       system = ActorSystem.create();
   }
   /**
    * Shuts down the created Actor System.
    */
   @AfterClass
   public static void teardown() {
       TestKit.shutdownActorSystem(system);
       system = null;
   }
  
   
  // assertThat(service.addCustomer(customer), is(notNullValue()));
  /* @Test 
   public  void testprofile () throws TwitterException, InterruptedException ,ExecutionException
   {

	   Result result = new HomeController(system).profile("@marnuell");
	   assertEquals(OK, result.status());
	   assertEquals("text/html", result.contentType().get());
	   assertEquals("utf-8", result.charset().get());
	   assertThat(contentAsString(result).contains("@marnuell"));
   }*/
   
  /* @Test
   public void TestProfile() throws Exception,TwitterException , InterruptedException ,ExecutionException,UnsupportedEncodingException{
     //  final ActorSystem actorSystem = ActorSystem.create("test");
       try {
         //  final ExecutionContextExecutor ec = actorSystem.dispatcher();
           final HomeController controller = new HomeController(system);
           final CompletionStage<Result> future = controller.profile("@Anusha80912301");

           // Block until the result is completed
           await().until(() -> {
               assertThat(future.toCompletableFuture()).isCompletedWithValueMatching(result -> {
                   return contentAsString(result).equals("Hi!");
               });
           });
       } finally {
           
       }
   }*/
	   
}
