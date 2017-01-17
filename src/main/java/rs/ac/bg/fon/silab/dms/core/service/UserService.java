package rs.ac.bg.fon.silab.dms.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.silab.dms.core.model.Company;
import rs.ac.bg.fon.silab.dms.core.model.Role;
import rs.ac.bg.fon.silab.dms.core.model.User;
import rs.ac.bg.fon.silab.dms.core.repository.UserRepository;
import rs.ac.bg.fon.silab.dms.core.exception.BadRequestException;
import rs.ac.bg.fon.silab.dms.security.TokenAuthenticationService;
import rs.ac.bg.fon.silab.dms.util.StringUtils;

import static rs.ac.bg.fon.silab.dms.util.StringUtils.isStringEmptyOrNull;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected CompanyService companyService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, CompanyService companyService) {
        this.encoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    @Transactional
    public User createUserWithNewCompany(User user) throws BadRequestException {
        validateUser(user);
        companyService.createCompany(user.getCompany());
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    @Transactional
    public User createUser(User user) throws BadRequestException {
        validateUser(user);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    void validateUser(User user) throws BadRequestException {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new BadRequestException("User with given username already exists.");
        }
        if (isStringEmptyOrNull(user.getPassword()) || user.getRole() == null || user.getCompany() == null) {
            throw new IllegalStateException("User is in a bad state.");
        }
    }

    public User getAuthenticatedUser(String token) {
        String authenticatedUserName = tokenAuthenticationService.getAuthenticationData(token)
                .getAuthentication().getName();
        User authenticatedUser = getUser(authenticatedUserName);
        return authenticatedUser;
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(User existingUser) {
        userRepository.delete(existingUser.getId());
    }
}
