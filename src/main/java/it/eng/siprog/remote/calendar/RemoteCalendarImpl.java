package it.eng.siprog.remote.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public abstract class RemoteCalendarImpl implements RemoteCalendar {

	private static final Logger logger = Logger.getLogger(RemoteCalendarImpl.class.getName());

	// Find range of dates corresponding to start and end of week.
	// It is assumed that a week starts on Monday and ends on Sunday.
	//
	private Pair<Date, Date> getWeekRange(Date date) {
	
		// Start by normalizing passed date, removing
		// sub-day parts.
		//
		logger.info("Date passed as parameter: " + date);
		date = DateUtils.truncate(date, Calendar.DATE);
		logger.info("Cleaned up date: " + date);

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		
		int dow = gc.get(Calendar.DAY_OF_WEEK);
		dow = (dow - Calendar.MONDAY + 7) % 7;
		
		Date monday = DateUtils.addDays(date, -dow);
		logger.info("Week range start (Monday): " + monday);
		
		Date sunday = DateUtils.addDays(monday, 6);
		logger.info("Week range end (Sunday): " + sunday);
		
		return new ImmutablePair<Date, Date>(monday, sunday);
	}

	@Override
	public List<CalendarDate> getCalendarWeek(Date date) {
		
		Pair<Date, Date> week = getWeekRange(date);
		return getCalendarRange(week.getLeft(), week.getRight());
	}

	// Find range of dates corresponding to start and end of the month.
	//
	private Pair<Date, Date> getMonthRange(Date date) {
	
		// Start by normalizing passed date, removing
		// sub-day parts.
		//
		logger.info("Date passed as parameter: " + date);
		date = DateUtils.truncate(date, Calendar.DATE);
		logger.info("Cleaned up date: " + date);

		Date start = DateUtils.truncate(date, Calendar.MONTH);
		logger.info("Month range start: " + start);

		Date end = DateUtils.addMonths(start, 1);
		end = DateUtils.addDays(end, -1);
		logger.info("Month range end: " + end);
		
		return new ImmutablePair<Date, Date>(start, end);
	}

	@Override
	public List<CalendarDate> getCalendarMonth(Date date) {
		
		Pair<Date, Date> week = getMonthRange(date);
		return getCalendarRange(week.getLeft(), week.getRight());
	}
}
