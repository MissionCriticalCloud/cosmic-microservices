package com.github.missioncriticalcloud.cosmic.api.usage.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
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

    @Test
    @Sql(value = "/test-schema.sql")
    public void testEmptyDatabase() {
        final List<Domain> domains = domainsRepository.list("/");
        assertThat(domains).isNotNull();
        assertThat(domains).isEmpty();
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testRootPath() {
        final List<Domain> domains = domainsRepository.list("/");
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(3);

        domains.forEach(this::assertDomain);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel1Path() {
        final List<Domain> domains = domainsRepository.list("/level1");
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(2);

        domains.forEach(this::assertDomain);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel2Path() {
        final List<Domain> domains = domainsRepository.list("/level1/level2");
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(1);

        domains.forEach(this::assertDomain);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel3Path() {
        final List<Domain> domains = domainsRepository.list("/level1/level2/level3");
        assertThat(domains).isNotNull();
        assertThat(domains).isEmpty();
    }

    private void assertDomain(final Domain domain) {
        assertThat(domain).isNotNull();
        assertThat(domain.getUuid()).isNotNull();
        assertThat(domain.getPath()).isNotNull();
        assertThat(domain.getName()).isNotNull();
    }

}
