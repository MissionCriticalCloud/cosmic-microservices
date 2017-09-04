package com.github.missioncriticalcloud.cosmic.usage.core.services;

import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.TokenException;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DomainToken;

public interface TokenService {

    String encrypt(DomainToken domainToken) throws TokenException;

    DomainToken decrypt(String token) throws TokenException;

    void validate(String token, String domain) throws TokenException;
}
