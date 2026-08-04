package com.whoami.launch.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "shop")
public class Shop {
	
	@Id
    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    private String imageUrl;
    private String description;


}
