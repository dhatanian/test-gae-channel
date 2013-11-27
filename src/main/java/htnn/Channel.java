package htnn;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Channel {
	@Id
	public String token;
	public String channelId;
	@Index
	public long remainingTime;
	
	public Channel(){};
	
	public Channel(String channelId, String token, long remainingTime) {
		this.channelId = channelId;
		this.token = token;
		this.remainingTime = remainingTime;
	}

}
