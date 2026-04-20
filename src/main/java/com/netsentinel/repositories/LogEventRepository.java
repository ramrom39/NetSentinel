package com.netsentinel.repositories;

import com.netsentinel.models.LogEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEventRepository extends JpaRepository<LogEvent, Long> {

    // ── Motor de Reglas: Brute Force ─────────────────────────
    @Query("SELECT COUNT(l) FROM LogEvent l WHERE l.sourceIp = :sourceIp AND l.eventType = :eventType AND l.timestamp >= :since")
    long countBySourceIpAndEventTypeSince(
            @Param("sourceIp") String sourceIp,
            @Param("eventType") String eventType,
            @Param("since") LocalDateTime since
    );

    // ── Motor de Reglas: DoS / Endpoint Scanning ─────────────
    /** Cuenta TODOS los eventos de una IP desde una fecha */
    @Query("SELECT COUNT(l) FROM LogEvent l WHERE l.sourceIp = :sourceIp AND l.timestamp >= :since")
    long countAllBySourceIpSince(
            @Param("sourceIp") String sourceIp,
            @Param("since") LocalDateTime since
    );

    // ── Enriquecimiento de Severidad ──────────────────────────
    // (reutiliza countAllBySourceIpSince para la ventana de 5 min)

    // ── Analytics: Distribución por Severidad ─────────────────
    @Query("SELECT l.severity, COUNT(l) FROM LogEvent l GROUP BY l.severity")
    List<Object[]> countGroupedBySeverity();

    // ── Analytics: Top IPs por número de eventos ──────────────
    @Query("SELECT l.sourceIp, COUNT(l) FROM LogEvent l GROUP BY l.sourceIp ORDER BY COUNT(l) DESC")
    List<Object[]> findTopIps(Pageable pageable);

    // ── Analytics: Volumen por hora (últimas 24 h) ────────────
    @Query("SELECT extract(hour from l.timestamp), COUNT(l) " +
           "FROM LogEvent l " +
           "WHERE l.timestamp >= :since " +
           "GROUP BY extract(hour from l.timestamp) " +
           "ORDER BY extract(hour from l.timestamp)")
    List<Object[]> countByHourSince(@Param("since") LocalDateTime since);
}
