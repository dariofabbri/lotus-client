package it.eng.siprog.remote.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

public abstract class RemoteCalendarTest {

	public abstract RemoteCalendar getRemoteCalendar();
	
	@Test
	public void testGetCalendarDate() {
		
		CalendarDate cd = getRemoteCalendar().getCalendarDate(new Date());
		
		Assert.assertNotNull(cd);
		System.out.println(cd);
	}

	@Test
	public void testGetCalendarRange() {
		
		Date from = DateUtils.addDays(new Date(), -5);
		Date to = DateUtils.addDays(from, 10);
		List<CalendarDate> calendarDates = getRemoteCalendar().getCalendarRange(from, to);
		
		Assert.assertNotNull(calendarDates);
		Assert.assertEquals(calendarDates.size(), 11);
		System.out.println(calendarDates);
	}

	@Test
	public void testGetCalendarWeek() {
		
		List<CalendarDate> calendarDates = getRemoteCalendar().getCalendarWeek(new Date());
		
		Assert.assertNotNull(calendarDates);
		Assert.assertEquals(calendarDates.size(), 7);
		System.out.println(calendarDates);
	}

	@Test
	public void testGetCalendarWeekWide() {
		
		Date date = new Date();
		
		for(int i = 0; i < 10; ++i) {
			List<CalendarDate> calendarDates = getRemoteCalendar().getCalendarWeek(date);
			
			Assert.assertNotNull(calendarDates);
			Assert.assertEquals(calendarDates.size(), 7);
			System.out.println(calendarDates);
			
			date = DateUtils.addDays(date, 1);
		}
	}

	@Test
	public void testGetCalendarMonth() {
		
		Date date = new Date();
		List<CalendarDate> calendarDates = getRemoteCalendar().getCalendarMonth(date);
		
		Assert.assertNotNull(calendarDates);
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		Assert.assertEquals(calendarDates.size(), gc.getActualMaximum(Calendar.DAY_OF_MONTH));

		System.out.println(calendarDates);
	}
}
