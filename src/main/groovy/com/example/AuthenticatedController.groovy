package com.example

import grails.plugin.springsecurity.SpringSecurityService

abstract class AuthenticatedController {

    SpringSecurityService springSecurityService

    protected User currentUser() {
        springSecurityService.currentUser as User
    }

    protected User requireCurrentUser() {
        User user = currentUser()
        if (!user) {
            redirect(controller: 'login', action: 'auth')
            return null
        }

        user
    }
}
