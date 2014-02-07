package it.eng.siprog.remote.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class CalendarDate {

	private Date date;
	private List<CalendarEntry> entries;

	public CalendarDate() {
		
		entries = new ArrayList<CalendarEntry>();
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public List<CalendarEntry> getEntries() {
		return entries;
	}
	
	public void setEntries(List<CalendarEntry> entries) {
		this.entries = entries;
	}
	
	public void addEntry(CalendarEntry entry) {
		this.entries.add(entry);
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("date", date)
			.append("entries", entries)
			.toString();
	}
}
