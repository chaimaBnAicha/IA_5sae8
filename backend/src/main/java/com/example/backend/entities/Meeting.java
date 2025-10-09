package com.example.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String description;
    private LocalDateTime scheduledTime;
    private String meetingLink;

    @ManyToOne
    @JoinColumn(name = "tache_id")
    private Tache tache;



    @ManyToMany
    @JoinTable(
        name = "meeting_participants",
        joinColumns = @JoinColumn(name = "meeting_id"),
        inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private List<User> participants;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public MeetingStatus getStatus() {
        return this.status;
    }
}