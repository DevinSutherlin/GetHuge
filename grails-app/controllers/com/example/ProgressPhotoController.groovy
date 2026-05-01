package com.example

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

@Secured(['ROLE_USER'])
class ProgressPhotoController extends AuthenticatedController {

    static allowedMethods = [save: 'POST', delete: 'POST']

    def index() {
        User me = requireCurrentUser()
        if (!me) {
            return
        }
        [progressPhotoList: ProgressPhoto.where { user == me }.list(sort: 'dateCreated', order: 'desc')]
    }

    @Transactional
    def save() {
        User me = requireCurrentUser()
        if (!me) {
            return
        }

        MultipartFile uploadedFile = request.getFile('photo')
        String caption = params.caption?.trim()

        if (!uploadedFile || uploadedFile.empty) {
            flash.error = 'Choose a PNG or image file to upload.'
            redirect action: 'index'
            return
        }

        if (!(uploadedFile.contentType in ['image/png', 'image/jpeg', 'image/jpg', 'image/gif', 'image/webp'])) {
            flash.error = 'Only image files are allowed.'
            redirect action: 'index'
            return
        }

        String extension = fileExtension(uploadedFile.contentType, uploadedFile.originalFilename)
        String storedFilename = "${UUID.randomUUID()}${extension}"
        try {
            Path uploadDir = uploadDirectory(me)
            Files.createDirectories(uploadDir)
            File targetFile = uploadDir.resolve(storedFilename).toFile()
            uploadedFile.transferTo(targetFile)

            ProgressPhoto progressPhoto = new ProgressPhoto(
                    user: me,
                    originalFilename: uploadedFile.originalFilename,
                    storedFilename: storedFilename,
                    contentType: uploadedFile.contentType,
                    fileSize: uploadedFile.size,
                    caption: caption
            )

            if (!progressPhoto.save(flush: true)) {
                targetFile.delete()
                flash.error = 'We could not save that photo.'
                render view: 'index', model: [progressPhotoList: ProgressPhoto.where { user == me }.list(sort: 'dateCreated', order: 'desc'),
                                              progressPhoto: progressPhoto]
                return
            }
        } catch (Exception e) {
            flash.error = 'We could not upload that photo.'
            redirect action: 'index'
            return
        }

        flash.message = 'Progress photo uploaded.'
        redirect action: 'index'
    }

    def image(Long id) {
        ProgressPhoto progressPhoto = ownedProgressPhoto(id)
        if (!progressPhoto) {
            notFound()
            return
        }

        File imageFile = uploadDirectory(progressPhoto.user).resolve(progressPhoto.storedFilename).toFile()
        if (!imageFile.exists()) {
            notFound()
            return
        }

        response.contentType = progressPhoto.contentType
        response.contentLengthLong = imageFile.length()
        imageFile.withInputStream { inputStream ->
            response.outputStream << inputStream
        }
        response.outputStream.flush()
    }

    @Transactional
    def delete(Long id) {
        ProgressPhoto progressPhoto = ownedProgressPhoto(id)
        if (!progressPhoto) {
            notFound()
            return
        }

        File imageFile = uploadDirectory(progressPhoto.user).resolve(progressPhoto.storedFilename).toFile()
        progressPhoto.delete(flush: true)
        if (imageFile.exists()) {
            imageFile.delete()
        }

        flash.message = 'Progress photo deleted.'
        redirect action: 'index'
    }

    protected ProgressPhoto ownedProgressPhoto(Long id) {
        ProgressPhoto progressPhoto = ProgressPhoto.get(id)
        User user = requireCurrentUser()
        if (!user) {
            return null
        }
        progressPhoto?.userId == user.id ? progressPhoto : null
    }

    protected void notFound() {
        flash.message = 'Progress photo not found.'
        redirect action: 'index'
    }

    private Path uploadDirectory(User user) {
        Paths.get(System.getProperty('user.dir'), 'uploads', 'progress-pictures', String.valueOf(user.id))
    }

    private String fileExtension(String contentType, String originalFilename) {
        switch (contentType) {
            case 'image/png':
                return '.png'
            case 'image/jpeg':
            case 'image/jpg':
                return '.jpg'
            case 'image/gif':
                return '.gif'
            case 'image/webp':
                return '.webp'
            default:
                String name = originalFilename ?: ''
                int dotIndex = name.lastIndexOf('.')
                return dotIndex > -1 ? name.substring(dotIndex) : ''
        }
    }
}
