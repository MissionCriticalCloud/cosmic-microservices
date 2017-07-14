package com.github.missioncriticalcloud.cosmic.usage.core.services;

import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.TokenException;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DomainToken;

public interface TokenService {

    String encrypt(final DomainToken domainToken) throws TokenException;

    DomainToken decrypt(final String token) throws TokenException;

    void validate(final String token, final String domain) throws TokenException;
}
