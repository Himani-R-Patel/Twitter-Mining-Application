package actors;

import java.util.List;

public class LocationActorProtocol {
	
	public static class locationHello{
		public final List<String> name;
		
		public locationHello(List<String> res) {
			this.name=res;
		}
	}

}
