package com.Backend.model;



import jakarta.persistence.*;

import java.time.DayOfWeek;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore; // Prevent infinite recursion



@Entity

@Table(name = "availabilities")

public class Availability {

   

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

   

    @ManyToOne

    @JoinColumn(name = "user_id")

    @JsonIgnore // Important: Don't print the full user again when fetching availability

    private User user;

   

    @Enumerated(EnumType.STRING)

    private DayOfWeek dayOfWeek;

   

    private LocalTime startTime;

    private LocalTime endTime;



    // Getters and Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }

    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }

    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }

    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

}