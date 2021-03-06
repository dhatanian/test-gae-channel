/**
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package htnn;

import java.util.List;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PublishServlet extends HttpServlet {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -424919766861090390L;
	private ChannelService channelService = ChannelServiceFactory.getChannelService();
	private StorageManager<Channel> channelManager = new StorageManager<Channel>(Channel.class);
	private Queue publicationQueue = QueueFactory.getQueue("publication");
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp){
		String message = req.getParameter("message");
		String destination = req.getParameter("destination");
		if(destination==null){
			List<Channel> channels = channelManager.getAll();
			for(Channel channel : channels){
				publicationQueue.add(TaskOptions.Builder.withMethod(Method.POST).url("/publish").param("message", message).param("destination", channel.token));
			}
		}else{
			Channel channel = channelManager.get(destination);
			if(channel==null){
				System.err.println("No channel found with id "+destination);
			}else{
				channelService.sendMessage(new ChannelMessage(channel.channelId, message));
			}
		}
		
	}
}
