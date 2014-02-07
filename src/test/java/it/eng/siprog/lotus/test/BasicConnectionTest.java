package it.eng.siprog.lotus.test;

import java.util.Properties;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DbDirectory;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import lotus.domino.corba.IObjectServer;
import lotus.domino.corba.IObjectServerHelper;
import lotus.domino.cso.ORBCallback;

import org.junit.Test;

public class BasicConnectionTest {

	@Test
	public void decodeIOR() throws NotesException {
	
		String ior = NotesFactory.getIOR("lab100.lab:63148");
		
		lotus.priv.CORBA.iiop.ORB orb = null;
		Properties paramProperties = new Properties();
		paramProperties.put("org.omg.CORBA.ORBClass",
				"lotus.priv.CORBA.iiop.ORB");

		orb = new lotus.priv.CORBA.iiop.ORB((String[])null, paramProperties);
		orb.setCallback(new ORBCallback());
		
		org.omg.CORBA.Object localObject = orb.string_to_object(ior);
		IObjectServer localIObjectServer = IObjectServerHelper.narrow(localObject);
		
		System.out.println(localIObjectServer);
	}
	
	@Test
	public void connectUsingIOR() throws NotesException {
		
		Session s;
		String ior = NotesFactory.getIOR("lab100.lab:63148");
		System.out.println(ior);
		s = NotesFactory.createSessionWithIOR(
				ior, 
				"Utest Utente10", 
				"utente10");
		String p = s.getPlatform();
		System.out.println("Platform = " + p);
	}

	@Test
	public void connectDirectly() throws NotesException {
		
		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10", 
				"utente10");
		System.out.println("Platform = " + s.getPlatform());
		System.out.println("UserName = " + s.getUserName());
	}

	@Test
	public void anonymousConnect() throws NotesException {
		
		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148");
		String p = s.getPlatform();

		System.out.println("Platform = " + p);
		System.out.println("Username = " + s.getUserName());
		System.out.println("Notes version = " + s.getNotesVersion());
	}

	
	@Test
	public void listDatabases() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		DbDirectory dir = s.getDbDirectory(null);

		String server = dir.getName();
		if (server.equals("")) {
			server = "Local";
		}

		System.out.println("Database directory list on server " + server + "\n");

		Database db = dir.getFirstDatabase(DbDirectory.DATABASE);
		while (db != null) {
			String fn = db.getFileName();
			String title = db.getTitle();
			String fp = db.getFilePath();
			System.out.println(fn + " - " + fp + " - " + title);
			db = dir.getNextDatabase();
		}
	}

	
	@Test
	public void listSIRDDocuments() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		Database db = s.getDatabase(null, "condivisi/agendadin.nsf");
		String fn = db.getFileName();
		String title = db.getTitle();
		System.out.println(fn + " - " + title);
		
		DocumentCollection dc = db.getAllDocuments();
		System.out.println("The getAllDocuments() returned " + dc.getCount() + " documents.");
		Document doc = dc.getFirstDocument();
		while(doc != null) {
			String progressivo = doc.getItemValueString("fldProgressivo");
			if(progressivo != null) {
				System.out.println(progressivo);
			}
			doc = dc.getNextDocument();
		}
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void listSIRDViews() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		Database db = s.getDatabase(null, "condivisi/agendadin.nsf");
		
		Vector<View> views = db.getViews();
		for(View view : views) {
			System.out.println("View: " + view.getName());
		}
	}

	
	@Test
	public void exploreSIRDVerbaleView() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		Database db = s.getDatabase(null, "condivisi/agendadin.nsf");
		
		View view = db.getView("Verbale");
		System.out.println("View: " + view.getName());
		System.out.println("Entries in view: " + view.getEntryCount());
		ViewEntryCollection vec = view.getAllEntries();
		ViewEntry ve = vec.getFirstEntry();
		while(ve != null) {
			System.out.println(ve.getDocument().generateXML());
			ve = vec.getNextEntry();
		}
	}

	
	@Test
	public void searchSIRDDocuments() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		Database db = s.getDatabase(null, "condivisi/agendadin.nsf");
		
		DocumentCollection dc = db.search("@IsAvailable(fldProgressivo)");
		System.out.println("The search() returned " + dc.getCount() + " documents.");
		Document doc = dc.getFirstDocument();
		while(doc != null) {
			String progressivo = doc.getItemValueString("fldProgressivo");
			if(progressivo != null) {
				System.out.println(progressivo);
			}
			doc = dc.getNextDocument();
		}
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void exploreSIRDSampleDocument() throws NotesException {

		Session s;
		s = NotesFactory.createSession(
				"lab100.lab:63148", 
				"Utest Utente10",
				"utente10");

		Database db = s.getDatabase(null, "condivisi/agendadin.nsf");
		
		DocumentCollection dc = db.search("@IsAvailable(fldProgressivo) & fldProgressivo = \"2013_3508\"");
		System.out.println("The search() returned " + dc.getCount() + " documents.");
		Document doc = dc.getFirstDocument();
		
		System.out.println("XML dump:");
		System.out.println(doc.generateXML());
		System.out.println();
		
		Vector<Item> items = doc.getItems();
		for(Item item : items) {
			System.out.println(item.getName());
		}
	}
}
