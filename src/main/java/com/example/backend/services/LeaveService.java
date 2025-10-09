package com.example.backend.services;

import com.example.backend.entities.Leave;
import com.example.backend.entities.LeaveStatus;
import com.example.backend.entities.LeaveType;
import com.example.backend.repositories.LeaveRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class LeaveService implements ILeaveService{
    LeaveRepository leaveRepository;

    @Override
    public Leave addLeave(Leave leave) {
        return leaveRepository.save(leave);
    }

    @Override
    public void deleteLeave(int idLeave) {

        leaveRepository.deleteById(idLeave);
    }

    @Override
    public Leave updateLeave(Leave leave) {
        return leaveRepository.save(leave);
    }

    @Override
    public List<Leave> allLeaves() {
        return leaveRepository.findAll();
    }
    @Override
    public Leave findLeaveById(int idLeave) {
        return leaveRepository.findById(idLeave).get();
    }

    @Override
    public boolean canAcceptLeave(int userId, Date startDate, Date endDate,
                                  LeaveType type, String documentAttachment, LeaveStatus status) {

        // Date logic
        if (startDate.after(endDate)) return false;
        if (startDate.before(new Date())) return false;

        // Medical leave check (if "Sick" leave requires a document)
        if (type == LeaveType.Sick && (documentAttachment == null || documentAttachment.isEmpty())) {
            return false;
        }

        // Check for overlap with other approved leaves
        return leaveRepository.canAcceptLeaveRequest(
                userId,
                LeaveStatus.Approved,
                startDate,
                endDate
        );
    }


   @Override
    public Map<String, Long> getLeaveCountByType() {
        List<Object[]> results = leaveRepository.getLeaveCountByType();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            // row[0] is the LeaveType enum, row[1] is the count
            String type = ((LeaveType) row[0]).name();
            Long count  = (Long) row[1];
            map.put(type, count);
        }
        return map;
    }

    @Override
    public Map<String, Double> getAverageLeaveDurationPerType() {
        List<Object[]> results = leaveRepository.getAverageLeaveDurationPerType();
        Map<String, Double> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            // row[0] is the LeaveType enum, row[1] is the average (Number)
            String type       = ((LeaveType) row[0]).name();
            Double avgDuration = ((Number) row[1]).doubleValue();
            map.put(type, avgDuration);
        }
        return map;
    }

    @Override
    public Map<Integer, Long> getApprovedLeavesByMonth() {
        List<Object[]> results = leaveRepository. getApprovedLeavesByMonth();
        Map<Integer, Long> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            // row[0] is the month (Integer), row[1] is the count
            Integer month = (Integer) row[0];
            Long    cnt   = (Long) row[1];
            map.put(month, cnt);
        }
        return map;
    }


}
