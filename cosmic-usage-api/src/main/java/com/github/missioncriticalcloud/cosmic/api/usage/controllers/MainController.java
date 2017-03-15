package com.github.missioncriticalcloud.cosmic.api.usage.controllers;

import static com.github.missioncriticalcloud.cosmic.api.usage.utils.FormatUtils.DATE_FORMATTER;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.api.usage.model.SearchResult;
import com.github.missioncriticalcloud.cosmic.api.usage.model.Usage;
import com.github.missioncriticalcloud.cosmic.api.usage.services.SearchService;
import com.github.missioncriticalcloud.cosmic.api.usage.services.UsageService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    private final SearchService searchService;
    private final UsageService usageService;

    @Autowired
    public MainController(final SearchService searchService, final UsageService usageService) {
        this.searchService = searchService;
        this.usageService = usageService;
    }

    @RequestMapping("/")
    public List<Usage> defaultEndpoint(
            @RequestParam("from") final String fromAsString,
            @RequestParam("to") final String toAsString,
            @RequestParam("path") final String path
    ) {
        final DateTime from = DATE_FORMATTER.parseDateTime(fromAsString);
        final DateTime to = DATE_FORMATTER.parseDateTime(toAsString);

        final SearchResult searchResult = searchService.search(from, to, path);
        return usageService.calculateDomainsUsage(searchResult.getDomains(), from, to);
    }

}
