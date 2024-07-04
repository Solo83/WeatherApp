package com.solo83.weatherapp.service;

import com.solo83.weatherapp.entity.UserSession;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionPersistanceService {



    private static SessionPersistanceService INSTANCE;

        private SessionPersistanceService() {
        }

        public static SessionPersistanceService getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new SessionPersistanceService();
            }
            return INSTANCE;
        }

    @Getter
    public static final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public UserSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void addSession(UserSession session) {
        sessions.put(session.getId(), session);
    }
}
