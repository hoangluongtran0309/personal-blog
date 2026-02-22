package com.hoangluongtran0309.personal_blog.user.domain.model;

public class Bio {

    private String headline;

    private String bio;

    protected Bio() {

    }

    public Bio(String headline, String bio) {
        if (headline.isBlank() || bio.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.headline = headline;
        this.bio = bio;
    }

    public String getHeadline() {
        return headline;
    }

    public String getBio() {
        return bio;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((headline == null) ? 0 : headline.hashCode());
        result = prime * result + ((bio == null) ? 0 : bio.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Bio other = (Bio) obj;
        if (headline == null) {
            if (other.headline != null)
                return false;
        } else if (!headline.equals(other.headline))
            return false;
        if (bio == null) {
            if (other.bio != null)
                return false;
        } else if (!bio.equals(other.bio))
            return false;
        return true;
    }

}
