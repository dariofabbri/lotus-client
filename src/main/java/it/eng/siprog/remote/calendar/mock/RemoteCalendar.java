package it.eng.siprog.remote.calendar.mock;

import it.eng.siprog.remote.calendar.CalendarDate;
import it.eng.siprog.remote.calendar.CalendarEntry;
import it.eng.siprog.remote.calendar.RemoteCalendarImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;

@Mock
public class RemoteCalendar extends RemoteCalendarImpl {

	private static final Logger logger = Logger.getLogger(RemoteCalendar.class.getName());
	
	private Map<Date, CalendarDate> calendar;
	
	public RemoteCalendar() {
		
		logger.info("Creating random based configuration for mock calendar.");
		
		GregorianCalendar gc = new GregorianCalendar(2010, 0, 1);
		Date date = gc.getTime();
		
		gc = new GregorianCalendar(2020, 11, 31);
		Date end = gc.getTime();
		
		calendar = new HashMap<Date, CalendarDate>();
		while(date.before(end)) {
			CalendarDate cd = buildRandomCalendarDate(date);
			calendar.put(date, cd);
			
			date = DateUtils.addDays(date, 1);
		}
		
		logger.info(String.format("Built %d calendar dates.", calendar.size()));
	}
	
	
	private CalendarDate buildRandomCalendarDate(Date date) {
		
		Random rnd = new Random();
		
		CalendarDate cd = new CalendarDate();
		cd.setDate(date);
		
		int noOfEntries = rnd.nextInt(10);
		for(int i = 0; i < noOfEntries; ++i) {
			
			CalendarEntry ce = new CalendarEntry();
			ce.setSubject(RandomStringUtils.randomAlphanumeric(rnd.nextInt(50)));
			ce.setCalendarDate(date);
			ce.setStartDate(date);
			ce.setEndDate(DateUtils.addDays(date, rnd.nextInt(60)));
			ce.setLocation(RandomStringUtils.randomAlphanumeric(rnd.nextInt(50)));
			ce.setBody(buildRandomText(rnd.nextInt(500), rnd));
			
			cd.addEntry(ce);
		}
		
		return cd;
	}
	
	
	private String buildRandomText(int words, Random rnd) {
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < words; ++i) {
			int length = Math.max((int)Math.floor(rnd.nextGaussian() * 6) + 7, 1);
			sb.append(RandomStringUtils.random(length, "abcdefghijklmnopqrstuvwxyz"));
			sb.append(" ");
		}
		
		return sb.toString();
	}


	@Override
	public CalendarDate getCalendarDate(Date date) {

		// Manipulate date to remove sub-day parts.
		//
		logger.info("Date passed as parameter: " + date);
		date = DateUtils.truncate(date, Calendar.DATE);
		logger.info("Cleaned up date: " + date);
		
		// Look-up date in generated calendar.
		//
		CalendarDate result = calendar.get(date);
		logger.info("getCalendarDate returning: " + result);
		
		return result;
	}

	@Override
	public List<CalendarDate> getCalendarRange(Date from, Date to) {

		// Manipulate "from" date to remove sub-day parts.
		//
		logger.info("From date passed as parameter: " + from);
		Date date = DateUtils.truncate(from, Calendar.DATE);
		logger.info("Cleaned up from date: " + date);

		// Manipulate "to" date to remove sub-day parts.
		//
		logger.info("To date passed as parameter: " + to);
		from = DateUtils.truncate(to, Calendar.DATE);
		logger.info("Cleaned up to date: " + to);

		List<CalendarDate> result = new ArrayList<CalendarDate>();
		while(!date.after(to)) {
			result.add(calendar.get(date));
			date = DateUtils.addDays(date, 1);
		}
		
		logger.info(String.format("getCalendarRange returned %d dates.", result.size()));
		return result;
	}
}
