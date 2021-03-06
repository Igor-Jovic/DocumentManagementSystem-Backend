/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.ac.bg.fon.silab.dms.rest.services.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.silab.dms.core.exception.DMSErrorException;
import rs.ac.bg.fon.silab.dms.core.model.Company;
import rs.ac.bg.fon.silab.dms.core.model.CompanyProcess;
import rs.ac.bg.fon.silab.dms.core.model.User;
import rs.ac.bg.fon.silab.dms.core.service.ProcessService;
import rs.ac.bg.fon.silab.dms.rest.services.process.dto.ProcessRequest;
import rs.ac.bg.fon.silab.dms.rest.services.process.dto.ProcessResponse;
import rs.ac.bg.fon.silab.dms.security.TokenAuthenticationService;

import static rs.ac.bg.fon.silab.dms.rest.model.ApiResponse.createSuccessResponse;

@RestController
@RequestMapping("/processes")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @PostMapping
    public ResponseEntity create(@RequestHeader("X-Authorization") String token, @RequestBody ProcessRequest processRequest) throws DMSErrorException {
        User authenticatedUser = tokenAuthenticationService.getAuthenticatedUser(token);
        if (processRequest.getParentProcess() != null && !userInProccess(authenticatedUser, processRequest.getParentProcess())) {
            throw new DMSErrorException("User in not authorized to create process with specified process parent.");
        }
        CompanyProcess companyProcess = processService.createProcess(createProcessFromRequest(processRequest, authenticatedUser.getCompany()));

        ProcessResponse processResponse = new ProcessResponse(companyProcess);
        return ResponseEntity.ok(createSuccessResponse(processResponse));
    }

    private boolean userInProccess(User user, Long process) {
        return user.getCompany().hasProcess(process);
    }

    private CompanyProcess createProcessFromRequest(ProcessRequest processRequest, Company company) throws DMSErrorException {
        CompanyProcess companyProcess = new CompanyProcess();
        companyProcess.setCompany(company);
        companyProcess.setName(processRequest.getName());
        CompanyProcess parentProcess = null;
        if (processRequest.getParentProcess() != null) {
            parentProcess = processService.getProcess(processRequest.getParentProcess());
            if (parentProcess.isPrimitive()) {
                throw new DMSErrorException("You can create only activity from primitive process.");
            }
            if (parentProcess.getChildProcesses().stream().anyMatch(CompanyProcess::isPrimitive)) {
                if (!processRequest.isPrimitive()) {
                    throw new DMSErrorException("Child process for this parent can be only primitive process.");
                }
            }
            if (parentProcess.getChildProcesses().stream().anyMatch(companyProcess1 -> !companyProcess1.isPrimitive()) && processRequest.isPrimitive())
                throw new DMSErrorException("Child process for this parent cannot be primitive.");
        }
        companyProcess.setPrimitive(processRequest.isPrimitive());
        companyProcess.setParentProcess(parentProcess);
        return companyProcess;
    }

}
