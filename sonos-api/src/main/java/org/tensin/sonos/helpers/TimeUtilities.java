/*
 * Copyright 2008 David Wheeler
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensin.sonos.helpers;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * A collection of date or time related methods. This class is primarily used
 * for the conversion of ISO8601 time formats.
 * TODO this class should use net.sbbi.upnp.services.ISO8601Date, rather than Joda Time
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class TimeUtilities {

    /** The Constant periodFormatter. */
    private static final PeriodFormatter periodFormatter;

    static {
        final PeriodFormatterBuilder builder = new PeriodFormatterBuilder();

        // TODO this will not work for fractional millis. which are stupid anyway.
        periodFormatter = builder.printZeroAlways().appendHours().minimumPrintedDigits(2).maximumParsedDigits(2).appendSeparator(":").minimumPrintedDigits(2).appendMinutes().appendSeparator(":").appendSecondsWithOptionalMillis().toFormatter();
    }

    /**
     * Convert duration to long.
     * 
     * @param duration
     *            the UPNP representation of the duration
     * @return the java Date <code>long</code> style representation of the
     *         duration, or -1 if duration is NOT_IMPLEMENTED.
     */
    public static long convertDurationToLong(final String duration) {
        if ("NOT_IMPLEMENTED".equals(duration) || "".equals(duration) || (duration == null)) {
            return -1;
        }
        // TODO there's also an "END_OF_MEDIA" thing...
        final Period period = periodFormatter.parsePeriod(duration);
        return period.toDurationFrom(new Instant()).getMillis();
    }

    /**
     * Parses the given ISO8601 formatted date into a java date.
     * 
     * @param date
     *            the date
     * @return the long
     */
    public static long convertISO8601DateToLong(final String date) {
        return new DateTime(date).getMillis();
    }

    /**
     * Parses the given ISO8601 Duration formatted string to a java date.
     * 
     * @param duration
     *            the duration
     * @return the long
     */
    public static long convertISO8601DurationToLong(final String duration) {
        return new Duration(duration).getMillis();
    }

    /**
     * TODO BUG durations longer than one day will not work.
     * 
     * @param duration
     *            the duration
     * @return A String of the following format: h:MM:ss[.m+]
     */
    public static String convertLongToDuration(final long duration) {
        final Period period = new Period(duration);
        return periodFormatter.print(period);
    }

    /**
     * Converts the given java date to an ISO8601 formatted date.
     * 
     * @param date
     *            the date
     * @return the string
     */
    public static String convertLongToISO8601Date(final long date) {
        return new DateTime(date).toString();
    }

    /**
     * Converts the given java date to an ISO8601 duration.
     * 
     * @param duration
     *            the duration
     * @return the string
     */
    public static String convertLongToISO8601Duration(final long duration) {
        return new Duration(duration).toString();
    }

    /**
     * Wait milli seconds.
     * 
     * @param waitTimeInMilliseconds
     *            the wait time in milliseconds
     */
    public static void waitMilliSeconds(final long waitTimeInMilliseconds) {
        try {
            Thread.sleep(waitTimeInMilliseconds);
        } catch (final InterruptedException e) {
        }
    }

    /**
     * Wait milli seconds.
     * 
     * @param waitTimeInMilliseconds
     *            the wait time in milliseconds
     */
    public static void waitSeconds(final long waitTimeInSeconds) {
        waitMilliSeconds(waitTimeInSeconds * 1000);
    }

}
