package com.example

import grails.plugin.springsecurity.annotation.Secured
import java.text.SimpleDateFormat

import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

@Secured(['ROLE_USER'])
class DashboardController extends AuthenticatedController {

    def index() {
        User me = requireCurrentUser()
        if (!me) {
            return
        }

        if (requiresProfileCompletion(me)) {
            redirect controller: 'profile', action: 'complete'
            return
        }

        ZoneId zone = ZoneId.systemDefault()
        LocalDate startOfWeekDate = LocalDate.now(zone).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        Date startOfWeek = Date.from(startOfWeekDate.atStartOfDay(zone).toInstant())
        Date startOfNextWeek = Date.from(startOfWeekDate.plusWeeks(1).atStartOfDay(zone).toInstant())
        long weeklyWorkoutCount = (WorkoutSession.executeQuery(
                'select count(ws.id) from WorkoutSession ws where ws.user = :user and ws.performedOn >= :startOfWeek and ws.performedOn < :startOfNextWeek',
                [user: me, startOfWeek: startOfWeek, startOfNextWeek: startOfNextWeek]
        )[0] as Number).longValue()
        int weeklyWorkoutGoal = me.weeklyWorkoutGoal ?: 0
        int weeklyWorkoutProgressPercent = weeklyWorkoutGoal > 0 ? Math.min(100, Math.round((weeklyWorkoutCount * 100.0) / weeklyWorkoutGoal) as int) : 0
        List<BodyWeightEntry> bodyWeightEntries = BodyWeightEntry.where { user == me }.list(sort: 'measuredOn', order: 'asc')
        Map bodyWeightChart = buildBodyWeightChart(bodyWeightEntries)

        [
                currentUser                  : me,
                exerciseCount                : Exercise.countByOwner(me),
                workoutSessionCount          : WorkoutSession.countByUser(me),
                weeklyWorkoutCount           : weeklyWorkoutCount,
                weeklyWorkoutGoal            : weeklyWorkoutGoal,
                weeklyWorkoutProgressPercent : weeklyWorkoutProgressPercent,
                bodyWeightEntries            : bodyWeightEntries,
                bodyWeightChart              : bodyWeightChart,
                bodyWeightEntry              : new BodyWeightEntry(measuredOn: new Date()),
                bodyWeightEntryDateValue     : formatDate(new Date()),
                recentExercises              : Exercise.where { owner == me }.list(max: 5, sort: 'lastUpdated', order: 'desc'),
                recentWorkoutSessions        : WorkoutSession.where { user == me }.list(max: 5, sort: 'performedOn', order: 'desc')
        ]
    }

    private boolean requiresProfileCompletion(User user) {
        OAuthID.findByUserAndProvider(user, 'google') && !user.profileComplete
    }

    private String formatDate(Date date) {
        new SimpleDateFormat('yyyy-MM-dd').format(date)
    }

    private Map buildBodyWeightChart(List<BodyWeightEntry> entries) {
        if (!entries) {
            return [hasData: false, points: [], path: '', minWeight: null, maxWeight: null, latest: null, delta: null]
        }

        BigDecimal minWeight = entries*.weight.min() as BigDecimal
        BigDecimal maxWeight = entries*.weight.max() as BigDecimal
        if (minWeight == maxWeight) {
            minWeight = minWeight - 1
            maxWeight = maxWeight + 1
        }

        int width = 560
        int height = 240
        int left = 24
        int right = 24
        int top = 20
        int bottom = 36
        int innerWidth = width - left - right
        int innerHeight = height - top - bottom
        int count = entries.size()

        List<Map> points = []
        entries.eachWithIndex { BodyWeightEntry entry, int index ->
            double x = count == 1 ? left + (innerWidth / 2.0) : left + (index * (innerWidth / (double) (count - 1)))
            BigDecimal normalized = ((entry.weight - minWeight) / (maxWeight - minWeight)) as BigDecimal
            double y = top + innerHeight - (normalized.doubleValue() * innerHeight)
            points << [
                    x     : Math.round(x as float),
                    y     : Math.round(y as float),
                    weight: entry.weight.setScale(1, RoundingMode.HALF_UP),
                    label : entry.measuredOn?.format('MMM d')
            ]
        }

        String path = points.collect { "${it.x},${it.y}" }.join(' ')
        BigDecimal delta = count > 1 ? (entries.last().weight - entries.first().weight).setScale(1, RoundingMode.HALF_UP) : 0.0G

        [
                hasData  : true,
                points   : points,
                path     : path,
                minWeight: minWeight.setScale(1, RoundingMode.HALF_UP),
                maxWeight: maxWeight.setScale(1, RoundingMode.HALF_UP),
                latest   : entries.last(),
                delta    : delta
        ]
    }
}
