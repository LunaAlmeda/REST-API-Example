package com.springBootApp.RestApiExample.repository;

import com.springBootApp.RestApiExample.entity.Prices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PricesRepository extends JpaRepository<Prices, Long> {

    @Query("SELECT p FROM Prices p " +
            "WHERE p.brandId = :brandId " +
            "AND p.productId = :productId " +
            "AND :dateTime BETWEEN p.startDate AND p.endDate " +
            "ORDER BY p.priority DESC")
    List<Prices> findPriceRelatedWithProvidedParameter(@Param("brandId") Integer brandId,
                                                       @Param("productId") Long productId,
                                                       @Param("dateTime") LocalDateTime dateTime);

}
