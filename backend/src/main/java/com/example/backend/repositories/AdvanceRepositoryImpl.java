package com.example.backend.repositories;

import com.example.backend.entities.Advance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class AdvanceRepositoryImpl implements AdvanceRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean canApproveAdvance(int userId, int advanceId, double maxAdvanceLimit) {
        try {
            String query = """
                SELECT a 
                FROM Advance a 
                JOIN a.user u
                WHERE a.id = :advanceId AND u.id = :userId
            """;

            Advance advance = entityManager.createQuery(query, Advance.class)
                    .setParameter("advanceId", advanceId)
                    .setParameter("userId", userId)
                    .getSingleResult();

            if (advance == null) {
                return false; // Advance not found
            }

            if (advance.getAmount_request() > maxAdvanceLimit) {
                return false; // Amount exceeds limit
            }

            if (advance.getUser().getSalary() < advance.getAmount_request()) {
                return false; // Salary not sufficient
            }

            List<Date> lastAdvanceDates = entityManager.createQuery("""
                    SELECT a.requestDate 
                    FROM Advance a 
                    WHERE a.user.id = :userId 
                    ORDER BY a.requestDate DESC
                """, Date.class)
                    .setParameter("userId", userId)
                    .getResultList();

            if (!lastAdvanceDates.isEmpty() && isWithinSameMonth(lastAdvanceDates.get(0), new Date())) {
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isWithinSameMonth(Date date1, Date date2) {
        return date1.toInstant().atZone(ZoneId.systemDefault()).getMonth() ==
                date2.toInstant().atZone(ZoneId.systemDefault()).getMonth() &&
                date1.toInstant().atZone(ZoneId.systemDefault()).getYear() ==
                        date2.toInstant().atZone(ZoneId.systemDefault()).getYear();
    }

}
