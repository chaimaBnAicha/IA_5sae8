package com.example.backend.services;

import com.example.backend.entities.Category;
import com.example.backend.entities.Insurance;
import com.example.backend.entities.InsuranceStatus;
import com.example.backend.repositories.InsuranceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Service
@AllArgsConstructor
@Slf4j
public class InsuranceService implements IServiceInsurance{

    InsuranceRepository insurancerepo;

    @Override
    public Insurance addInsurance(Insurance insurance) {
        return insurancerepo.save(insurance);
    }

    @Override
    public void deleteInsurance(int idInsurance) {
        insurancerepo.deleteById(idInsurance);

    }

    @Override
    public Insurance updateInsurance(Insurance insurance) {
       return insurancerepo.save(insurance);
    }

    @Override
    public List<Insurance> allInsurances() {
        return insurancerepo.findAll();
    }

    @Override
    public Insurance findInsuranceById(int idInsurance) {
        return insurancerepo.findById(idInsurance).get();
    }



    public Map<InsuranceStatus, Long> countInsurancesByStatus() {
        List<Insurance> allInsurances = insurancerepo.findAll();
        Map<InsuranceStatus, Long> statusCount = new EnumMap<>(InsuranceStatus.class);

        for (Insurance insurance : allInsurances) {
            InsuranceStatus status = insurance.getStatus();
            statusCount.put(status, statusCount.getOrDefault(status, 0L) + 1);
        }

        return statusCount;
    }





    // ✅ Nombre d’assurances par catégorie
    public Map<Category, Long> countInsurancesByCategory() {
        List<Object[]> results = insurancerepo.countInsurancesByCategory();
        Map<Category, Long> categoryCount = new EnumMap<>(Category.class);

        for (Object[] row : results) {
            Category category = (Category) row[0];
            Long count = (Long) row[1];
            categoryCount.put(category, count);
        }

        return categoryCount;
    }

    // ✅ Nombre d’assurances par mois de début
    public Map<Integer, Long> countInsurancesByMonth() {
        List<Object[]> results = insurancerepo.countInsurancesByMonth();
        Map<Integer, Long> monthCount = new TreeMap<>(); // Trie automatiquement les mois

        for (Object[] row : results) {
            Integer month = (Integer) row[0];
            Long count = (Long) row[1];
            monthCount.put(month, count);
        }

        return monthCount;
    }


}
