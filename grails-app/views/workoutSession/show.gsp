<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${workoutSession.title}</title>
</head>
<body>
<div class="d-flex justify-content-between align-items-center mb-3">
    <div>
        <h1 class="h3 mb-1">${workoutSession.title}</h1>
        <p class="text-body-secondary mb-0">${workoutSession.performedOn ? new java.text.SimpleDateFormat('MMM d, yyyy').format(workoutSession.performedOn) : ''}</p>
    </div>
    <div class="btn-group">
        <g:link action="edit" id="${workoutSession.id}" class="btn btn-outline-primary">Edit</g:link>
        <g:form action="delete" id="${workoutSession.id}" method="POST" style="display:inline">
            <button type="submit" class="btn btn-outline-danger">Delete</button>
        </g:form>
    </div>
</div>

<div class="card shadow-sm mb-3">
    <div class="card-body">
        <dl class="row mb-0">
            <dt class="col-sm-3">Duration</dt>
            <dd class="col-sm-9">${workoutSession.durationMinutes ?: 0} minutes</dd>
            <dt class="col-sm-3">Notes</dt>
            <dd class="col-sm-9">${workoutSession.notes ?: 'No notes yet.'}</dd>
        </dl>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">
        <h2 class="h5 mb-3">Exercise sets</h2>
        <g:if test="${workoutSession.exerciseSets}">
            <table class="table mb-0 align-middle">
                <thead>
                <tr>
                    <th>Exercise</th>
                    <th>Set</th>
                    <th>Reps</th>
                    <th>Weight</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${workoutSession.exerciseSets.sort { it.setNumber }}" var="exerciseSet">
                    <tr>
                        <td>${exerciseSet.exercise?.name}</td>
                        <td>${exerciseSet.setNumber}</td>
                        <td>${exerciseSet.reps}</td>
                        <td>${exerciseSet.weight}${exerciseSet.unit}</td>
                        <td>${exerciseSet.completed ? 'Complete' : 'Pending'}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </g:if>
        <g:else>
            <p class="text-body-secondary mb-0">No sets logged yet.</p>
        </g:else>
    </div>
</div>
</body>
</html>
