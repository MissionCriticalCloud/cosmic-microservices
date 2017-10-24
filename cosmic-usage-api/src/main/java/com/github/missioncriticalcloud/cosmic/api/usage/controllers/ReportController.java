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
import com.github.missioncriticalcloud.cosmic.usage.core.views.ComputeView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
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
public class ReportController {

    private final UsageCalculator usageCalculator;
    private final TokenService tokenService;
    private final DomainsRepository domainsRepository;

    @Autowired
    public ReportController(
            final UsageCalculator usageCalculator,
            final TokenService tokenService,
            final DomainsRepository domainsRepository) {
        this.usageCalculator = usageCalculator;
        this.tokenService = tokenService;
        this.domainsRepository = domainsRepository;
    }

    @RequestMapping("/detailed")
    @JsonView(DetailedView.class)
    public Domain detailed(
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime from,
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime to,
            @RequestParam final String token,
            @RequestParam final String path,
            @RequestParam(required = false, defaultValue = DataUnit.DEFAULT) final DataUnit dataUnit,
            @RequestParam(required = false, defaultValue = TimeUnit.DEFAULT) final TimeUnit timeUnit
    ) {
        tokenService.validate(token, path);

        return usageCalculator.calculate(from, to, path, dataUnit, timeUnit);
    }

    @RequestMapping("/compute/domains")
    @JsonView(ComputeView.class)
    public List<Domain> computeDomains(
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime from,
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime to,
            @RequestParam final String token,
            @RequestParam(required = false, defaultValue = DataUnit.DEFAULT) final DataUnit dataUnit,
            @RequestParam(required = false, defaultValue = TimeUnit.DEFAULT) final TimeUnit timeUnit
    ) {
        tokenService.validate(token, Domain.ROOT_PATH);

        return usageCalculator.calculateComputeDomains(from, to, Domain.ROOT_PATH, dataUnit, timeUnit);
    }

    @RequestMapping("/compute/domains/{uuid}")
    @JsonView(ComputeView.class)
    public Domain computeDomainForUuid(
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime from,
            @RequestParam @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) final DateTime to,
            @RequestParam final String token,
            @PathVariable("uuid") final String uuid,
            @RequestParam(required = false, defaultValue = DataUnit.DEFAULT) final DataUnit dataUnit,
            @RequestParam(required = false, defaultValue = TimeUnit.DEFAULT) final TimeUnit timeUnit
    ) {

        Domain domain = domainsRepository.get(uuid);
        tokenService.validate(token, domain.getPath());

        return usageCalculator.calculateComputeForUuid(from, to, domain, dataUnit, timeUnit);
    }

}
