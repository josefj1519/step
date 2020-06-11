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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.maps.model.LatLng;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/markers")
public class MarkerServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Collection<LatLng> markers = getMarkers();
    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(markers));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    storeMarker(new LatLng(Double.parseDouble(request.getParameter("lat")), Double.parseDouble(request.getParameter("lng"))));
  }

  /** Fetches markers from Datastore. */
  private Collection<LatLng> getMarkers() {
    Collection<LatLng> markers = new ArrayList<>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Marker");
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      markers.add(new LatLng((double) entity.getProperty("lat"), (double) entity.getProperty("lng")));
    }
    return markers;
  }

  /** Stores a marker in Datastore. */
  public void storeMarker(LatLng marker) {
    Entity markerEntity = new Entity("Marker");
    markerEntity.setProperty("lat", marker.lat);
    markerEntity.setProperty("lng", marker.lng);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(markerEntity);
  }
}
