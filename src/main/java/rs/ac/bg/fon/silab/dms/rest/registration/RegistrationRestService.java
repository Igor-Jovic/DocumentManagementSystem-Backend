package rs.ac.bg.fon.silab.dms.rest.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.silab.dms.core.service.CompanyService;
import rs.ac.bg.fon.silab.dms.rest.registration.dto.RegistrationRequest;
import rs.ac.bg.fon.silab.dms.rest.registration.dto.RegistrationResponse;
import rs.ac.bg.fon.silab.dms.core.model.Company;
import rs.ac.bg.fon.silab.dms.core.model.Role;
import rs.ac.bg.fon.silab.dms.core.model.User;
import rs.ac.bg.fon.silab.dms.core.exception.BadRequestException;
import rs.ac.bg.fon.silab.dms.core.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/registration")
public class RegistrationRestService {

    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    //should we add another mapping layer? maybe a class that Service will use to map objects
    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response register(RegistrationRequest registrationRequest) throws BadRequestException {
        if (!registrationRequest.isValid()) {
            throw new BadRequestException("A problem occured. In order to register you need to provide company name, username and password.");
        }
        User user = userService.createUser(createUserFromRequest(registrationRequest));

        RegistrationResponse registrationResponse = createResponseFromUser(user);
        return Response.ok(registrationResponse).build();
    }

    private User createUserFromRequest(RegistrationRequest registrationRequest) throws BadRequestException {
        Company company = new Company(registrationRequest.companyName);
        company = companyService.createCompany(company);
        return new User(registrationRequest.username, registrationRequest.password, Role.ADMIN, company);
    }

    private RegistrationResponse createResponseFromUser(User user) {
        return new RegistrationResponse(user.getUsername(), user.getRole().toString(), user.getCompany().getName());
    }
}
