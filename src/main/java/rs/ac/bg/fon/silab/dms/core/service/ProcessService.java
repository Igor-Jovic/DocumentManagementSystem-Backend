/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.ac.bg.fon.silab.dms.core.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.silab.dms.core.exception.BadRequestException;
import rs.ac.bg.fon.silab.dms.core.model.CompanyProcess;
import rs.ac.bg.fon.silab.dms.core.repository.ProcessRepository;

/**
 *
 * @author stefan
 */
@Service
public class ProcessService {

    @Autowired
    private ProcessRepository processRepository;

    public CompanyProcess createProcess(CompanyProcess process) {
        return processRepository.saveAndFlush(process);
    }

    public CompanyProcess getProcess(Long id) throws BadRequestException {
        CompanyProcess companyProcess = processRepository.findOne(id);
        if (companyProcess == null) {
            throw new BadRequestException("Process does not exists.");
        }
        return companyProcess;
    }

    public List<CompanyProcess> getAllMainProcesses() {
        List<CompanyProcess> companyProcesses = processRepository.findByParentProcess(null);
        return companyProcesses;
    }

}
