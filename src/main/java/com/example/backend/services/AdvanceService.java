package com.example.backend.services;

import com.example.backend.entities.Advance;
import com.example.backend.entities.AdvanceStatus;
import com.example.backend.entities.User;
import com.example.backend.repositories.AdvanceRepository;
import com.example.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
@Slf4j
public class AdvanceService implements IAdvanceService {
    AdvanceRepository advrepo;
    UserRepository userRepository;
    private static final double MAX_ADVANCE_LIMIT = 1000.0;
    @Override
    public Advance addAdvance(Advance advance) {
        return advrepo.save(advance);
    }

    @Override
    public void deleteAdvance(int idAdvance) {
        advrepo.deleteById(idAdvance);

    }

    @Override
    public Advance updateAdvance(Advance advance) {
        return advrepo.save(advance);
    }

    @Override
    public List<Advance> allAdvances() {
        return advrepo.findAll();
    }

    @Override
    public Advance findAdvanceById(int idAdvance) {
        return advrepo.findById(idAdvance).get();
    }

    @Override
    public List<Advance> getAllAdvancesByUser(int userId) {
        return advrepo.findAdvancesByUserId(userId);
    }

    @Override

    public boolean canApproveAdvance(int userId, int advanceId) {
        return advrepo.canApproveAdvance(userId, advanceId, MAX_ADVANCE_LIMIT);
    }

    //statistics
    public Map<String, Long> getAdvancesByStatus() {
        List<Object[]> results = advrepo.countAdvancesByStatus();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            map.put(((AdvanceStatus) result[0]).name(), (Long) result[1]);
        }
        return map;
    }

    public Map<String, Long> getAdvancesByMonth() {
        List<Object[]> results = advrepo.countAdvancesByMonth();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            map.put((String) result[0], (Long) result[1]);
        }
        return map;
    }


        public Map<String, Double> getSinusoidalData() {
            List<Object[]> results = advrepo.findAdvancesForSinusoidal();
            Map<String, Double> sinusoidalData = new LinkedHashMap<>();

            for (Object[] result : results) {
                String date = (String) result[0];
                Double amount = ((Number) result[1]).doubleValue();
                sinusoidalData.put(date, amount);
            }
            return sinusoidalData;
        }






   /* @Override
    public Advance updateAdvanceStatus(int id, String status) {
        Advance advance = findAdvanceById(id);
        if (advance != null) {
            advance.setStatus(AdvanceStatus.valueOf(status));
            return advrepo.save(advance);
        }
        return null;
    }*/
}
