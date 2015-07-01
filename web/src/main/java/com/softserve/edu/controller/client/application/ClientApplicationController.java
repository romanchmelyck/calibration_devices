package com.softserve.edu.controller.client.application;

import com.softserve.edu.dto.application.ApplicationFieldDTO;
import com.softserve.edu.dto.application.ClientStageVerificationDTO;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.ClientData;
import com.softserve.edu.entity.Provider;
import com.softserve.edu.entity.Verification;
import com.softserve.edu.entity.util.ReadStatus;
import com.softserve.edu.entity.util.Status;
import com.softserve.edu.service.MailService;
import com.softserve.edu.service.provider.ProviderService;
import com.softserve.edu.service.verification.VerificationService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/application/")
public class ClientApplicationController {

    private Logger logger = Logger.getLogger(ClientApplicationController.class);

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private MailService mail;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String saveApplication(
            @RequestBody ClientStageVerificationDTO verificationDTO) {

        ClientData clientData = new ClientData(
                verificationDTO.getFirstName(),
                verificationDTO.getLastName(),
                verificationDTO.getMiddleName(),
                verificationDTO.getEmail(),
                verificationDTO.getPhone(),
                new Address(verificationDTO.getRegion(),
                        verificationDTO.getDistrict(),
                        verificationDTO.getLocality(),
                        verificationDTO.getStreet(),
                        verificationDTO.getBuilding(),
                        verificationDTO.getFlat())
        );
        Provider provider = providerService.findById(verificationDTO.getProviderId());

        Verification verification = new Verification(
                new Date(),
                clientData,
                provider,
                Status.SENT,
                ReadStatus.UNREAD
        );

        verificationService.saveVerification(verification);

        String name = clientData.getFirstName() + " " + clientData.getLastName();
        mail.sendMail(clientData.getEmail(), name, verification.getId());

        return verification.getId();
    }

    @RequestMapping(value = "check/{verificationId}", method = RequestMethod.GET)
    public String getClientCode(@PathVariable String verificationId) {

        Verification verification = verificationService.findById(verificationId);
        return verification == null ? "NOT_FOUND" : verification.getStatus().name();
    }

    @RequestMapping(value = "providers/{district}", method = RequestMethod.GET)
    public List<ApplicationFieldDTO> getProvidersCorrespondingDistrict(
            @PathVariable String district) {

        return providerService.findByDistrictDesignation(district)
                .stream()
                .map(provider -> new ApplicationFieldDTO(
                                provider.getId(),
                                provider.getName())
                )
                .collect(Collectors.toList());
    }
}
