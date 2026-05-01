package com.example

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class ProfileController extends AuthenticatedController {

    static allowedMethods = [save: 'POST']

    def complete() {
        User user = requireCurrentUser()
        if (!user) {
            return
        }
        [profileCompletionCommand: new ProfileCompletionCommand(
                firstName: user.firstName,
                lastName: user.lastName,
                weeklyWorkoutGoal: user.weeklyWorkoutGoal
        )]
    }

    @Transactional
    def save(ProfileCompletionCommand profileCompletionCommand) {
        User user = requireCurrentUser()
        if (!user) {
            return
        }

        if (profileCompletionCommand.hasErrors()) {
            render view: 'complete', model: [profileCompletionCommand: profileCompletionCommand]
            return
        }

        user.firstName = profileCompletionCommand.firstName?.trim()
        user.lastName = profileCompletionCommand.lastName?.trim()
        user.displayName = "${user.firstName} ${user.lastName}".trim()
        user.weeklyWorkoutGoal = profileCompletionCommand.weeklyWorkoutGoal

        if (!user.save(flush: true)) {
            render view: 'complete', model: [profileCompletionCommand: profileCompletionCommand]
            return
        }

        flash.message = 'Your profile is complete.'
        redirect controller: 'dashboard', action: 'index'
    }
}
