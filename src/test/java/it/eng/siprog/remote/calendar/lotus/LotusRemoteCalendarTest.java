package it.eng.siprog.remote.calendar.lotus;

import it.eng.siprog.WeldJUnit4Runner;
import it.eng.siprog.remote.calendar.RemoteCalendar;
import it.eng.siprog.remote.calendar.RemoteCalendarTest;

import javax.inject.Inject;

import org.junit.runner.RunWith;

@RunWith(WeldJUnit4Runner.class)
public class LotusRemoteCalendarTest extends RemoteCalendarTest {

	@Inject @Lotus
	private RemoteCalendar remoteCalendar;
	
	@Override
	public RemoteCalendar getRemoteCalendar() {
		return remoteCalendar;
	}
}
