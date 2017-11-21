package com.github.missioncriticalcloud.cosmic.billviewer.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private final String cosmicApiBaseUrl;
    private final BigDecimal cpuPrice;
    private final BigDecimal memoryPrice;
    private final BigDecimal storagePrice;
    private final BigDecimal publicIpPrice;
    private final BigDecimal serviceFee;
    private final BigDecimal innovationFee;

    public MainController(
            @Value("${cosmic.bill-viewer.cosmic-api-base-url}") final String cosmicApiBaseUrl,
            @Value("${cosmic.bill-viewer.cpu-price}") final BigDecimal cpuPrice,
            @Value("${cosmic.bill-viewer.memory-price}") final BigDecimal memoryPrice,
            @Value("${cosmic.bill-viewer.storage-price}") final BigDecimal storagePrice,
            @Value("${cosmic.bill-viewer.public-ip-price}") final BigDecimal publicIpPrice,
            @Value("${cosmic.bill-viewer.service-fee}") final BigDecimal serviceFee,
            @Value("${cosmic.bill-viewer.innovation-fee}") final BigDecimal innovationFee
    ) {
        this.cosmicApiBaseUrl = cosmicApiBaseUrl;
        this.cpuPrice = cpuPrice;
        this.memoryPrice = memoryPrice;
        this.storagePrice = storagePrice;
        this.publicIpPrice = publicIpPrice;
        this.serviceFee = serviceFee;
        this.innovationFee = innovationFee;
    }

    @ModelAttribute("config")
    public Map<String, Object> config() {
        final Map<String, Object> config = new HashMap<>();
        config.put("cosmicApiBaseUrl", cosmicApiBaseUrl);
        config.put("cpuPrice", cpuPrice);
        config.put("memoryPrice", memoryPrice);
        config.put("storagePrice", storagePrice);
        config.put("publicIpPrice", publicIpPrice);
        config.put("serviceFee", serviceFee);
        config.put("innovationFee", innovationFee);

        return config;
    }

    @RequestMapping("/")
    public String domains(@RequestParam("token") final String token, final Model model) {
        model.addAttribute("token", token);

        return "domains";
    }

    @RequestMapping("/detailed")
    public String detailed(@RequestParam("path") final String path, @RequestParam("token") final String token, final Model model) {
        model.addAttribute("token", token);
        model.addAttribute("path", path);

        return "detailed";
    }

    @RequestMapping("/compute/{uuid}")
    public String compute(@RequestParam("token") final String token, @PathVariable(value = "uuid") final String uuid, final Model model) {
        model.addAttribute("token", token);
        model.addAttribute("uuid", uuid);

        return "compute";
    }

    @RequestMapping("/storage/{uuid}")
    public String storage(@RequestParam("token") final String token, @PathVariable(value = "uuid") final String uuid, final Model model) {
        model.addAttribute("token", token);
        model.addAttribute("uuid", uuid);

        return "storage";
    }

    @RequestMapping("/networking/{uuid}")
    public String networking(@RequestParam("token") final String token, @PathVariable(value = "uuid") final String uuid, final Model model) {
        model.addAttribute("token", token);
        model.addAttribute("uuid", uuid);

        return "networking";
    }
}
