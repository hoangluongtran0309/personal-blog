package com.hoangluongtran0309.personal_blog.project;

import org.springframework.util.Assert;

public class ProjectBody {

    private String summary;

    private String content;

    protected ProjectBody() {

    }

    public ProjectBody(String summary, String content) {
        Assert.hasText(summary, "Project summary must not be blank");
        this.summary = summary;
        Assert.hasText(content, "Project content must not be blank");
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((summary == null) ? 0 : summary.hashCode());
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ProjectBody other = (ProjectBody) obj;
        if (summary == null) {
            if (other.summary != null) {
                return false;
            }
        } else if (!summary.equals(other.summary)) {
            return false;
        }
        if (content == null) {
            if (other.content != null) {
                return false;
            }
        } else if (!content.equals(other.content)) {
            return false;
        }
        return true;
    }

}
