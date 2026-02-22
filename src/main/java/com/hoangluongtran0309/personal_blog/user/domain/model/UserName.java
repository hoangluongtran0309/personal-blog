package com.hoangluongtran0309.personal_blog.user.domain.model;

public class UserName {

    private String username;

    private String firstname;

    private String lastname;

    protected UserName() {

    }

    public UserName(String username) {
        if (username.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.username = username;
    }

    public UserName(String firstname, String lastname) {
        if (firstname.isBlank() || lastname.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.firstname = firstname;
        this.lastname = lastname;
    }

    public UserName(String username, String firstname, String lastname) {
        if (username.isBlank() || firstname.isBlank() || lastname.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
        result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
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
        UserName other = (UserName) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (firstname == null) {
            if (other.firstname != null)
                return false;
        } else if (!firstname.equals(other.firstname))
            return false;
        if (lastname == null) {
            if (other.lastname != null)
                return false;
        } else if (!lastname.equals(other.lastname))
            return false;
        return true;
    }

}
