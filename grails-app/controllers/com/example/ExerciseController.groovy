package com.example

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class ExerciseController extends AuthenticatedController {

    static allowedMethods = [save: 'POST', update: 'POST', delete: 'POST']

    def index() {
        User user = requireCurrentUser()
        if (!user) {
            return
        }
        [exerciseList: Exercise.where { owner == user }.list(sort: 'name', order: 'asc')]
    }

    def show(Long id) {
        Exercise exercise = ownedExercise(id)
        if (!exercise) {
            notFound()
            return
        }

        [exercise: exercise]
    }

    def create() {
        [exercise: new Exercise()]
    }

    def save() {
        User user = requireCurrentUser()
        if (!user) {
            return
        }

        Exercise exercise = new Exercise(params)
        exercise.owner = user

        if (!exercise.save(flush: true)) {
            render view: 'create', model: [exercise: exercise]
            return
        }

        flash.message = 'Exercise created.'
        redirect action: 'show', id: exercise.id
    }

    def edit(Long id) {
        Exercise exercise = ownedExercise(id)
        if (!exercise) {
            notFound()
            return
        }

        [exercise: exercise]
    }

    def update(Long id) {
        User user = requireCurrentUser()
        if (!user) {
            return
        }

        Exercise exercise = ownedExercise(id)
        if (!exercise) {
            notFound()
            return
        }

        exercise.properties = params
        exercise.owner = user

        if (!exercise.save(flush: true)) {
            render view: 'edit', model: [exercise: exercise]
            return
        }

        flash.message = 'Exercise updated.'
        redirect action: 'show', id: exercise.id
    }

    def delete(Long id) {
        Exercise exercise = ownedExercise(id)
        if (!exercise) {
            notFound()
            return
        }

        exercise.delete(flush: true)
        flash.message = 'Exercise deleted.'
        redirect action: 'index'
    }

    protected Exercise ownedExercise(Long id) {
        Exercise exercise = Exercise.get(id)
        User user = requireCurrentUser()
        if (!user) {
            return null
        }
        exercise?.ownerId == user.id ? exercise : null
    }

    protected void notFound() {
        flash.message = 'Exercise not found.'
        redirect action: 'index'
    }
}
