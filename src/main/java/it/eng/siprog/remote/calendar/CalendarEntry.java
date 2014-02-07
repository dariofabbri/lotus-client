package it.eng.siprog.remote.calendar;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class CalendarEntry { 

	private String subject;
	private Date calendarDate;
	private Date startDate;
	private Date endDate;
	private String location;
	private String body;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getCalendarDate() {
		return calendarDate;
	}

	public void setCalendarDate(Date calendarDate) {
		this.calendarDate = calendarDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("subject", subject)
			.append("calendarDate", calendarDate)
			.append("startDate", startDate)
			.append("endDate", endDate)
			.append("location", location)
			.append("body", body)
			.toString();
	}	
}
