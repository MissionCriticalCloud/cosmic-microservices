package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

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
    private static final String ROOT_PATH = "/";
    private static final String LEVEL_1_PATH = "/level1";
    private static final String LEVEL_2_PATH = "/level1/level2";
    private static final String LEVEL_3_PATH = "/level1/level2/level3";

    @Autowired
    private DomainsRepository domainsRepository;

    @Test
    @Sql(value = "/test-schema.sql")
    public void testEmptyDatabaseSearchDomains() {
        final List<Domain> domains = domainsRepository.search(ROOT_PATH);
        assertThat(domains).isNotNull();
        assertThat(domains).isEmpty();
    }

    @Test(expected = org.springframework.dao.EmptyResultDataAccessException.class)
    @Sql(value = "/test-schema.sql")
    public void testEmptyDatabaseGetDomain() {
        final Domain domain = domainsRepository.getByPath(ROOT_PATH);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testRootPathSearchDomains() {
        final List<Domain> domains = domainsRepository.search(ROOT_PATH);
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(3);

        domains.forEach(this::assertDomain);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testRootPathGetDomain() {
        final Domain domain = domainsRepository.getByPath(ROOT_PATH);
        assertThat(domain).isNotNull();

        assertDomain(domain);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel1PathSearchDomains() {
        final List<Domain> domains = domainsRepository.search(LEVEL_1_PATH);
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(2);

        domains.forEach(this::assertDomain);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel1PathGetDomain() {
        final Domain domain = domainsRepository.getByPath(LEVEL_1_PATH);
        assertThat(domain).isNotNull();

        assertDomain(domain);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel2PathSearchDomains() {
        final List<Domain> domains = domainsRepository.search(LEVEL_2_PATH);
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(1);

        domains.forEach(this::assertDomain);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel2PathGetDomain() {
        final Domain domain = domainsRepository.getByPath(LEVEL_2_PATH);
        assertThat(domain).isNotNull();

        assertDomain(domain);
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel3PathSearchDomains() {
        final List<Domain> domains = domainsRepository.search(LEVEL_3_PATH);
        assertThat(domains).isNotNull();
        assertThat(domains).isEmpty();
    }

    @Test(expected = org.springframework.dao.EmptyResultDataAccessException.class)
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel3PathGetDomain() {
        final Domain domain = domainsRepository.getByPath(LEVEL_3_PATH);
    }

    private void assertDomain(final Domain domain) {
        assertThat(domain).isNotNull();
        assertThat(domain.getUuid()).isNotNull();
        assertThat(domain.getPath()).isNotNull();
        assertThat(domain.getName()).isNotNull();
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testLevel2PathGetDomainByUuid() {
        final Domain domain = domainsRepository.get("domain_uuid2");
        assertThat(domain).isNotNull();

        assertDomain(domain);
        assertThat(domain.getEmail()).isNotNull();
        assertThat(domain.getEmail()).isEqualTo("level1@cosmic.nl");
    }
}
