package org.dulab.site.authentication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.site.models.UserPrincipal;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class DefaultAuthenticationService implements AuthenticationService {

    private static final Logger LOG = LogManager.getLogger();
    private static final SecureRandom RANDOM;
    private static final int HASHING_ROUNDS = 10;

    static {
        try {
            RANDOM = SecureRandom.getInstanceStrong();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private UserPrincipalRepository userPrincipalRepository;

    public DefaultAuthenticationService() {
        userPrincipalRepository = new DefaultUserPrincipalRepository();
    }

    @Override
    @Transactional
    public UserPrincipal authenticate(String username, String password) {

        UserPrincipal principal = userPrincipalRepository.getByUsername(username);
        if (principal == null) {
            LOG.warn("Authentication failed for non-existent user {}.", username);
            return null;
        }

        if (!BCrypt.checkpw(password, new String(principal.getHashedPassword(), StandardCharsets.UTF_8))) {
            LOG.warn("Authentication failed for user {}.", username);
            return null;
        }

        LOG.debug("User {} successfully authenticated.", username);

        return principal;
    }

    @Override
    @Transactional
    public void saveUser(UserPrincipal principal, String password) {
        if (password != null && password.length() > 0) {
            String salt = BCrypt.gensalt(HASHING_ROUNDS, RANDOM);
            principal.setHashedPassword(BCrypt.hashpw(password, salt).getBytes());
        }

        if (principal.getId() < 1)
            userPrincipalRepository.add(principal);
        else
            userPrincipalRepository.update(principal);
    }
}
