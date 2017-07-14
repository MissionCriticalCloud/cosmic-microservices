package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.Objects;

import org.joda.time.DateTime;

public class DomainToken {
    private DateTime ttl;
    private DateTime created;
    private String domainPath;

    public DomainToken() {
    }

    public DomainToken(final DateTime ttl, final DateTime created, final String domainPath) {
        this.ttl = ttl;
        this.created = created;
        this.domainPath = domainPath;
    }

    public DateTime getTtl() {
        return ttl;
    }

    public void setTtl(final DateTime ttl) {
        this.ttl = ttl;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(final DateTime created) {
        this.created = created;
    }

    public String getDomainPath() {
        return domainPath;
    }

    public void setDomainPath(final String domainPath) {
        this.domainPath = domainPath;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DomainToken that = (DomainToken) o;
        return Objects.equals(ttl, that.ttl) &&
                Objects.equals(created, that.created) &&
                Objects.equals(domainPath, that.domainPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ttl, created, domainPath);
    }
}
