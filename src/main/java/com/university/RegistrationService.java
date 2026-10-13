package com.university;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistrationService {

    private static final int MAX_CREDITS = 20;

    private final List<String> auditLog = new ArrayList<>();

    public boolean register(Student student, Course course) {

        if (!course.isActive()) {
            return false;
        }

        if (course.getCurrentEnrollment() >= course.getCapacity()) {
            return false;
        }

        if (student.isRegistered(course.getCourseCode())) {
            return false;
        }

        String prerequisite = course.getPrerequisite();

        if (prerequisite != null
                && !prerequisite.isBlank()
                && !student.hasCompletedPrerequisite(prerequisite)) {
            return false;
        }

        if (student.getTotalCredits() + course.getCredits() > MAX_CREDITS) {
            return false;
        }

        student.addRegistration(
                course.getCourseCode(),
                course.getCredits());

        course.incrementEnrollment();

        // Record only successful registrations.
        auditLog.add(
                "Student ID: " + student.getStudentId()
                + ", Course Code: " + course.getCourseCode());

        return true;
    }

    public List<String> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }
}