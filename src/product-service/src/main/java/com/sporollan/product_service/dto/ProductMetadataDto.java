package com.sporollan.product_service.dto;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductMetadataDto {
    private String id;
    private String name;
    private Set<String> tracked;
    private Long dateAdded;
    private String img;

    public ProductMetadataDto(String id, String name, Set<String> tracked, Long dateAdded, String img) {
        this.id = id;
        this.name = name;
        this.tracked = tracked;
        this.dateAdded = dateAdded;
        this.img = img;
    }
}
