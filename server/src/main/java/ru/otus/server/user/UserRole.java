package ru.otus.server.user;

public enum UserRole {

    USER("user"),
    ADMIN("admin");

    private final String roleCode;
    UserRole(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public static UserRole fromString(String value) {
        for (UserRole status : UserRole.values()) {
            if (status.roleCode.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
