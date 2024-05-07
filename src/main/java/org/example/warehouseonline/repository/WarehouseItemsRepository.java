package org.example.warehouseonline.repository;

import io.micrometer.common.lang.NonNullApi;
import org.example.warehouseonline.entity.WareHouseItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NonNullApi
@Repository
public interface WarehouseItemsRepository extends JpaRepository<WareHouseItems, Integer> {
    Optional<WareHouseItems> findById(long ItemId);

    List<WareHouseItems> findAll();

    void deleteById(long id);
}
