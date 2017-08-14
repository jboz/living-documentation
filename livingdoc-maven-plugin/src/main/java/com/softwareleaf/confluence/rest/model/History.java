/**
 * Copyright(C) 2015 <a href="mailto:Jonathon.a.hope@gmail.com" >Jonathon Hope</a>
 */

package com.softwareleaf.confluence.rest.model;

import com.softwareleaf.confluence.rest.util.StringUtils;

/**
 * Represents the History object of a {@code Content}.
 *
 * @author Jonathon Hope
 * @author Julien Boz
 */
public class History {
    /**
     * Represents if this is the latest version of the {@code Content}.
     */
    private boolean latest;
    /**
     * @see CreatedBy
     */
    private CreatedBy createdBy;
    /**
     * A {@code Date} in format {@literal "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"}.
     */
    private String createdDate;
    /**
     * Versioning.
     */
    private Version nextVersion;
    private Version previousVersion;
    private Version lastUpdated;

    /**
     * Represents the expandable options.
     */
    public enum Expandables {
        NEXT_VERSION, PREVIOUS_VERSION, LAST_UPDATED;

        @Override
        public String toString() {
            return StringUtils.convertToCamelCase(name());
        }
    }

    public History() {
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Version getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(Version nextVersion) {
        this.nextVersion = nextVersion;
    }

    public Version getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(Version previousVersion) {
        this.previousVersion = previousVersion;
    }

    public Version getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Version lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        History history = (History) o;

        if (latest != history.latest)
            return false;
        if (!createdBy.equals(history.createdBy))
            return false;
        if (!createdDate.equals(history.createdDate))
            return false;
        if (nextVersion != null ? !nextVersion.equals(history.nextVersion) : history.nextVersion != null)
            return false;
        if (previousVersion != null ? !previousVersion.equals(history.previousVersion) : history.previousVersion != null)
            return false;
        return !(lastUpdated != null ? !lastUpdated.equals(history.lastUpdated) : history.lastUpdated != null);

    }

    @Override
    public int hashCode() {
        int result = (latest ? 1 : 0);
        result = 31 * result + createdBy.hashCode();
        result = 31 * result + createdDate.hashCode();
        result = 31 * result + (nextVersion != null ? nextVersion.hashCode() : 0);
        result = 31 * result + (previousVersion != null ? previousVersion.hashCode() : 0);
        result = 31 * result + (lastUpdated != null ? lastUpdated.hashCode() : 0);
        return result;
    }
}
