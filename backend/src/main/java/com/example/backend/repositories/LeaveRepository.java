package com.example.backend.repositories;

import com.example.backend.entities.Leave;
import com.example.backend.entities.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
public interface LeaveRepository extends JpaRepository<Leave, Integer> {
    @Query("SELECT COUNT(l) = 0 FROM Leave l " +
            "WHERE l.user.id = :userId " +
            "AND l.status = :status " +
            "AND l.start_date <= :endDate " +
            "AND l.end_date >= :startDate")
    boolean canAcceptLeaveRequest(@Param("userId") int userId,
                                  @Param("status") LeaveStatus status,
                                  @Param("startDate") Date startDate,
                                  @Param("endDate") Date endDate);

    @Query("SELECT l.type, COUNT(l) FROM Leave l WHERE l.status = com.example.backend.entities.LeaveStatus.Approved GROUP BY l.type")
    List<Object[]> getLeaveCountByType();

    @Query("SELECT l.type, AVG(DATEDIFF(l.end_date, l.start_date) + 1) " +
            "FROM Leave l " +
            "GROUP BY l.type")
    List<Object[]> getAverageLeaveDurationPerType();

    @Query("SELECT FUNCTION('MONTH', l.start_date), COUNT(l) " +
            "FROM Leave l " +
            "WHERE l.status = com.example.backend.entities.LeaveStatus.Approved " +
            "GROUP BY FUNCTION('MONTH', l.start_date) " +
            "ORDER BY FUNCTION('MONTH', l.start_date)")
    List<Object[]> getApprovedLeavesByMonth();



}
