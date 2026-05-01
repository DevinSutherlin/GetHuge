package com.example

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class BodyWeightController extends AuthenticatedController {

    static allowedMethods = [save: 'POST']

    @Transactional
    def save() {
        User me = requireCurrentUser()
        if (!me) {
            return
        }

        BodyWeightEntry bodyWeightEntry = new BodyWeightEntry(params)
        bodyWeightEntry.user = me
        bodyWeightEntry.measuredOn = bodyWeightEntry.measuredOn ?: new Date()

        if (!bodyWeightEntry.save(flush: true)) {
            flash.error = 'We could not save that body weight entry.'
            redirect controller: 'dashboard', action: 'index'
            return
        }

        flash.message = 'Body weight saved.'
        redirect controller: 'dashboard', action: 'index'
    }
}
