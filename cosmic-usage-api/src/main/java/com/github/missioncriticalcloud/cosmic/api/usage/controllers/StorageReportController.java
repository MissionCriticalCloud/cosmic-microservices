package com.github.missioncriticalcloud.cosmic.api.usage.controllers;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_DATE_FORMAT;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.api.usage.services.UsageCalculator;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.DomainsRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.services.TokenService;
import com.github.missioncriticalcloud.cosmic.usage.core.views.StorageView;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class StorageReportController {

    private final UsageCalculator usageCalculator;
    private final TokenService tokenService;
    private final DomainsRepository domainsRepository;

    @Autowired
    public StorageReportController(
            final UsageCalculator usageCalculator,
            final TokenService tokenService,
            final DomainsRepository domainsRepository
    ) {
        this.usageCalculator = usageCalculator;
        this.tokenService = tokenService;
        this.domainsRepository = domainsRepository;
    }

    @RequestMapping("/storage/domains")
    @JsonView(StorageView.class)
    public List<Domain> storageDomains(
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime from,
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime to,
            @RequestParam final String token,
            @RequestParam(required = false, defaultValue = DataUnit.DEFAULT) final DataUnit dataUnit,
            @RequestParam(required = false, defaultValue = TimeUnit.DEFAULT) final TimeUnit timeUnit
    ) {
        tokenService.validate(token, Domain.ROOT_PATH);

        return usageCalculator.calculateStorage(from, to, Domain.ROOT_PATH, dataUnit, timeUnit);
    }

    @RequestMapping("/storage/domains/{uuid}")
    @JsonView(StorageView.class)
    public Domain storageDomain(
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime from,
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime to,
            @RequestParam final String token,
            @PathVariable("uuid") final String uuid,
            @RequestParam(required = false, defaultValue = DataUnit.DEFAULT) final DataUnit dataUnit,
            @RequestParam(required = false, defaultValue = TimeUnit.DEFAULT) final TimeUnit timeUnit
    ) {
        final Domain domain = domainsRepository.get(uuid);
        tokenService.validate(token, domain.getPath());

        return usageCalculator.calculateStorage(from, to, domain, dataUnit, timeUnit);
    }

}
