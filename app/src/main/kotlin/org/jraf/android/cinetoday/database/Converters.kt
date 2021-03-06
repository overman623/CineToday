/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2017-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.android.cinetoday.database

import androidx.room.TypeConverter
import java.util.Date

object Converters {
    object DateConverter {
        @JvmStatic
        @TypeConverter
        fun longToDate(value: Long?) = value?.let { Date(value) }

        @JvmStatic
        @TypeConverter
        fun dateToLong(date: Date?) = date?.time
    }

    object ListConverter {
        @JvmStatic
        @TypeConverter
        fun stringArrayToString(value: Array<String>?) = value?.let { value.joinToString("|") }

        @JvmStatic
        @TypeConverter
        fun stringToStringArray(string: String?) = string?.split("|")?.toTypedArray()
    }

}