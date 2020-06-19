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

package com.google.sps;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.disjoint;
import static java.util.Collections.sort;
import static java.util.Arrays.asList;

import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;


// FindMeetingQuery finds open meeting timeslots throughout the day.
public final class FindMeetingQuery {
  /** 
  *  @param events Set of events that attendees have, that need to be avoided.
  *  @param request The duration of requested meeting and people attending. 
  *  @return A list of open meeting timeslots.  
  */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
      List<Event> eventsList = new ArrayList<>(events);
      sort(eventsList, new Comparator<Event>() {
        public int compare (Event e1, Event e2) {
            return TimeRange.ORDER_BY_START.compare(e1.getWhen(), e2.getWhen());
          }
        });
      // We want to remove any attendees not attending or events less-than/equal to 0. 
      eventsList.removeIf(e -> (
          e.getWhen().duration() <= 0 || disjoint(e.getAttendees(), request.getAttendees())));
    
      if( request.getAttendees().isEmpty()){
          return asList(TimeRange.WHOLE_DAY);
      }
      if(request.getDuration() > TimeRange.WHOLE_DAY.duration()){
          return EMPTY_LIST;
      }
      if(eventsList.size()==0){
          return asList(TimeRange.WHOLE_DAY);
      }
      if(eventsList.size()==1){
          return asList(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, eventsList.get(0).getWhen().start(), false),
          TimeRange.fromStartEnd(eventsList.get(0).getWhen().end(), TimeRange.END_OF_DAY, true)); 
       }

      return getOpenTimeSlots(eventsList, request);
  }
  /**
  *  This method gets the open time slots by looping through a list of events
  *  given that the list is sorted by start time. 
  *  @param eventsList list of events in sorted order (by start time).
  *  @param request he duration of requested meeting and people attending.
  *  @return A list of open meeting timeslots. 
  */
  private Collection<TimeRange> getOpenTimeSlots(List<Event> eventsList, MeetingRequest request){
      Collection<TimeRange> queryResult = new ArrayList<>();
      for(int i=0;i<eventsList.size();i++){
         
        // First event in the list, check if there is enough time between event start and the start of the day. 
        //  If there is enough time, add to the result. Since the events are sorted by start time we can check the
        //  first index.
        
        if(i==0){
            if(eventsList.get(i).getWhen().start()-request.getDuration()>=TimeRange.START_OF_DAY){
              queryResult.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY,eventsList.get(i).getWhen().start(), false));
            }
        }
        if(i != eventsList.size()-1){
          if(eventsList.get(i).getWhen().overlaps(eventsList.get(i+1).getWhen())){
            // Check for overlap.  Don't need to check for the reverse, since it is pre-sorted. 
            if(eventsList.get(i).getWhen().contains(eventsList.get(i+1).getWhen())){
                //  If one event contains another event. Set i+1 to i.  
                // This is to get the earliest start and latest end time for an event.
                 eventsList.set(i+1, eventsList.get(i));
            }   
        //  Check if there is enough time between two events. 
        } else if(eventsList.get(i+1).getWhen().start()-eventsList.get(i).getWhen().end()>=request.getDuration()){
            queryResult.add(TimeRange.fromStartEnd(eventsList.get(i).getWhen().end(),eventsList.get(i+1).getWhen().start(), false));
        } 
        // For last element in event list check if there is enough time between event end and end of day 
       } else if (eventsList.get(i).getWhen().end()+request.getDuration()<= TimeRange.END_OF_DAY){
           queryResult.add(TimeRange.fromStartEnd(eventsList.get(i).getWhen().end(), TimeRange.END_OF_DAY, true));
       }
      }
      return queryResult;
  }
}
