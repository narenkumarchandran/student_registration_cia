package com.university;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @Test
    void shouldRegisterStudentForValidCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectInactiveCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                false,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }

    @Test
    void shouldRejectMissingPrerequisite() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
    }

    @Test
    void shouldAllowRegistrationWhenPrerequisiteIsCompleted() {

        Student student = new Student("S101", "Anita");
        student.addCompletedPrerequisite("CS301");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS401"));
    }

    @Test
    void shouldRejectDuplicateRegistration() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));

        boolean secondAttempt = service.register(student, course);

        assertFalse(secondAttempt);
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCreditLimitIsExceeded() {

        Student student = new Student("S101", "Anita");

        // Simulate 20 existing credits
        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }
@Test
void shouldRejectRegistrationWhenCourseIsFull() {
    Student student = new Student("S102", "Rahul");
    Course course = new Course("CS301", "Data Structures", 4, 1, true, null);

    course.incrementEnrollment(); // Fill the only available seat

    RegistrationService service = new RegistrationService();
    boolean result = service.register(student, course);

    assertFalse(result);
    assertFalse(student.isRegistered("CS301"));
    assertEquals(1, course.getCurrentEnrollment());
    assertEquals(0, student.getTotalCredits());
}

@Test
void shouldAllowRegistrationWhenOneSeatRemains() {
    Student student = new Student("S103", "Priya");
    Course course = new Course("CS302", "Algorithms", 4, 2, true, null);

    course.incrementEnrollment(); // One of two seats is occupied

    RegistrationService service = new RegistrationService();
    boolean result = service.register(student, course);

    assertTrue(result);
    assertTrue(student.isRegistered("CS302"));
    assertEquals(2, course.getCurrentEnrollment());
    assertEquals(4, student.getTotalCredits());
}
    @Test
    void shouldRecordSuccessfulRegistrationInAuditLog() {
        Student student = new Student("S104", "Kumar");
        Course course = new Course("CS303", "Operating Systems", 4, 30, true, null);
        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertEquals(1, service.getAuditLog().size());
        assertTrue(service.getAuditLog().get(0).contains("S104"));
        assertTrue(service.getAuditLog().get(0).contains("CS303"));
    }

    @Test
    void shouldNotRecordRejectedRegistrationInAuditLog() {
        Student student = new Student("S105", "Meena");
        Course course = new Course("CS304", "Computer Networks", 4, 1, true, null);
        course.incrementEnrollment(); // Course is already full

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertTrue(service.getAuditLog().isEmpty());
    }

    @Test
    void failedAttemptShouldNotChangePreviousAuditEntry() {
        Student student = new Student("S106", "Arun");
        Course course = new Course("CS305", "Database Systems", 4, 30, true, null);
        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));
        assertEquals(1, service.getAuditLog().size());

        String originalEntry = service.getAuditLog().get(0);

        // Attempt to register for the same course again.
        assertFalse(service.register(student, course));

        assertEquals(1, service.getAuditLog().size());
        assertEquals(originalEntry, service.getAuditLog().get(0));
    }
}