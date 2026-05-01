package com.example

class BodyWeightEntry {

    User user
    Date measuredOn = new Date()
    BigDecimal weight

    Date dateCreated
    Date lastUpdated

    static belongsTo = [user: User]

    static constraints = {
        user nullable: false
        measuredOn nullable: false, unique: 'user'
        weight nullable: false, min: 1.0G, scale: 2
    }

    static mapping = {
        sort measuredOn: 'asc'
        weight scale: 2
    }

    String toString() {
        "${weight} on ${measuredOn?.format('MMM d, yyyy')}"
    }
}
