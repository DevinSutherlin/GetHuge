package com.example

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class WorkoutSessionController extends AuthenticatedController {

    static allowedMethods = [save: 'POST', update: 'POST', delete: 'POST']

    def index() {
        User me = requireCurrentUser()
        if (!me) {
            return
        }
        [workoutSessionList: WorkoutSession.list().findAll { it.userId == me.id }.sort { a, b -> b.performedOn <=> a.performedOn }]
    }

    def show(Long id) {
        WorkoutSession workoutSession = ownedWorkoutSession(id)
        if (!workoutSession) {
            notFound()
            return
        }

        [workoutSession: workoutSession]
    }

    def create() {
        [workoutSession: new WorkoutSession(performedOn: new Date())]
    }

    def save() {
        User me = requireCurrentUser()
        if (!me) {
            return
        }

        WorkoutSession workoutSession = new WorkoutSession(params)
        workoutSession.user = me

        if (!workoutSession.save(flush: true)) {
            render view: 'create', model: [workoutSession: workoutSession]
            return
        }

        flash.message = 'Workout session created.'
        redirect action: 'show', id: workoutSession.id
    }

    def edit(Long id) {
        WorkoutSession workoutSession = ownedWorkoutSession(id)
        if (!workoutSession) {
            notFound()
            return
        }

        [workoutSession: workoutSession]
    }

    def update(Long id) {
        User me = requireCurrentUser()
        if (!me) {
            return
        }

        WorkoutSession workoutSession = ownedWorkoutSession(id)
        if (!workoutSession) {
            notFound()
            return
        }

        workoutSession.properties = params
        workoutSession.user = me

        if (!workoutSession.save(flush: true)) {
            render view: 'edit', model: [workoutSession: workoutSession]
            return
        }

        flash.message = 'Workout session updated.'
        redirect action: 'show', id: workoutSession.id
    }

    def delete(Long id) {
        WorkoutSession workoutSession = ownedWorkoutSession(id)
        if (!workoutSession) {
            notFound()
            return
        }

        workoutSession.delete(flush: true)
        flash.message = 'Workout session deleted.'
        redirect action: 'index'
    }

    protected WorkoutSession ownedWorkoutSession(Long id) {
        WorkoutSession workoutSession = WorkoutSession.get(id)
        User user = requireCurrentUser()
        if (!user) {
            return null
        }
        workoutSession?.userId == user.id ? workoutSession : null
    }

    protected void notFound() {
        flash.message = 'Workout session not found.'
        redirect action: 'index'
    }
}
