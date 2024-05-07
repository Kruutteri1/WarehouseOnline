package org.example.warehouseonline.repository;

import io.micrometer.common.lang.NonNullApi;
import org.example.warehouseonline.entity.WarehouseOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NonNullApi
@Repository
public interface WarehouseOrdersRepository extends JpaRepository<WarehouseOrders, Integer> {

    Optional<WarehouseOrders> findById(long ItemId);

    List<WarehouseOrders> findAll();

    void deleteById(long id);

}
