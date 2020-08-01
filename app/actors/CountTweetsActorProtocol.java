package actors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CountTweetsActorProtocol {
	
	public static class counthello{
		public final LinkedHashMap<String, Integer> name;
		
		public counthello(LinkedHashMap<String, Integer> name) {
			this.name=name;
		}
	}

}
