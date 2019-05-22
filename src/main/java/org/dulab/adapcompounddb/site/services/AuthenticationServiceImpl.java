package org.dulab.adapcompounddb.site.services;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.repositories.UserPrincipalRepository;
import org.hibernate.Hibernate;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOG = LogManager.getLogger();

    private static final int HASHING_LOG_ROUNDS = 10;

    private final UserPrincipalRepository userPrincipalRepository;

    @Autowired
    public AuthenticationServiceImpl(UserPrincipalRepository userPrincipalRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
    }

    @Override
    @Transactional
    public UserPrincipal authenticate(String username, String password) {

        UserPrincipal principal = userPrincipalRepository.findUserPrincipalByUsername(username).orElse(null);

        if (principal == null) {
            LOG.warn("Authentication failed for non-existent user {}.", username);
            return null;
        }

        if (!BCrypt.checkpw(password, principal.getHashedPassword())) {
            LOG.warn("Authentication failed for user {}.", username);
            return null;
        }

        LOG.debug("User {} successfully authenticated.", username);

        return (UserPrincipal) Hibernate.unproxy(principal);
    }

    @Override
    @Transactional
    public void saveUser(UserPrincipal principal, String password) {
        LOG.info("Registering a new user...");
        if (password != null && password.length() > 0) {
            LOG.info("Generating a salt...");
            String salt = BCrypt.gensalt(HASHING_LOG_ROUNDS);
            LOG.info("Hashing the password with the generated salt...");
            principal.setHashedPassword(BCrypt.hashpw(password, salt));
        }
        LOG.info("Assigning default role...");
        principal.assignDefaultRole();
        LOG.info("Saving the user...");
        userPrincipalRepository.save(principal);
        LOG.info("Registering is completed.");
    }

    @Override
    public void changePassword(String username, String oldpass, String newpass)
            throws IllegalStateException {

        UserPrincipal principal = userPrincipalRepository.findUserPrincipalByUsername(username).orElse(null);
        if (BCrypt.checkpw(oldpass, principal.getHashedPassword())) {                     // check if the old password is equal to current password
            // check if the new password is the same as old password
            if (BCrypt.checkpw(newpass, principal.getHashedPassword())) {                // then check if the new password is the same as current password,
                throw new IllegalStateException("The new password cannot be the same as old password!!");
            } else {
                String salt = BCrypt.gensalt(HASHING_LOG_ROUNDS);                 // update new password as current password and alert update successfully.
                principal.setHashedPassword(BCrypt.hashpw(newpass, salt));
                userPrincipalRepository.save(principal);
            }
        } else {
            // the old password does not match the current password
            throw new IllegalStateException("The old password does not match your current password, Please try again!");
        }
    }

    @Override
    @Transactional
    public Optional<UserPrincipal> findUser(long id) {
        return userPrincipalRepository.findById(id);
    }


}
