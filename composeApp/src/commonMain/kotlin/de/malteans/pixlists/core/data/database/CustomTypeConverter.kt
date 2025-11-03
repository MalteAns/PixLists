package de.malteans.pixlists.core.data.database

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

object CustomTypeConverter {
    @TypeConverter
    fun fromLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.fromEpochDays(it) }
    }

    @TypeConverter
    fun toLocalDate(value: LocalDate?): Long? {
        return value?.toEpochDays()
    }
}