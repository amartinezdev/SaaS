package com.example.listingai.subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "plans")
public class Plan {
    @Id
    @Column(length = 20)
    private String code;

    @Column(name = "display_name", nullable = false, length = 80)
    private String displayName;

    protected Plan() {}

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
}
