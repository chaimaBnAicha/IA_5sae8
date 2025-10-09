package com.example.backend.repositories;

import com.example.backend.entities.Advance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvanceRepository extends JpaRepository<Advance, Integer>, AdvanceRepositoryCustom {

    // Corrected to use user_id which is the actual column name in database
    @Query("SELECT a FROM Advance a JOIN FETCH a.user WHERE a.user.id = :userId")
    List<Advance> findAdvancesByUserId(@Param("userId") int userId);

    @Query("SELECT a.requestDate FROM Advance a WHERE a.user.id = :userId ORDER BY a.requestDate DESC")
    List<Date> findLastAdvanceDatesByUserId(@Param("userId") int userId);

    // Statistics
    @Query("SELECT a.status, COUNT(a) FROM Advance a GROUP BY a.status")
    List<Object[]> countAdvancesByStatus();

    @Query("SELECT MONTHNAME(a.requestDate), COUNT(a) FROM Advance a GROUP BY MONTH(a.requestDate)")
    List<Object[]> countAdvancesByMonth();

    @Query("SELECT FUNCTION('DATE_FORMAT', a.requestDate, '%Y-%m-%d'), SUM(a.amount_request) " +
            "FROM Advance a GROUP BY FUNCTION('DATE_FORMAT', a.requestDate, '%Y-%m-%d') ORDER BY a.requestDate")
    List<Object[]> findAdvancesForSinusoidal();

    // Corrected to use proper join with user_id
    @Query("SELECT a FROM Advance a JOIN a.user u WHERE a.id = :advanceId AND u.id = :userId")
    Optional<Advance> findAdvanceByUserAndId(@Param("userId") int userId,
                                             @Param("advanceId") int advanceId);

    // Corrected approval check query with proper join condition
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Advance a JOIN a.user u " +
            "WHERE a.id = :advanceId AND u.id = :userId AND a.amount_request <= :maxLimit")
    boolean canApproveAdvance(@Param("userId") int userId,
                              @Param("advanceId") int advanceId,
                              @Param("maxLimit") double maxLimit);
}