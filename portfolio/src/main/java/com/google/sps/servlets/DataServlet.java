// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import java.util.ArrayList;
import java.util.List;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Servlet that returns some example content.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String commentCount = request.getParameter("count");
    int maxComments = request.getParameter("count") == 
    null ? 0 : Integer.parseInt( request.getParameter("count"));
    Query query = new Query("Shows").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<String> shows = new ArrayList<>();
    int count = 0;
    for (Entity entity : results.asIterable()) {
      if(count < maxComments){
        shows.add(entity.getProperty("show").toString());
      } else{
        break;
      }
      count++; 
    }
    response.setContentType("application/json;");
    response.getWriter().println((new Gson()).toJson(shows));
  }

 @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String textShow = request.getParameter("text-input");
      Entity taskEntity = new Entity("Shows");
      taskEntity.setProperty("show", textShow);
      taskEntity.setProperty("timestamp", System.currentTimeMillis());
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(taskEntity);
      response.sendRedirect("/index.html#quote-container");
  }

}
