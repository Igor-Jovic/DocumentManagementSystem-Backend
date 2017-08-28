package rs.ac.bg.fon.silab.dms.core.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.silab.dms.core.exception.DMSErrorException;
import rs.ac.bg.fon.silab.dms.core.model.User;
import rs.ac.bg.fon.silab.dms.core.repository.UserRepository;

import static org.springframework.util.StringUtils.isEmpty;
import rs.ac.bg.fon.silab.dms.core.model.UserES;
import rs.ac.bg.fon.silab.dms.core.repository.es.UserESRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected CompanyService companyService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserESRepository userESRepository;

    UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, CompanyService companyService, UserESRepository userESRepository) {
        this.encoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.userESRepository = userESRepository;
    }

    @Transactional
    public User createUserWithNewCompany(User user) throws DMSErrorException {
        validateUser(user);
        companyService.createCompany(user.getCompany());
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    @Transactional
    public User createUser(User user) throws DMSErrorException {
        validateUser(user);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.saveAndFlush(user);
        userESRepository.save(new UserES(user));
        return user;
    }

    void validateUser(User user) throws DMSErrorException {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new DMSErrorException("User with given username already exists.");
        }
        if (isEmpty(user.getPassword()) || user.getRole() == null || user.getCompany() == null) {
            throw new IllegalStateException("User is in a bad state.");
        }
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserES> getByUsernameLike(String username, Pageable page) {
        return userESRepository.findByUsernameLike(username, page).getContent();
    }

    public List<UserES> getByCompanyNameLike(String companyName, Pageable page) {
        return userESRepository.findByCompanyNameLike(companyName, page).getContent();
    }
}
