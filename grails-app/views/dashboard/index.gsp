<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Dashboard</title>
</head>
<body>
<g:if test="${flash.message}">
    <div class="alert alert-info mb-4">${flash.message}</div>
</g:if>
<g:if test="${flash.error}">
    <div class="alert alert-danger mb-4">${flash.error}</div>
</g:if>

<div class="row g-4 mb-4">
    <div class="col-12 col-lg-6 d-flex flex-column gap-4">
        <div class="d-flex flex-column flex-md-row align-items-md-end justify-content-between gap-3">
            <div>
                <p class="text-uppercase text-body-secondary small mb-1">Signed in</p>
                <h1 class="h3 mb-1">${currentUser.fullName}</h1>
                <p class="text-body-secondary mb-0">Keep your training streak moving.</p>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-body">
                <div class="d-flex flex-column flex-md-row align-items-md-start justify-content-between gap-3 mb-4">
                    <div>
                        <div class="text-uppercase text-body-secondary small mb-2">Body weight</div>
                        <h2 class="h4 mb-1">Track your weight over time</h2>
                        <p class="text-body-secondary mb-0">
                            Log a weight and date, then watch the trend line update as you add new entries.
                        </p>
                    </div>
                    <div class="text-md-end">
                        <div class="small text-body-secondary">Latest</div>
                        <div class="h4 fw-semibold mb-0">
                            <g:if test="${bodyWeightChart.hasData}">
                                ${bodyWeightChart.latest.weight} lb
                            </g:if>
                            <g:else>
                                --
                            </g:else>
                        </div>
                    </div>
                </div>

                <g:form controller="bodyWeight" action="save" method="POST" class="row g-3 align-items-end mb-4">
                    <div class="col-12 col-md-4">
                        <label class="form-label" for="weight">Weight</label>
                        <g:field type="number" name="weight" value="${bodyWeightEntry?.weight}" class="form-control" min="1" step="0.1" placeholder="180.0"/>
                    </div>
                    <div class="col-12 col-md-4">
                        <label class="form-label" for="measuredOn">Date</label>
                        <g:field type="date" name="measuredOn" value="${bodyWeightEntryDateValue}" class="form-control"/>
                    </div>
                    <div class="col-12 col-md-4">
                        <button type="submit" class="btn btn-primary w-100">Save weight</button>
                    </div>
                </g:form>

                <g:if test="${bodyWeightChart.hasData}">
                    <div class="border rounded-3 p-3 bg-body-tertiary">
                        <div class="d-flex justify-content-between small text-body-secondary mb-2">
                            <span>${bodyWeightChart.minWeight} lb</span>
                            <span>${bodyWeightChart.maxWeight} lb</span>
                        </div>
                        <svg viewBox="0 0 560 240" class="w-100" role="img" aria-label="Body weight trend chart">
                            <defs>
                                <linearGradient id="bodyWeightLineGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                                    <stop offset="0%" stop-color="#0d6efd"/>
                                    <stop offset="100%" stop-color="#0a58ca"/>
                                </linearGradient>
                            </defs>
                            <rect x="0" y="0" width="560" height="240" rx="16" fill="white" opacity="0.01"/>
                            <g opacity="0.25" stroke="#adb5bd" stroke-width="1">
                                <line x1="24" y1="20" x2="536" y2="20"/>
                                <line x1="24" y1="100" x2="536" y2="100"/>
                                <line x1="24" y1="180" x2="536" y2="180"/>
                            </g>
                            <polyline fill="none" stroke="url(#bodyWeightLineGradient)" stroke-width="4" stroke-linecap="round" stroke-linejoin="round" points="${bodyWeightChart.path}"/>
                            <g>
                                <g:each in="${bodyWeightChart.points}" var="point">
                                    <circle cx="${point.x}" cy="${point.y}" r="5" fill="#0d6efd" stroke="white" stroke-width="3"/>
                                </g:each>
                            </g>
                            <g class="small text-body-secondary">
                                <g:each in="${bodyWeightChart.points}" var="point">
                                    <text x="${point.x}" y="224" text-anchor="middle" fill="#6c757d" font-size="12">${point.label}</text>
                                </g:each>
                            </g>
                        </svg>
                        <div class="d-flex justify-content-between small text-body-secondary mt-2">
                            <span>
                                <g:if test="${bodyWeightChart.delta > 0}">
                                    +${bodyWeightChart.delta} lb from first entry
                                </g:if>
                                <g:else>
                                    <g:if test="${bodyWeightChart.delta < 0}">
                                        ${bodyWeightChart.delta} lb from first entry
                                    </g:if>
                                    <g:else>
                                        No change from first entry
                                    </g:else>
                                </g:else>
                            </span>
                            <span>${bodyWeightEntries.size()} entries</span>
                        </div>
                    </div>
                </g:if>
                <g:else>
                    <div class="border rounded-3 p-4 bg-body-tertiary text-body-secondary">
                        Add your first body weight entry to generate the chart.
                    </div>
                </g:else>
            </div>
        </div>
    </div>
    <div class="col-12 col-lg-6">
        <div class="card shadow-sm">
            <div class="card-body">
                <div class="text-uppercase text-body-secondary small mb-2">Weekly goal</div>
                <div class="display-6 fw-semibold mb-1">${weeklyWorkoutCount}</div>
                <p class="text-body-secondary mb-3">
                    workout${weeklyWorkoutCount == 1 ? '' : 's'} completed this week
                </p>
                <g:if test="${weeklyWorkoutGoal > 0}">
                    <div class="progress" style="height: .8rem;">
                        <div class="progress-bar" role="progressbar"
                             style="width: ${weeklyWorkoutProgressPercent}%"
                             aria-valuenow="${weeklyWorkoutCount}"
                             aria-valuemin="0"
                             aria-valuemax="${weeklyWorkoutGoal}">
                        </div>
                    </div>
                    <div class="d-flex justify-content-between small text-body-secondary mt-2">
                        <span>${weeklyWorkoutCount} of ${weeklyWorkoutGoal}</span>
                        <span>${weeklyWorkoutProgressPercent}%</span>
                    </div>
                    <g:link controller="profile" action="complete" class="btn btn-sm btn-outline-primary mt-3">
                        Edit goal
                    </g:link>
                </g:if>
                <g:else>
                    <p class="text-body-secondary mb-0">
                        Set a weekly workout goal in your profile to start tracking progress here.
                    </p>
                    <g:link controller="profile" action="complete" class="btn btn-sm btn-outline-primary mt-3">
                        Complete profile
                    </g:link>
                </g:else>
            </div>
        </div>
    </div>
</div>

<div class="row g-3 mb-4">
    <div class="col-12 col-md-6">
        <div class="card shadow-sm">
            <div class="card-body">
                <div class="text-body-secondary small">Exercises</div>
                <div class="display-6 fw-semibold">${exerciseCount}</div>
            </div>
        </div>
    </div>
    <div class="col-12 col-md-6">
        <div class="card shadow-sm">
            <div class="card-body">
                <div class="text-body-secondary small">Workout sessions</div>
                <div class="display-6 fw-semibold">${workoutSessionCount}</div>
            </div>
        </div>
    </div>
</div>

<div class="row g-4">
    <div class="col-12 col-lg-6">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <div class="d-flex align-items-center justify-content-between mb-3">
                    <h2 class="h5 mb-0">Recent exercises</h2>
                    <g:link controller="exercise">View all</g:link>
                </div>
                <g:if test="${recentExercises}">
                    <div class="list-group list-group-flush">
                        <g:each in="${recentExercises}" var="exercise">
                            <g:link controller="exercise" action="show" id="${exercise.id}" class="list-group-item list-group-item-action px-0">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <div class="fw-semibold">${exercise.name}</div>
                                        <div class="small text-body-secondary">${exercise.primaryMuscleGroup}</div>
                                    </div>
                                    <span class="small text-body-secondary">${exercise.equipment ?: 'Bodyweight'}</span>
                                </div>
                            </g:link>
                        </g:each>
                    </div>
                </g:if>
                <g:else>
                    <p class="text-body-secondary mb-0">No exercises yet.</p>
                </g:else>
            </div>
        </div>
    </div>

    <div class="col-12 col-lg-6 d-flex flex-column gap-4">
        <div class="card shadow-sm flex-grow-1">
            <div class="card-body">
                <div class="d-flex align-items-center justify-content-between mb-3">
                    <h2 class="h5 mb-0">Recent workouts</h2>
                    <g:link controller="workoutSession">View all</g:link>
                </div>
                <g:if test="${recentWorkoutSessions}">
                    <div class="list-group list-group-flush">
                        <g:each in="${recentWorkoutSessions}" var="workoutSession">
                            <g:link controller="workoutSession" action="show" id="${workoutSession.id}" class="list-group-item list-group-item-action px-0">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <div class="fw-semibold">${workoutSession.title}</div>
                                        <div class="small text-body-secondary">
                                            <g:formatDate date="${workoutSession.performedOn}" format="MMM d, yyyy"/>
                                        </div>
                                    </div>
                                    <span class="small text-body-secondary">
                                        ${workoutSession.durationMinutes ?: 0} min
                                    </span>
                                </div>
                            </g:link>
                        </g:each>
                    </div>
                </g:if>
                <g:else>
                    <p class="text-body-secondary mb-0">No workout sessions yet.</p>
                </g:else>
            </div>
        </div>
    </div>
</div>
</body>
</html>
