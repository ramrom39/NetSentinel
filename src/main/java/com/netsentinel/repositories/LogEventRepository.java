package com.netsentinel.repositories;

import com.netsentinel.models.LogEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LogEventRepository extends JpaRepository<LogEvent, Long> {

    @Query("SELECT COUNT(l) FROM LogEvent l WHERE l.sourceIp = :sourceIp AND l.eventType = :eventType AND l.timestamp >= :since")
    long countBySourceIpAndEventTypeSince(
            @Param("sourceIp") String sourceIp,
            @Param("eventType") String eventType, 
            @Param("since") LocalDateTime since
    );
}
