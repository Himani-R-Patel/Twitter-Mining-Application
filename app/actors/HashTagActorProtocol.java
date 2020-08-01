package actors;

import java.util.List;

public class HashTagActorProtocol {
	
	public static class SayHello{
		public final List<String> name;
		
		public SayHello(List<String> res) {
			this.name=res;
		}
	}

}
