package com.vforce360.repository;

import com.vforce360.models.MarReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MarReportRepository extends JpaRepository<MarReport, UUID> {
    // Standard JPA repository
}
