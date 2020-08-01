package actors;

import java.util.List;

public class SentimentActorProtocol {
	
	public static class SentimentHello{
		public final String name;
		
		public SentimentHello(String res) {
			this.name=res;
		}
	}

}
