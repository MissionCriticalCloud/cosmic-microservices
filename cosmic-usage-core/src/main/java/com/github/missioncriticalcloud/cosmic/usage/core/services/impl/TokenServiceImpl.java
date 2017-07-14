package com.github.missioncriticalcloud.cosmic.usage.core.services.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.TokenException;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DomainToken;
import com.github.missioncriticalcloud.cosmic.usage.core.services.TokenService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private final TextEncryptor encryptor;
    private final ObjectMapper objectMapper;

    @Autowired
    TokenServiceImpl(
            @Value("${cosmic.usage-core.token-encryption-key}") final String encryptionKey,
            @Value("${cosmic.usage-core.token-encryption-salt}") final String encryptionSalt
    ) {
        encryptor = Encryptors.delux(encryptionKey, encryptionSalt);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
    }

    @Override
    public String encrypt(final DomainToken domainToken) throws TokenException {
        final String domainTokenSerialized;
        try {
            domainTokenSerialized = objectMapper.writeValueAsString(domainToken);
        } catch (JsonProcessingException e) {
            throw new TokenException(e.getOriginalMessage(), e);
        }

        return encryptor.encrypt(domainTokenSerialized);
    }

    @Override
    public DomainToken decrypt(final String token) throws TokenException {
        final DomainToken domainToken;

        try {
            domainToken = objectMapper.readValue(encryptor.decrypt(token), DomainToken.class);
        } catch (IOException | IllegalArgumentException e) {
            throw new TokenException(e.getMessage(), e);
        }

        return domainToken;
    }

    @Override
    public void validate(final String token, final String domain) throws TokenException {
        final DomainToken domainToken = decrypt(token);

        if (!(domain.startsWith(domainToken.getDomainPath()) && domainToken.getTtl().isAfter(new DateTime()))) {
            throw new TokenException("Unauthorized!");
        }
    }
}
