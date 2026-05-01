package com.example

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode

@GrailsCompileStatic
@EqualsAndHashCode(includes = 'username')
class User implements Serializable {

    private static final long serialVersionUID = 1

    String username
    String password
    String email
    String displayName
    String firstName
    String lastName
    Integer weeklyWorkoutGoal

    boolean enabled = true
    boolean accountExpired = false
    boolean accountLocked = false
    boolean passwordExpired = false

    Date dateCreated
    Date lastUpdated

    static hasMany = [workoutSessions: WorkoutSession, exercises: Exercise, oAuthIDs: OAuthID, bodyWeightEntries: BodyWeightEntry]

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    static constraints = {
        username blank: false, unique: true, size: 3..50
        password blank: false, password: true
        email nullable: true, unique: true, email: true, maxSize: 255
        displayName nullable: true, maxSize: 100
        firstName nullable: true, maxSize: 50
        lastName nullable: true, maxSize: 50
        weeklyWorkoutGoal nullable: true, min: 1, max: 21
    }

    static mapping = {
        table 'app_user'
        password column: 'password_hash'
        sort dateCreated: 'desc'
    }

    @Override
    String toString() {
        fullName
    }

    String getFullName() {
        String trimmedFirstName = firstName?.trim()
        String trimmedLastName = lastName?.trim()
        if (trimmedFirstName && trimmedLastName) {
            return "${trimmedFirstName} ${trimmedLastName}"
        }

        if (trimmedFirstName) {
            return trimmedFirstName
        }

        if (trimmedLastName) {
            return trimmedLastName
        }

        displayName?.trim() ?: username
    }

    boolean isProfileComplete() {
        firstName?.trim() && lastName?.trim() && weeklyWorkoutGoal != null && weeklyWorkoutGoal > 0
    }
}
