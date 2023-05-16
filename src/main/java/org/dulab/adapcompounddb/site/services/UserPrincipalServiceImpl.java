package org.dulab.adapcompounddb.site.services;

import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import org.apache.http.client.utils.URIBuilder;
import org.dulab.adapcompounddb.models.dto.SearchParametersDTO;
import org.dulab.adapcompounddb.models.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.entities.UserParameter;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.repositories.UserParameterRepository;
import org.dulab.adapcompounddb.models.UserParameterType;
import org.dulab.adapcompounddb.site.repositories.UserPrincipalRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;

@Service
public class UserPrincipalServiceImpl implements UserPrincipalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPrincipalServiceImpl.class);

    private final UserPrincipalRepository userPrincipalRepository;
    private final UserParameterRepository userParameterRepository;

    private final EmailService emailService;

    @Value("${INTEGRATION_TEST}")
    private boolean INTEGRATION_TEST;

    @Autowired
    public UserPrincipalServiceImpl(UserPrincipalRepository userPrincipalRepository,
                                    UserParameterRepository userParameterRepository,
                                    EmailService emailService) {
        this.userPrincipalRepository = userPrincipalRepository;
        this.userParameterRepository = userParameterRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void saveUserPrincipal(UserPrincipal userPrincipal) {
        userPrincipalRepository.save(userPrincipal);
    }

    @Override
    @Transactional
    public UserParameter findParameter(UserPrincipal userPrincipal, String parameter) {
        return userParameterRepository
                .findByUserPrincipalIdAndIdentifier(userPrincipal.getId(), parameter)
                .orElse(userParameterRepository
                        .findByUserPrincipalIdIsNullAndIdentifier(parameter)
                        .orElseThrow(EmptySearchResultException::new));
    }

    @Override
    @Transactional
    public UserParameter findDefaultParameter(String parameter) {
        return userParameterRepository
                .findByUserPrincipalIdIsNullAndIdentifier(parameter)
                .orElseThrow(EmptySearchResultException::new);
    }

    @Override
    @Transactional
    public void saveDefaultParameter(String parameter, UserParameterType type, Object object) {

        if (object == null) {
            LOGGER.warn("Attempt to save null-parameter with identifier " + parameter);
            return;
        }

        UserParameter userParameter = userParameterRepository
                .findByUserPrincipalIdIsNullAndIdentifier(parameter)
                .orElse(new UserParameter());

        if (object.equals(userParameter.getObject())) return;

        userParameter.setUserPrincipalId(null);
        userParameter.setIdentifier(parameter);
        userParameter.setType(type);
        userParameter.setValue(type.toString(object));

        userParameterRepository.save(userParameter);
    }

    @Override
    @Transactional
    public void saveParameter(UserPrincipal userPrincipal, String parameter, UserParameterType type, Object object) {

        if (object == null) {
            LOGGER.warn("Attempt to save null-parameter with identifier " + parameter);
            return;
        }

        UserParameter userParameter = userParameterRepository
                .findByUserPrincipalIdAndIdentifier(userPrincipal.getId(), parameter)
                .orElse(new UserParameter());

        if (object.equals(userParameter.getObject())) return;

        userParameter.setUserPrincipalId(userPrincipal.getId());
        userParameter.setIdentifier(parameter);
        userParameter.setType(type);
        userParameter.setValue(type.toString(object));

        userParameterRepository.save(userParameter);
    }

    @Override
    public UserPrincipal findUserByUsername(String username) {
        return userPrincipalRepository.findUserPrincipalWithRolesByUsername(username)
                .orElse(null);

//        UserPrincipal userPrincipal = null;
//        Optional<UserPrincipal> userPrincipalFound = userPrincipalRepository.findUserPrincipalWithRolesByUsername(username);
//        if(userPrincipalFound.isPresent()) {
//            userPrincipal = userPrincipalFound.get();
//        }
//        return userPrincipal;
    }

    @Override
    public List<UserPrincipal> findAllUsers() {
        return MappingUtils.toList(userPrincipalRepository.findAll());
    }


    @Override
    public void delete(long id) {
        userPrincipalRepository.deleteById(id);
    }

    @Override
    public UserPrincipal findByUserEmail(String email) {
        return userPrincipalRepository.findByemail(email);
    }

    @Override
    public UserPrincipal findByUserEmailOrUsername(String input) {
        return userPrincipalRepository.findByEmailOrUsername(input, input);
    }

    @Override
    public UserPrincipal findByOrganizationToken(String token) {
        return userPrincipalRepository.findByOrganizationRequestToken(token);
    }

    @Override
    public UserPrincipal findByPasswordToken(String token) {
        return userPrincipalRepository.findBypasswordResetToken(token);
    }

    @Override
    public SearchParametersDTO updateSearchParameters(SearchParametersDTO searchParameters, UserPrincipal user) {
        SearchParametersDTO currentSearchParameters = user.getSearchParametersDTO();
        if (currentSearchParameters == null || !currentSearchParameters.equals(searchParameters)) {
            user.setSearchParameters(searchParameters);
            saveUserPrincipal(user);
        }
        return searchParameters;
    }

    @Override
    public UserPrincipal addUserToOrganization(final UserPrincipal currentUser, final List<Long> userId) {
        userPrincipalRepository.addUsersToOrganization(currentUser.getId(), userId);
        return findUserByUsername(currentUser.getUsername());
    }

    @Override
    public UserPrincipal deleteUserFromOrganization(String username, UserPrincipal currentUser) {
        UserPrincipal newMember = findUserByUsername(username);
        if (newMember != null) {
            if (newMember.isOrganization()) {
                throw new IllegalStateException("Cannot add an organization account. Please try some other user.");
            }
            newMember.setOrganizationId(null);
            userPrincipalRepository.save(newMember);
        } else {
            throw new EmptySearchResultException("User not found. Please check username and try again.");
        }
        List<UserPrincipal> members = currentUser.getMembers().stream()
                .filter(c -> !c.getUsername().equals(username))
                .collect(Collectors.toList());
        currentUser.setMembers(members);
        return currentUser;
    }

    @Override
    public List<UserPrincipal> fetchUsernamesForOrganization(String input, UserPrincipal user) {
        UserPrincipal fetchedUser = userPrincipalRepository.findByEmailOrUsername(input, input);
        List<UserPrincipal> userPrincipalList = new ArrayList<>();
        // keeping this to search by username in the future
        if (fetchedUser != null) {
            userPrincipalList.add(fetchedUser);
            userPrincipalList = userPrincipalList.stream()
                    .filter(c -> !c.isOrganization() && c.getOrganizationId() == null)
                    .collect(Collectors.toList());
            userPrincipalList.sort((o1, o2) -> {
                if (o1.getOrganizationId() == null && o2.getOrganizationId() == null)
                    return 0;
                if (o1.getOrganizationId() == null)
                    return -1;
                return 1;
            });
        }
        return userPrincipalList;
    }

    @Override
    public void sendInviteToUser(UserPrincipal orgUser, List<Long> selectedUsers) throws Exception {
        Iterator<UserPrincipal> userPrincipleList = userPrincipalRepository.findAllById(selectedUsers).iterator();
        while (userPrincipleList.hasNext()) {
            try {
                UserPrincipal user = userPrincipleList.next();
                // For test environment for automation testing, INTEGRATION_TEST is true, and we store username as token
                String inviteToken = INTEGRATION_TEST ? user.getUsername() : UUID.randomUUID().toString();
                Date expirationDate = new Date(System.currentTimeMillis() + 3600000);//1 hour expiration time
                user.setOrganizationRequestToken(inviteToken);
                user.setOrganizationRequestExpirationDate(expirationDate);
                saveUserPrincipal(user);

                URIBuilder uriBuilder = new URIBuilder()
                        .setScheme("https")
                        .setHost("adap.cloud")
                        .setPath("/organization/addUser")
                        .addParameter("token", inviteToken)
                        .addParameter("orgEmail", orgUser.getEmail());
                String url = uriBuilder.build().toString();

                // Disabling email service in test server for automation testing
                if (!INTEGRATION_TEST)
                    emailService.sendOrganizationInviteEmail(user, orgUser, url);
                LOGGER.info("An invite was sent to " + user.getEmail());
            } catch (Exception e) {
                LOGGER.warn( e.getMessage(), e);
                throw new Exception("Couldn't send invite");
            }
        }
    }
}
