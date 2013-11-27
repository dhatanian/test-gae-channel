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

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class ChannelServlet extends HttpServlet {

	private static final long serialVersionUID = -5794696486715813626L;
	private ChannelService channelService = ChannelServiceFactory.getChannelService();
	private StorageManager<Channel> channelManager = new StorageManager<Channel>(Channel.class);
	private Queue publicationQueue = QueueFactory.getQueue("publication");
	
	@Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp)
	      throws IOException {
	
		String channelId = UUID.randomUUID().toString();
		String token = channelService.createChannel(channelId,24*60);
		long remainingTime = System.currentTimeMillis()+23*60*60*1000;
	    Channel channel = new Channel(channelId, token, remainingTime);
	    channelManager.save(channel);
	    resp.setContentType("text/plain");
	    resp.getWriter().print(token);
	    
	  }
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String message = req.getParameter("message");
		publicationQueue.add(TaskOptions.Builder.withMethod(Method.POST).url("/publish").param("message", message));
		resp.sendRedirect("/command.html");
	}
}
