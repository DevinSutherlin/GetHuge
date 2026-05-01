package com.example

import grails.testing.gorm.DataTest
import spock.lang.Specification

class DomainConstraintsSpec extends Specification implements DataTest {

    @Override
    Class<?>[] getDomainClassesToMock() {
        [User, Role, UserRole, WorkoutSession, Exercise, ExerciseSet, ProgressPhoto, BodyWeightEntry] as Class<?>[]
    }

    void "user requires core identity fields and basic auth flags default correctly"() {
        when:
        def user = new User(
                username: 'lifter1',
                email: 'lifter1@example.com',
                password: 'strongpass',
                displayName: 'Lifter One'
        )

        def invalidUser = new User(
                username: '',
                email: 'not-an-email',
                password: 'short'
        )

        then:
        user.validate()
        user.enabled
        !user.accountExpired
        !user.accountLocked
        !user.passwordExpired

        !invalidUser.validate()
        invalidUser.errors.getFieldError('username')
        invalidUser.errors.getFieldError('email')
        !invalidUser.errors.getFieldError('displayName')
    }

    void "user builds a full name and profile completion state"() {
        given:
        def user = new User(
                username: 'lifter3',
                email: 'lifter3@example.com',
                password: 'strongpass',
                firstName: 'Jordan',
                lastName: 'Miles',
                weeklyWorkoutGoal: 4
        )

        expect:
        user.fullName == 'Jordan Miles'
        user.toString() == 'Jordan Miles'
        user.profileComplete
    }

    void "user exposes assigned authorities through the join domain"() {
        given:
        def user = new User(
                username: 'lifter2',
                email: 'lifter2@example.com',
                password: 'strongpass',
                displayName: 'Lifter Two'
        ).save(validate: false)
        def role = new Role(authority: 'ROLE_USER').save(validate: false)
        new UserRole(user: user, role: role).save(validate: false)

        expect:
        user.authorities*.authority as Set == ['ROLE_USER'] as Set
    }

    void "workout session allows optional notes and duration"() {
        given:
        def user = new User(
                username: 'coach',
                email: 'coach@example.com',
                password: 'strongpass',
                displayName: 'Coach'
        )

        when:
        def session = new WorkoutSession(
                user: user,
                title: 'Push Day',
                performedOn: new Date()
        )

        then:
        session.validate()
    }

    void "exercise set enforces positive set metrics and supported units"() {
        given:
        def owner = new User(
                username: 'owner1',
                email: 'owner1@example.com',
                password: 'strongpass',
                displayName: 'Owner One'
        )
        def exercise = new Exercise(owner: owner, name: 'Bench Press', primaryMuscleGroup: 'Chest')
        def session = new WorkoutSession(
                user: new User(
                        username: 'sam',
                        email: 'sam@example.com',
                        password: 'strongpass',
                        displayName: 'Sam'
                ),
                title: 'Upper Body',
                performedOn: new Date()
        )

        when:
        def invalidSet = new ExerciseSet(
                workoutSession: session,
                exercise: exercise,
                setNumber: 0,
                reps: 0,
                weight: -10.0G,
                unit: 'stone'
        )

        then:
        !invalidSet.validate()
        invalidSet.errors.getFieldError('setNumber')
        invalidSet.errors.getFieldError('reps')
        invalidSet.errors.getFieldError('weight')
        invalidSet.errors.getFieldError('unit')
    }

    void "progress photo requires a valid image file record"() {
        given:
        def user = new User(
                username: 'photo1',
                email: 'photo1@example.com',
                password: 'strongpass',
                displayName: 'Photo One'
        )

        when:
        def photo = new ProgressPhoto(
                user: user,
                originalFilename: 'progress.png',
                storedFilename: 'abc.png',
                contentType: 'image/png',
                fileSize: 1234L
        )

        then:
        photo.validate()
    }

    void "body weight entry requires a date and a positive weight"() {
        given:
        def user = new User(
                username: 'scale1',
                email: 'scale1@example.com',
                password: 'strongpass',
                displayName: 'Scale One'
        )

        expect:
        new BodyWeightEntry(
                user: user,
                measuredOn: new Date(),
                weight: 182.4G
        ).validate()
    }
}
