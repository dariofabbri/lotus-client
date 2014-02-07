package it.eng.siprog.remote.calendar;

import java.util.Date;
import java.util.List;

public interface RemoteCalendar {

	CalendarDate getCalendarDate(Date date);
	List<CalendarDate> getCalendarRange(Date from, Date to);
	List<CalendarDate> getCalendarWeek(Date date);
	List<CalendarDate> getCalendarMonth(Date date);
}
