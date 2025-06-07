package org.currency.DTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CurrenciesDTO {
    private Long ID;
    @NotBlank(message = "Code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters long")
    private String code;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Sign is required")
    private String sign;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
