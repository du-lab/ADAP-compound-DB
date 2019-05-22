package org.dulab.adapcompounddb.site.services;

import java.util.Optional;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.repositories.UserPrincipalRepository;
import org.hibernate.Hibernate;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.swing.*;

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
    public void changePassword(String username, String oldpass, String newpass) {
        UserPrincipal principal = userPrincipalRepository.findUserPrincipalByUsername(username).orElse(null);
        if(BCrypt.checkpw(oldpass, principal.getHashedPassword())){                     // check if the old password is equal to current password
            // check if the new password is the same as old password
            if(BCrypt.checkpw(newpass, principal.getHashedPassword())) {                // then check if the new password is the same as current password,
                String msg = "The new password cannot be the same as old password!!";   // alert user and do not change the password.
                msgbox(msg);
            } else {                                                              // then check if the new password is different from the current password,
                String salt = BCrypt.gensalt(HASHING_LOG_ROUNDS);                 // update new password as current password and alert update successfully.
                principal.setHashedPassword(BCrypt.hashpw(newpass, salt));
                userPrincipalRepository.save(principal);
                String msg = "Changing password successfully";
                msgbox(msg);
            }
        }
        else{
            // did not change the password successfully, notice user about the information
            String msg = "Changing password failed!";
            msgbox(msg);
        }
    }

    @Override
    @Transactional
    public Optional<UserPrincipal> findUser(long id) {
        return userPrincipalRepository.findById(id);
    }

    // display message on screen!
    public void msgbox(String s){
        JOptionPane.showMessageDialog(null,s);
    }

}
