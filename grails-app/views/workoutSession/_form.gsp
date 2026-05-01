<g:hasErrors bean="${workoutSession}">
    <div class="alert alert-danger">
        <g:renderErrors bean="${workoutSession}" as="list"/>
    </div>
</g:hasErrors>

<g:form controller="workoutSession" action="${workoutSession?.id ? 'update' : 'save'}" id="${workoutSession?.id}" method="POST" class="card shadow-sm">
    <div class="card-body">
        <div class="mb-3">
            <label class="form-label" for="title">Title</label>
            <g:textField name="title" value="${workoutSession?.title}" class="form-control"/>
        </div>
        <div class="mb-3">
            <label class="form-label" for="performedOn">Performed on</label>
            <g:field type="date" name="performedOn" value="${workoutSession?.performedOn ? new java.text.SimpleDateFormat('yyyy-MM-dd').format(workoutSession.performedOn) : ''}" class="form-control"/>
        </div>
        <div class="mb-3">
            <label class="form-label" for="durationMinutes">Duration minutes</label>
            <g:field type="number" name="durationMinutes" value="${workoutSession?.durationMinutes}" class="form-control" min="1"/>
        </div>
        <div class="mb-3">
            <label class="form-label" for="notes">Notes</label>
            <g:textArea name="notes" value="${workoutSession?.notes}" rows="4" class="form-control"/>
        </div>
        <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary">Save</button>
            <g:link action="index" class="btn btn-outline-secondary">Cancel</g:link>
        </div>
    </div>
</g:form>
