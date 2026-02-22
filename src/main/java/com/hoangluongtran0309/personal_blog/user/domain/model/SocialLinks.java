package com.hoangluongtran0309.personal_blog.user.domain.model;

public class SocialLinks {

    private String githubUrl;

    private String xUrl;

    private String facebookUrl;

    private String linkedinUrl;

    protected SocialLinks() {

    }

    public SocialLinks(String githubUrl, String xUrl, String facebookUrl, String linkedinUrl) {
        if (githubUrl.isBlank() || xUrl.isBlank() || facebookUrl.isBlank() || linkedinUrl.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.githubUrl = githubUrl;
        this.xUrl = xUrl;
        this.facebookUrl = facebookUrl;
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getxUrl() {
        return xUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((githubUrl == null) ? 0 : githubUrl.hashCode());
        result = prime * result + ((xUrl == null) ? 0 : xUrl.hashCode());
        result = prime * result + ((facebookUrl == null) ? 0 : facebookUrl.hashCode());
        result = prime * result + ((linkedinUrl == null) ? 0 : linkedinUrl.hashCode());
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
        SocialLinks other = (SocialLinks) obj;
        if (githubUrl == null) {
            if (other.githubUrl != null)
                return false;
        } else if (!githubUrl.equals(other.githubUrl))
            return false;
        if (xUrl == null) {
            if (other.xUrl != null)
                return false;
        } else if (!xUrl.equals(other.xUrl))
            return false;
        if (facebookUrl == null) {
            if (other.facebookUrl != null)
                return false;
        } else if (!facebookUrl.equals(other.facebookUrl))
            return false;
        if (linkedinUrl == null) {
            if (other.linkedinUrl != null)
                return false;
        } else if (!linkedinUrl.equals(other.linkedinUrl))
            return false;
        return true;
    }

}
