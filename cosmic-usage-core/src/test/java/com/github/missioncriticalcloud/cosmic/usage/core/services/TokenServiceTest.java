package com.github.missioncriticalcloud.cosmic.usage.core.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.TokenException;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DomainToken;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Test
    public void testEncryptDecryptDomainToken() throws IOException {
        DomainToken domainToken = new DomainToken(
                new DateTime().plus(3600L),
                new DateTime(),
                "/Cust/Test"
        );

        String encrypted = tokenService.encrypt(domainToken);

        DomainToken decrypted = tokenService.decrypt(encrypted);

        assertThat(decrypted.getDomainPath()).isEqualTo(domainToken.getDomainPath());

        assertThat(encrypted).isNotEqualTo(decrypted.getDomainPath());
    }

    @Test
    public void testVerifyDomainToken() throws IOException {
        String domain = "/Cust/Test";

        DomainToken tokenMoreSpecific = new DomainToken(
                new DateTime().plus(3600L),
                new DateTime(),
                "/Cust/Test"
        );
        DomainToken tokenLessSpecific = new DomainToken(
                new DateTime().plus(3600L),
                new DateTime(),
                "/Cust"
        );
        DomainToken tokenMaster = new DomainToken(
                new DateTime().plus(3600L),
                new DateTime(),
                "/"
        );

        tokenService.validate(tokenService.encrypt(tokenMoreSpecific), domain);
        tokenService.validate(tokenService.encrypt(tokenLessSpecific), domain);
        tokenService.validate(tokenService.encrypt(tokenMaster), domain);
    }

    @Test(expected = TokenException.class)
    public void testVerifyDomainTokenException() throws IOException {
        String domain = "/Cust/Test";

        DomainToken tokenWrong = new DomainToken(
                new DateTime().plus(3600L),
                new DateTime(),
                "/Cust/Bla"
        );

        tokenService.validate(tokenService.encrypt(tokenWrong), domain);
    }

    @Test
    public void testVerifyDomainTokenTtl() throws IOException {
        String domainPath = "/Cust/Test";

        DomainToken ttlAfter = new DomainToken(
                new DateTime().plus(3600L),
                new DateTime(),
                domainPath
        );

        tokenService.validate(tokenService.encrypt(ttlAfter), domainPath);
    }

    @Test(expected = TokenException.class)
    public void testVerifyDomainTokenTtlException() throws IOException {
        String domainPath = "/Cust/Test";

        DomainToken ttlBefore = new DomainToken(
                new DateTime().plus(-3600L),
                new DateTime(),
                domainPath
        );

        tokenService.validate(tokenService.encrypt(ttlBefore), domainPath);
    }
}
