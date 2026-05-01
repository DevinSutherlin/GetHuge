<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Workout Sessions</title>
</head>
<body>
<div class="d-flex justify-content-between align-items-center mb-3">
    <div>
        <h1 class="h3 mb-1">Workout Sessions</h1>
        <p class="text-body-secondary mb-0">Sessions you own.</p>
    </div>
    <g:link action="create" class="btn btn-primary">
        <i class="bi bi-plus-lg me-1"></i>New session
    </g:link>
</div>

<div class="card shadow-sm">
    <div class="card-body p-0">
        <table class="table mb-0 align-middle">
            <thead>
            <tr>
                <th>Title</th>
                <th>Date</th>
                <th>Duration</th>
                <th class="text-end">Actions</th>
            </tr>
            </thead>
            <tbody>
            <g:if test="${workoutSessionList}">
                <g:each in="${workoutSessionList}" var="workoutSession">
                    <tr>
                        <td>${workoutSession.title}</td>
                        <td>${workoutSession.performedOn ? new java.text.SimpleDateFormat('MMM d, yyyy').format(workoutSession.performedOn) : ''}</td>
                        <td>${workoutSession.durationMinutes ?: 0} min</td>
                        <td class="text-end">
                            <div class="btn-group btn-group-sm" role="group">
                                <g:link action="show" id="${workoutSession.id}" class="btn btn-outline-secondary">View</g:link>
                                <g:link action="edit" id="${workoutSession.id}" class="btn btn-outline-primary">Edit</g:link>
                            </div>
                        </td>
                    </tr>
                </g:each>
            </g:if>
            <g:else>
                <tr>
                    <td colspan="4" class="text-body-secondary p-4">No workout sessions yet.</td>
                </tr>
            </g:else>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
