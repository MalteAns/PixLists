package de.malteans.pixlists.core.domain

enum class Months {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    val getShortStringId : String
        get() = when (this) {
            JANUARY -> "J"
            FEBRUARY -> "F"
            MARCH -> "M"
            APRIL -> "A"
            MAY -> "M"
            JUNE -> "J"
            JULY -> "J"
            AUGUST -> "A"
            SEPTEMBER -> "S"
            OCTOBER -> "O"
            NOVEMBER -> "N"
            DECEMBER -> "D"
        }

    val getDaysCount : Int
        get() = when (this) {
            JANUARY -> 31
            FEBRUARY -> 28
            MARCH -> 31
            APRIL -> 30
            MAY -> 31
            JUNE -> 30
            JULY -> 31
            AUGUST -> 31
            SEPTEMBER -> 30
            OCTOBER -> 31
            NOVEMBER -> 30
            DECEMBER -> 31
        }

    companion object {
        fun getByIndex(index: Int): Months {
            return when (index) {
                1 -> JANUARY
                2 -> FEBRUARY
                3 -> MARCH
                4 -> APRIL
                5 -> MAY
                6 -> JUNE
                7 -> JULY
                8 -> AUGUST
                9 -> SEPTEMBER
                10 -> OCTOBER
                11 -> NOVEMBER
                12 -> DECEMBER
                else -> throw IllegalArgumentException("Invalid index")
            }
        }
    }
}