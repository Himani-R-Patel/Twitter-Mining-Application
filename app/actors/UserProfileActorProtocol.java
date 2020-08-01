package actors;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UserProfileActorProtocol {
	
	public static class profileHello{
		public final HashMap<String, List<String>> name;
		
		public profileHello(HashMap<String, List<String>> name) {
			this.name=name;
		}
	}

}
