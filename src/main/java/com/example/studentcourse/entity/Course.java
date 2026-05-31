package com.example.studentcourse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String courseName;

    @Column(nullable = false, length = 40)
    private String teacher;

    @Column(nullable = false)
    private Integer credit;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer selectedCount = 0;

    public Course() {
    }

    public Course(String courseName, String teacher, Integer credit, Integer capacity) {
        this.courseName = courseName;
        this.teacher = teacher;
        this.credit = credit;
        this.capacity = capacity;
        this.selectedCount = 0;
    }

    public boolean isFull() {
        return selectedCount != null && capacity != null && selectedCount >= capacity;
    }

    public void increaseSelectedCount() {
        if (isFull()) {
            throw new IllegalStateException("课程容量已满");
        }
        selectedCount = selectedCount == null ? 1 : selectedCount + 1;
    }

    public void decreaseSelectedCount() {
        if (selectedCount == null || selectedCount <= 0) {
            selectedCount = 0;
            return;
        }
        selectedCount--;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(Integer selectedCount) {
        this.selectedCount = selectedCount;
    }
}
