package com.github.missioncriticalcloud.cosmic.api.usage.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolationException;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.api.usage.model.Domain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class DomainsRepositoryIT {

    @Autowired
    private DomainsRepository domainsRepository;

    @Test(expected = ConstraintViolationException.class)
    @Sql(value = "/test-schema.sql")
    public void test1() {
        domainsRepository.list(null);
    }

    @Test
    @Sql(value = "/test-schema.sql")
    public void testEmptyDatabase() {
        final List<Domain> domains = domainsRepository.list("/");
        assertThat(domains).isNotNull();
        assertThat(domains).isEmpty();
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/test-data.sql"})
    public void testRootPath() {
        final List<Domain> domains = domainsRepository.list("/");
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(3);

        domains.forEach(domain -> {
            assertThat(domain).isNotNull();
            assertThat(domain.getUuid()).isNotNull();
        });
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/test-data.sql"})
    public void testLevel1Path() {
        final List<Domain> domains = domainsRepository.list("/level1");
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(2);

        domains.forEach(domain -> {
            assertThat(domain).isNotNull();
            assertThat(domain.getUuid()).isNotNull();
        });
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/test-data.sql"})
    public void testLevel2Path() {
        final List<Domain> domains = domainsRepository.list("/level1/level2");
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(1);

        domains.forEach(domain -> {
            assertThat(domain).isNotNull();
            assertThat(domain.getUuid()).isNotNull();
        });
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/test-data.sql"})
    public void testLevel3Path() {
        final List<Domain> domains = domainsRepository.list("/level1/level2/level3");
        assertThat(domains).isNotNull();
        assertThat(domains).isEmpty();
    }

}
