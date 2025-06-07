package org.currency.model;

import jakarta.validation.constraints.NotBlank;

public class Currencies {
    private long ID;
    @NotBlank(message = "Code is required")
    private String code;
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Sign is required")
    private String sign;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
