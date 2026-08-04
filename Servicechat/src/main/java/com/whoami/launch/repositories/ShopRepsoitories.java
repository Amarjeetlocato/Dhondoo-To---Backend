package com.whoami.launch.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entities.Shop;

@Repository
public interface ShopRepsoitories extends JpaRepository<Shop, String>{

}
