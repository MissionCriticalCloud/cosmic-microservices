package com.github.missioncriticalcloud.cosmic.api.usage.controllers;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.DomainsRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.services.TokenService;
import com.github.missioncriticalcloud.cosmic.usage.core.views.GeneralView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class DomainsController {

    private final DomainsRepository domainsRepository;
    private final TokenService tokenService;

    @Autowired
    public DomainsController(final DomainsRepository domainsRepository, final TokenService tokenService) {
        this.domainsRepository = domainsRepository;
        this.tokenService = tokenService;
    }

    @RequestMapping("/domains")
    @JsonView(GeneralView.class)
    public List<Domain> general(@RequestParam final String token) {
        tokenService.validate(token, Domain.ROOT_PATH);

        return domainsRepository.search(Domain.ROOT_PATH);
    }
}
