/*
 * Copyright 2016 TomeOkin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomeokin.lspush.util;

import android.content.Context;

import com.tomeokin.lspush.R;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Date;

public class DateUtils {

    private DateUtils() { }

    public static String toDurationFriendly(Context context, Date date) {
        final Instant instant = DateTimeUtils.toInstant(date);
        LocalDateTime target = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime now = LocalDateTime.now();
        Duration duration;

        if (target.isAfter(now.minusMinutes(1))) {
            return context.getResources().getString(R.string.just_now);
        }

        if (target.isAfter(now.minusHours(1))) {
            duration = Duration.between(now, target);
            return context.getResources().getString(R.string.minutes_ago, Math.abs(duration.toMinutes()));
        }

        if (target.isAfter(now.minusDays(1))) {
            duration = Duration.between(now, target);
            return context.getResources().getString(R.string.hours_ago, Math.abs(duration.toHours()));
        }

        if (target.isAfter(now.minusWeeks(1))) {
            duration = Duration.between(target, now);
            return context.getResources().getString(R.string.days_ago, Math.abs(duration.toDays()));
        }

        // http://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
        return target.format(DateTimeFormatter.ISO_LOCAL_DATE);

        //if (now.isEqual(target)) {
        //    duration = Duration.between(now, target);
        //    return target.format(DateTimeFormatter.ISO_TIME);
        //}

        // 本周
        //TemporalField chinaField = WeekFields.of(Locale.CHINA).dayOfWeek();
        //LocalDate start = now.with(chinaField, 1);
        //LocalDate end = now.with(chinaField, 7);
        //if (target.isAfter(start) && target.isBefore(end)) {
        //    return target.format()
        //}
        // 一周内
        //LocalDate from = now.minusDays(7);
        //if (target.isAfter(from)) {
        //
        //    return duration.toDays() + " 天前";
        //}
    }

    public static Instant ofSecond(long second) {
        return DateTimeUtils.toInstant(new Date(second));
    }
}
