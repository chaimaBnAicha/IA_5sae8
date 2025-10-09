package com.example.backend.services;

import com.example.backend.entities.Meeting;
import com.example.backend.entities.MeetingStatus;
import com.example.backend.repositories.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private static final String JITSI_BASE_URL = "https://meet.jit.si/";

    @Autowired
    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public Meeting save(Meeting meeting) {
        try {
            // Generate a unique room name for Jitsi Meet
            String roomName = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String meetingLink = JITSI_BASE_URL + roomName;
            
            meeting.setMeetingLink(meetingLink);
            meeting.setStatus(MeetingStatus.SCHEDULED);
            
            return meetingRepository.save(meeting);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create meeting: " + e.getMessage());
        }
    }

    public Meeting getMeetingById(Long id) {
        return meetingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Meeting not found"));
    }

    public void cancelMeeting(Long meetingId) {
        Meeting meeting = getMeetingById(meetingId);
        meeting.setStatus(MeetingStatus.CANCELLED);
        meetingRepository.save(meeting);
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }
}