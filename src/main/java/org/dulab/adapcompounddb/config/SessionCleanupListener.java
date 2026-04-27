package org.dulab.adapcompounddb.config;

import org.dulab.adapcompounddb.site.repositories.TempSpectrumMatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
public class SessionCleanupListener implements HttpSessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionCleanupListener.class);

    private static TempSpectrumMatchRepository tempSpectrumMatchRepository;

    @Autowired
    public void setTempSpectrumMatchRepository(TempSpectrumMatchRepository repo) {
        SessionCleanupListener.tempSpectrumMatchRepository = repo;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        try {
            if (tempSpectrumMatchRepository != null) {
                tempSpectrumMatchRepository.deleteBySessionId(sessionId);
                LOGGER.info("Cleaned up temp matches for expired session " + sessionId);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to clean up temp matches for session " + sessionId + ": " + e.getMessage());
        }
    }
}
