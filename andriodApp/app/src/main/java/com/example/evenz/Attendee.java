package com.example.evenz;

import java.util.ArrayList;



public class Attendee extends User
    {
        private Geolocation geolocation;
        private boolean notifications;
        private ArrayList<String> eventList;

            public Attendee(String name, String profilePicID, String phone, String email) {
                this(name, profilePicID, phone, email, null, false, new ArrayList<>());
            }

            public Attendee(String name, String profilePicID, String phone, String email, Geolocation geolocation, boolean notifications, ArrayList<String> eventList) {
                super(name, profilePicID, phone, email);
                this.geolocation = geolocation;
                this.notifications = notifications;
                this.eventList = eventList;
            }

            // existing methods...


        public Geolocation getGeolocation() {
            return geolocation;
        }

        public void setGeolocation(Geolocation geolocation) {
            this.geolocation = geolocation;
        }

        public boolean isNotifications() {
            return notifications;
        }

        public void setNotifications(boolean notifications) {
            this.notifications = notifications;
        }

        public ArrayList<String> getEventList() {
            return eventList;
        }

        public void setEventList(ArrayList<String> eventList) {
            this.eventList = eventList;
        }

        public void addEvent(String eventID) {
            eventList.add(eventID);
        }

        public void removeEvent(String eventID) {
            eventList.remove(eventID);
        }

        public void clearEventList() {
            eventList.clear();
        }



    }
