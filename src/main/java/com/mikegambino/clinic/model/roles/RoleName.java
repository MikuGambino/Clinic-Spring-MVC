package com.mikegambino.clinic.model.roles;

import com.mikegambino.clinic.exception.ResourceNotFoundException;
import lombok.Getter;
import static com.mikegambino.clinic.util.AppConstants.*;

@Getter
public enum RoleName {
    ROLE_PATIENT("patient"),
    ROLE_ADMIN("admin"),
    ROLE_DOCTOR("doctor");
    private final String roleName;

    RoleName(String roleName) {
        this.roleName = roleName;
    }

    public static boolean containsRole(String simpleName) {
        simpleName = simpleName.toLowerCase();
        return simpleName.equals(ROLE_ADMIN.roleName) ||
                simpleName.equals(ROLE_DOCTOR.roleName) ||
                simpleName.equals(ROLE_PATIENT.roleName);
    }

    public static RoleName getBySimpleName(String simpleName) {
        simpleName = simpleName.toLowerCase();
        if (simpleName.equals(ROLE_ADMIN.getRoleName())) {
            return ROLE_ADMIN;
        } else if (simpleName.equals(ROLE_PATIENT.getRoleName())) {
            return ROLE_PATIENT;
        } else if (simpleName.equals(ROLE_DOCTOR.getRoleName())) {
            return ROLE_DOCTOR;
        }

        throw new ResourceNotFoundException(ROLE, TITLE, simpleName);
    }
}
