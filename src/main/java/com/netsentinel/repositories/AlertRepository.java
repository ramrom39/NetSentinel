package com.netsentinel.repositories;

import com.netsentinel.models.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // Distribución de alertas por tipo (ruleName) para el pie chart
    @Query("SELECT a.ruleName, COUNT(a) FROM Alert a GROUP BY a.ruleName ORDER BY COUNT(a) DESC")
    List<Object[]> countGroupedByRuleName();
}
