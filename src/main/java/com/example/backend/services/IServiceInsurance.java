package com.example.backend.services;

import com.example.backend.entities.Category;
import com.example.backend.entities.Insurance;
import com.example.backend.entities.InsuranceStatus;

import java.util.List;
import java.util.Map;

public interface IServiceInsurance {



    Insurance addInsurance(Insurance insurance);
    void deleteInsurance(int idInsurance);
    Insurance updateInsurance(Insurance insurance);
    List<Insurance> allInsurances();
    Insurance findInsuranceById(int idInsurance);

    Map<InsuranceStatus, Long> countInsurancesByStatus();

    Map<Category, Long> countInsurancesByCategory();

    Map<Integer, Long> countInsurancesByMonth();


}
