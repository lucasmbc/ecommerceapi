package io.github.lucasmbc.ecommerceapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data Transfer Object for category creation or update requests.")
public class CategoryRequestDTO {

    @NotBlank(message = "Name must not be blank")
    @Schema(description = "Name of the product category.", example = "Electronics")
    private String name;

    @Schema(description = "Description of the product category.", example = "Category for electronic devices like smartphones, laptops, etc.")
    private String description;

    public CategoryRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}