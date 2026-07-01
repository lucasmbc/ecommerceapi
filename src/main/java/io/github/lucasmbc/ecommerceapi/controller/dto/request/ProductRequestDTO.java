package io.github.lucasmbc.ecommerceapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Data Transfer Object for product creation or update requests.")
public class ProductRequestDTO {

    @Schema(
            description = "Product name",
            example = "Notebook"
    )
    @NotBlank(message = "Name must not be blank")
    private String name;

    @Schema(
            description = "Product description",
            example = "High-performance laptop with 16GB RAM and 512GB SSD."
    )
    private String description;

    @Schema(
            description = "Product price",
            example = "3500.00"
    )
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Schema(
            description = "Product stock quantity",
            example = "10"
    )
    @NotNull(message = "Stock cannot be null")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Schema(
            description = "Unique identifier of the product's category",
            example = "f1e2d3c4-b5a6-0987-6543-210fedcba987"
    )
    @NotNull(message = "Category cannot be null")
    private UUID categoryId;

    @Schema(
            description = "URL to the product's image",
            example = "https://example.com/images/notebook.jpg"
    )
    @URL(message = "Image URL must be a valid URL")
    private String imageUrl;

    public ProductRequestDTO() {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}