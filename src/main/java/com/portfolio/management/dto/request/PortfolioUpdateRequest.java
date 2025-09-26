package com.portfolio.management.dto.request;

import jakarta.validation.constraints.Size;

public class PortfolioUpdateRequest {
    @Size(min = 2, max = 100, message = "Portfolio name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Boolean isPublic;

    // Constructors
    public PortfolioUpdateRequest() {
    }

    public PortfolioUpdateRequest(String name, String description, Boolean isPublic) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
    }

    // Getters and setters
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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
