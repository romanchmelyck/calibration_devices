package com.softserve.edu.controller.provider;

import com.softserve.edu.controller.provider.util.VerificationPageDTOTransformer;
import com.softserve.edu.dto.provider.VerificationDTO;
import com.softserve.edu.dto.provider.VerificationReadStatusUpdateDTO;
import com.softserve.edu.dto.provider.VerificationSearchDTO;
import com.softserve.edu.dto.provider.VerificationUpdatingDTO;
import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.dto.application.ClientStageVerificationDTO;
import com.softserve.edu.dto.provider.VerificationPageDTO;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.Calibrator;
import com.softserve.edu.entity.ClientData;
import com.softserve.edu.entity.Verification;
import com.softserve.edu.service.calibrator.CalibratorService;
import com.softserve.edu.service.SecurityUserDetailsService;
import com.softserve.edu.service.provider.ProviderService;
import com.softserve.edu.service.verification.VerificationService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/provider/verifications/")
public class ProviderVerificationController {

    @Autowired
    VerificationService verificationService;

    @Autowired
    ProviderService providerService;

    @Autowired
    CalibratorService calibratorService;
    
    private final Logger logger = Logger.getLogger(ProviderVerificationController.class);

    @RequestMapping(value = "archive/{pageNumber}/{itemsPerPage}", method = RequestMethod.GET)
    public PageDTO<VerificationPageDTO> getPageOfAllVerificationsByProviderId(
            @PathVariable Integer pageNumber,
            @PathVariable Integer itemsPerPage,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        Page<VerificationPageDTO> page = VerificationPageDTOTransformer
                .toDTO(verificationService
                        .findPageOfAllVerificationsByProviderId(
                                user.getOrganizationId(),
                                pageNumber,
                                itemsPerPage
                        ));

        return new PageDTO<>(page.getTotalElements(), page.getContent());
    }

    @RequestMapping(value = "new/{pageNumber}/{itemsPerPage}", method = RequestMethod.GET)
    public PageDTO<VerificationPageDTO> getPageOfAllSentVerificationsByProviderId(
            @PathVariable Integer pageNumber,
            @PathVariable Integer itemsPerPage,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        Page<VerificationPageDTO> page = VerificationPageDTOTransformer
                .toDTO(verificationService
                        .findPageOfSentVerificationsByProviderId(
                                user.getOrganizationId(),
                                pageNumber,
                                itemsPerPage));
        return new PageDTO<>(page.getTotalElements(), page.getContent());
    }

    @RequestMapping(value = "new/search", method = RequestMethod.POST)
    public PageDTO<VerificationPageDTO> getPageOfAllSentVerificationsByProviderIdAndSearch(
    		@RequestBody VerificationSearchDTO verificationSearchDto,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {

    		
    			
    	if(verificationSearchDto.getSearchByDate().length()>0){
			
			 Page<VerificationPageDTO> page = VerificationPageDTOTransformer
		                .toDTO(verificationService
		                        .findPageOfSentVerificationsByCalibratorIdAndSearch(
		                                employeeUser.getOrganizationId(),
		                                verificationSearchDto.getPageNumber(),
		                                verificationSearchDto.getItemsPerPage(),
		                                verificationSearchDto.getSearchByDate(),
		                                verificationSearchDto.getSearchByDate()
		                                ));

		        return new PageDTO<>(page.getTotalElements(), page.getContent());
		} else {
			
			 Page<VerificationPageDTO> page = VerificationPageDTOTransformer
		                .toDTO(verificationService
		                        .findPageOfSentVerificationsByCalibratorId(
		                                employeeUser.getOrganizationId(),
		                                verificationSearchDto.getPageNumber(),
		                                verificationSearchDto.getItemsPerPage()));

		        return new PageDTO<>(page.getTotalElements(), page.getContent());
		}
    		
    }
    
    
    @RequestMapping(value = "new/count/provider", method = RequestMethod.GET)
    public Long getCountOfNewVerificationsByProviderId( @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
    	//System.out.println("Inside controller count ...");
    	return verificationService.findCountOfNewVerificationsByProviderId(user.getOrganizationId());
    }
    
    
    /**
     * Find calibrators by district which correspond provider district
     *
     * @return calibrator
     */
    @RequestMapping(value = "new/calibrators", method = RequestMethod.GET)
    public List<Calibrator> updateVerification(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        return calibratorService.findByDistrict(
                providerService
                        .findById(user.getOrganizationId())
                        .getAddress()
                        .getDistrict()
        );
    }

    /**
     * Update verifications
     */
    @RequestMapping(value = "new/update", method = RequestMethod.PUT)
    public void updateVerification(
            @RequestBody VerificationUpdatingDTO verificationUpdatingDTO) {
        for (String verificationId : verificationUpdatingDTO.getIdsOfVerifications()) {
            verificationService
                    .updateVerificationByProvider(
                            verificationId,
                            verificationUpdatingDTO.getCalibrator()
                    );
        }
    }

    @RequestMapping(value = "new/read", method = RequestMethod.PUT)
    public void markVerificationAsRead(@RequestBody VerificationReadStatusUpdateDTO verificationDto) {
     System.out.println("inside controller to update");
     verificationService.updateVerificationReadStatus(verificationDto.getVerificationId(), verificationDto.getReadStatus());
    }
    
    
    @RequestMapping(value = "new/{verificationId}", method = RequestMethod.GET)
    public ClientStageVerificationDTO getNewVerificationDetailsById(
            @PathVariable String verificationId,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        Verification verification = verificationService
                .findByIdAndProviderId(
                        verificationId,
                        user.getOrganizationId()
                );

        ClientData clientData = verification.getClientData();
        Address address = clientData.getClientAddress();

        return new ClientStageVerificationDTO(clientData, address, null);
    }

    @RequestMapping(value = "archive/{verificationId}", method = RequestMethod.GET)
    public VerificationDTO getArchivalVerificationDetailsById(
            @PathVariable String verificationId,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        Verification verification = verificationService
                .findByIdAndProviderId(verificationId, user.getOrganizationId());

        return new VerificationDTO(
                verification.getClientData(),
                verification.getId(),
                verification.getInitialDate(),
                verification.getExpirationDate(),
                verification.getStatus(),
                verification.getCalibrator(),
                verification.getCalibratorEmployee(),
                verification.getDevice(),
                verification.getProvider(),
                verification.getProviderEmployee(),
                verification.getStateVerificator(),
                verification.getStateVerificatorEmployee()
        );
    }
}
