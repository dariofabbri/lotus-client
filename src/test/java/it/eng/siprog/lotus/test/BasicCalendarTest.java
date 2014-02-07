package it.eng.siprog.lotus.test;

import java.util.Date;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import org.junit.Test;

public class BasicCalendarTest {
	
	@Test
	public void dumpAllCalendarThroughView() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		Database db = s.getDatabase(null, "mail/utente1.nsf");
		
		View calendarView = db.getView("$Calendar");
		ViewEntryCollection vec = calendarView.getAllEntries();
		System.out.println("View entries: " + vec.getCount());
		
		ViewEntry ve = vec.getFirstEntry();
		while(ve != null) {
			
			Document doc = ve.getDocument();
			
			System.out.println();
			System.out.println(doc.generateXML());

			ve = vec.getNextEntry(); 
		}
	}
	
	@Test
	public void dumpSingleCalendarDay() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		Database db = s.getDatabase(null, "mail/utente1.nsf");
		
		StringBuilder searchTerm = new StringBuilder();
		searchTerm
			.append("@IsAvailable(CalendarDateTime) ")
			.append("& ")
			.append("CalendarDateTime >= @Date(2014; 02; 06; 0; 0; 0) ")
			.append("& ")
			.append("CalendarDateTime < @Date(2014; 02; 07; 0; 0; 0) ");
		
		DocumentCollection dc = db.search(searchTerm.toString());
		Document doc = dc.getFirstDocument();
		while(doc != null) {
	
			dumpCalendarDocument(doc);
			doc = dc.getNextDocument();
		}
	}
	
	@Test
	public void dumpCalendarWeek() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		Database db = s.getDatabase(null, "mail/utente1.nsf");
		
		StringBuilder searchTerm = new StringBuilder();
		searchTerm
			.append("@IsAvailable(CalendarDateTime) ")
			.append("& ")
			.append("CalendarDateTime >= @Date(2014; 02; 03; 0; 0; 0) ")
			.append("& ")
			.append("CalendarDateTime < @Date(2014; 02; 10; 0; 0; 0) ");
		
		DocumentCollection dc = db.search(searchTerm.toString());
		Document doc = dc.getFirstDocument();
		while(doc != null) {
	
			dumpCalendarDocument(doc);
			doc = dc.getNextDocument();
		}
	}
	
	@Test
	public void dumpCalendarMonth() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		Database db = s.getDatabase(null, "mail/utente1.nsf");
		
		StringBuilder searchTerm = new StringBuilder();
		searchTerm
			.append("@IsAvailable(CalendarDateTime) ")
			.append("& ")
			.append("CalendarDateTime >= @Date(2014; 02; 01; 0; 0; 0) ")
			.append("& ")
			.append("CalendarDateTime < @Date(2014; 03; 1; 0; 0; 0) ");
		
		DocumentCollection dc = db.search(searchTerm.toString());
		Document doc = dc.getFirstDocument();
		while(doc != null) {
	
			dumpCalendarDocument(doc);
			doc = dc.getNextDocument();
		}
	}
	
	
	private void dumpCalendarDocument(Document doc) throws NotesException {
		
		System.out.println("Subject: " + doc.getItemValueString("Subject"));
		System.out.println("Calendar DateTime: " + toSingleDate(doc, "CalendarDateTime"));
		System.out.println("Start Date: " + toSingleDate(doc, "StartDate"));
		System.out.println("End Date: " + toSingleDate(doc, "EndDate"));
		System.out.println("Location: " + doc.getItemValueString("Location"));
		System.out.println("Body: " + doc.getItemValueString("Body"));
		System.out.println();		
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
}
