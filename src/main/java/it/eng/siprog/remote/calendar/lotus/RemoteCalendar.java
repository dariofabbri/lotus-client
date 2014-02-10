package it.eng.siprog.remote.calendar.lotus;

import it.eng.siprog.remote.calendar.CalendarDate;
import it.eng.siprog.remote.calendar.CalendarEntry;
import it.eng.siprog.remote.calendar.RemoteCalendarImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Lotus
public class RemoteCalendar extends RemoteCalendarImpl {

	private static final Logger logger = Logger.getLogger(RemoteCalendar.class.getName());

	private String server;
	private String username;
	private String password;
	private String calendarDatabase;
	
	
	public RemoteCalendar() {
		
		InputStream is = this.getClass().getResourceAsStream("/META-INF/lotus.properties");
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			String msg = "Exception caught while reading lotus.properties file.";
			logger.log(Level.SEVERE, msg, e);
			throw new RuntimeException(msg, e);
		}
		
		server = props.getProperty("server");
		username = props.getProperty("username");
		password = props.getProperty("password");
		calendarDatabase = props.getProperty("calendar_database");
	}
	
	@Override
	public CalendarDate getCalendarDate(Date date) {
		
		List<CalendarDate> list = getCalendarRange(date, date);
		if(list.size() > 1) {
			throw new RuntimeException("Unexpected number of dates returned: " + list.size());
		}

		return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public List<CalendarDate> getCalendarRange(Date from, Date to) {

		// Get connection.
		//
		Pair<Session, Database> connection = getConnection();
		
		// Manipulate "from" date to remove sub-day parts.
		//
		logger.info("From date passed as parameter: " + from);
		from = DateUtils.truncate(from, Calendar.DATE);
		logger.info("Cleaned up from date: " + from);

		// Manipulate "to" date to remove sub-day parts.
		//
		logger.info("To date passed as parameter: " + to);
		to = DateUtils.truncate(to, Calendar.DATE);
		to = DateUtils.addDays(to, 1);
		logger.info("Cleaned up to date: " + to);

		
		StringBuilder searchTerm = new StringBuilder();
		searchTerm
			.append("@IsAvailable(CalendarDateTime) ")
			.append("& ")
			.append("CalendarDateTime >= ")
			.append(makeLotusFormulaDate(from))
			.append(" & ")
			.append("CalendarDateTime < ")
			.append(makeLotusFormulaDate(to));

		// Prepare result object.
		//
		Map<Date, CalendarDate> result = new TreeMap<Date, CalendarDate>();

		// Execute search on remote database.
		//
		DocumentCollection dc;
		try {
			dc = connection.getRight().search(searchTerm.toString());
		} catch (NotesException e) {
			String msg = "Exception caught while performing search.";
			logger.log(Level.SEVERE, msg, e);
			throw new RuntimeException(msg, e);
		}
		
		// Start iteration on query result.
		//
		Document doc;
		try {
			doc = dc.getFirstDocument();
		} catch (NotesException e) {
			String msg = "Exception caught while accessing first document in results.";
			logger.log(Level.SEVERE, msg, e);
			throw new RuntimeException(msg, e);
		}
		
		// Iterate through results.
		//
		while(doc != null) {

			// Get calendar date and time (the day of the entry in the calendar).
			//
			Date calendarDateTime;
			try {
				calendarDateTime = toSingleDate(doc, "CalendarDateTime");
			} catch (NotesException e) {
				String msg = "Exception caught while accessing CalendarDateTime item in Lotus document.";
				logger.log(Level.SEVERE, msg, e);
				throw new RuntimeException(msg, e);
			}
			
			// Cut sub-day parts in extracted date.
			//
			Date date = DateUtils.truncate(from, Calendar.DATE);
			
			// Look-up entry in result map.
			//
			CalendarDate calendarDate = result.get(date);
			if(calendarDate == null) {
				calendarDate = new CalendarDate();
				result.put(date, calendarDate);
			}
			
			// Create entry corresponding to current Lotus document.
			//
			CalendarEntry calendarEntry = new CalendarEntry();
			try {
				calendarEntry.setSubject(doc.getItemValueString("Subject"));
				calendarEntry.setCalendarDate(calendarDateTime);
				calendarEntry.setStartDate(toSingleDate(doc, "StartDate"));
				calendarEntry.setEndDate(toSingleDate(doc, "EndDate"));
				calendarEntry.setLocation(doc.getItemValueString("Location"));
				calendarEntry.setBody(doc.getItemValueString("Body"));
			} catch (NotesException e) {
				String msg = "Exception caught while accessing detail items in Lotus document.";
				logger.log(Level.SEVERE, msg, e);
				throw new RuntimeException(msg, e);
			}

			// Release Lotus object for current document.
			//
			try {
				doc.recycle();
			} catch (NotesException e) {
				String msg = "Exception caught while recycling document.";
				logger.log(Level.SEVERE, msg, e);
				throw new RuntimeException(msg, e);
			}
			
			// Move onto next document in search result set.
			//
			try {
				doc = dc.getNextDocument();
			} catch (NotesException e) {
				String msg = "Exception caught while accessing following document in result.";
				logger.log(Level.SEVERE, msg, e);
				throw new RuntimeException(msg, e);
			}
		}
		
		// Release Lotus connection objects to avoid memory leaks.
		//
		releaseConnection(connection);

		// Convert result to a list.
		//
		List<CalendarDate> list = new ArrayList<CalendarDate>();
		for(Map.Entry<Date, CalendarDate> entry : result.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	private Date toSingleDate(Document doc, String itemName) throws NotesException {
		
		if(!doc.hasItem(itemName)) {
			return null;
		}
		
		Item item = doc.getFirstItem(itemName);
		if(item.getType() != Item.DATETIMES) {
			return null;
		}
		
		Vector times = doc.getItemValueDateTimeArray(itemName);
		DateTime dt = (DateTime)times.get(0);
		
		return dt.toJavaDate();
	}

	
	private String makeLotusFormulaDate(Date date) {

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		
		StringBuilder sb = new StringBuilder();
		sb.append("@Date(")
				.append(String.format("%4d", gc.get(Calendar.YEAR)))
				.append("; ")
				.append(String.format("%2d", gc.get(Calendar.MONTH) + 1))
				.append("; ")
				.append(String.format("%2d", gc.get(Calendar.DATE)))
				.append("; ")
				.append(String.format("%2d", gc.get(Calendar.HOUR_OF_DAY)))
				.append("; ")
				.append(String.format("%2d", gc.get(Calendar.MINUTE)))
				.append("; ")
				.append(String.format("%2d", gc.get(Calendar.SECOND)))
				.append(")");

		return sb.toString(); 
	}
	
	private Pair<Session, Database> getConnection() {
		
		Session s = null;
		try {
			s = NotesFactory.createSession(server, username, password);
		} catch (NotesException e) {
			String msg = "Exception caught while creating Lotus session.";
			logger.log(Level.SEVERE, msg, e);
			throw new RuntimeException(msg, e);
		}
		logger.info("Lotus session created. Instance: " + s);
		
		Database db = null;
		try {
			db = s.getDatabase(null, calendarDatabase);
		} catch (NotesException e) {
			String msg = "Exception caught while opening Lotus database.";
			logger.log(Level.SEVERE, msg, e);
			throw new RuntimeException(msg, e);
		}
		logger.info("Calendar database opened. Instance: " + db);

		return new ImmutablePair<Session, Database>(s, db);
	}
	
	private void releaseConnection(Pair<Session, Database> connection) {
		
		try {
			// Recycle Lotus database object.
			//
			connection.getRight().recycle();
			
			// Recycle Lotus session object.
			//
			connection.getLeft().recycle();
			
		} catch (NotesException e) {
			String msg = "Exception caught while recycling Lotus object.";
			logger.log(Level.SEVERE, msg, e);
			throw new RuntimeException(msg, e);
		}
	}
}
