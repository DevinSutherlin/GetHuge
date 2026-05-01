<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Progress Photos</title>
</head>
<body>
<div class="row g-4 mb-4">
    <div class="col-12 col-lg-5">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <p class="text-uppercase text-body-secondary small mb-1">Upload</p>
                <h1 class="h3 mb-2">Progress photos</h1>
                <p class="text-body-secondary mb-0">
                    Keep a visual record of your progress with PNG, JPG, GIF, or WebP uploads.
                </p>
                <g:if test="${flash.message}">
                    <div class="alert alert-info mt-3 mb-0">${flash.message}</div>
                </g:if>
                <g:if test="${flash.error}">
                    <div class="alert alert-danger mt-3 mb-0">${flash.error}</div>
                </g:if>
            </div>
        </div>
    </div>
    <div class="col-12 col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body">
                <g:form controller="progressPhoto" action="save" method="POST" enctype="multipart/form-data">
                    <div class="mb-3">
                        <label class="form-label" for="photo">Choose image</label>
                        <input type="file" class="form-control" name="photo" id="photo" accept="image/png,image/jpeg,image/jpg,image/gif,image/webp"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="caption">Caption</label>
                        <g:textField name="caption" class="form-control" placeholder="Front pose, week 6"/>
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-upload me-1"></i>Upload photo
                    </button>
                </g:form>
            </div>
        </div>
    </div>
</div>

<div class="row g-3">
    <g:if test="${progressPhotoList}">
        <g:each in="${progressPhotoList}" var="progressPhoto">
            <div class="col-12 col-sm-6 col-lg-4">
                <div class="card shadow-sm h-100 overflow-hidden">
                    <button type="button"
                            class="border-0 p-0 bg-transparent text-start"
                            data-bs-toggle="modal"
                            data-bs-target="#progressPhotoModal"
                            data-photo-src="${createLink(controller: 'progressPhoto', action: 'image', id: progressPhoto.id)}"
                            data-photo-caption="${progressPhoto.caption ?: progressPhoto.originalFilename}"
                            data-photo-date="${progressPhoto.dateCreated ? new java.text.SimpleDateFormat('MMM d, yyyy').format(progressPhoto.dateCreated) : ''}">
                        <img src="${createLink(controller: 'progressPhoto', action: 'image', id: progressPhoto.id)}"
                             class="card-img-top"
                             style="object-fit: cover; height: 240px;"
                             alt="${progressPhoto.originalFilename}"/>
                    </button>
                    <div class="card-body">
                        <div class="fw-semibold">${progressPhoto.caption ?: progressPhoto.originalFilename}</div>
                        <div class="small text-body-secondary">
                            Uploaded ${progressPhoto.dateCreated ? new java.text.SimpleDateFormat('MMM d, yyyy').format(progressPhoto.dateCreated) : ''}
                        </div>
                    </div>
                    <div class="card-footer bg-white border-top-0 pt-0">
                        <div class="d-flex justify-content-end">
                            <g:form controller="progressPhoto" action="delete" id="${progressPhoto.id}" method="POST"
                                    onsubmit="return confirm('Delete this progress photo?');">
                                <button type="submit" class="btn btn-sm btn-outline-danger">
                                    Delete
                                </button>
                            </g:form>
                        </div>
                    </div>
                </div>
            </div>
        </g:each>
    </g:if>
    <g:else>
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-body text-body-secondary">
                    No progress photos yet. Upload your first one above.
                </div>
            </div>
        </div>
    </g:else>
</div>

<div class="modal fade" id="progressPhotoModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl">
        <div class="modal-content">
            <div class="modal-header">
                <div>
                    <h2 class="modal-title h5 mb-0" id="progressPhotoModalTitle">Progress photo</h2>
                    <div class="small text-body-secondary" id="progressPhotoModalDate"></div>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-0">
                <img src="" alt="Progress photo preview" id="progressPhotoModalImage" class="img-fluid w-100"/>
            </div>
            <div class="modal-footer justify-content-start">
                <span class="text-body-secondary small" id="progressPhotoModalCaption"></span>
            </div>
        </div>
    </div>
</div>

<script>
    (function () {
        const modal = document.getElementById('progressPhotoModal');
        if (!modal) {
            return;
        }

        modal.addEventListener('show.bs.modal', function (event) {
            const trigger = event.relatedTarget;
            if (!trigger) {
                return;
            }

            const image = document.getElementById('progressPhotoModalImage');
            const title = document.getElementById('progressPhotoModalTitle');
            const caption = document.getElementById('progressPhotoModalCaption');
            const date = document.getElementById('progressPhotoModalDate');

            image.src = trigger.getAttribute('data-photo-src') || '';
            title.textContent = trigger.getAttribute('data-photo-caption') || 'Progress photo';
            caption.textContent = trigger.getAttribute('data-photo-caption') || '';
            date.textContent = trigger.getAttribute('data-photo-date') || '';
        });
    })();
</script>
</body>
</html>
