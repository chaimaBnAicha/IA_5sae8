package com.example.backend.controllers;


import com.example.backend.entities.Leave;
import com.example.backend.entities.LeaveStatus;
import com.example.backend.entities.LeaveType;
import com.example.backend.services.EmailService;
import com.example.backend.services.ILeaveService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/leave")
@CrossOrigin(origins = "http://localhost:4200",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*")
public class LeaveController {


    ILeaveService leaveService;
    private final EmailService emailService;

    @GetMapping("/retrieve-all-leave")
    public List<Leave> getLeaves() {
        List<Leave> listLeaves = leaveService.allLeaves();
        return listLeaves;
    }


    @PostMapping("/add-leave")
    public Leave addLeave(@RequestBody Leave l) {
        Leave leave = leaveService.addLeave(l);
        return leave;
    }
    @DeleteMapping("/remove-leave/{leave-id}")
    public void removeLeave(@PathVariable("leave-id") int Id) {
        leaveService.deleteLeave(Id);
    }

    @PutMapping("/modify-Leave")
    public Leave modifyLeave(@RequestBody Leave l) {
        Leave leave = leaveService.updateLeave(l);
        return leave;
    }
    @GetMapping("/retrieve-leave/{leave-id}")
    public Leave retrieveLeave(@PathVariable("leave-id") int Id) {
        Leave leave = leaveService.findLeaveById(Id);
        return leave;
    }
    @GetMapping("/can-accept")
    public boolean canAcceptLeave(
            @RequestParam int userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam LeaveType type,
            @RequestParam(required = false) String document,
            @RequestParam LeaveStatus status
    ) {
        return leaveService.canAcceptLeave(userId, startDate, endDate, type, document, status);
    }
    @PostMapping("/send")
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam(defaultValue = "false") boolean isHtml) {

        if (isHtml) {
            emailService.sendHtmlEmail1(to, subject, body);
        } else {
            emailService.sendEmail1(to, subject, body);
        }

        return "Email sent successfully!";
    }

    @GetMapping("/leave-count-by-type")
    public Map<String, Long> getLeaveCountByType() {
        return leaveService.getLeaveCountByType();
    }

    @GetMapping("/average-leave-duration")
    public Map<String, Double> getAverageLeaveDurationPerType() {
        return leaveService.getAverageLeaveDurationPerType();
    }

    @GetMapping("/leaves-by-month")
    public Map<Integer, Long> getApprovedLeavesByMonth() {
        return leaveService.getApprovedLeavesByMonth();
    }


}
