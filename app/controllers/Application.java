package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import org.apache.commons.io.FilenameUtils;

import play.Logger;
import play.Play;
import play.mvc.Util;
import play.mvc.With;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.ExternalRestrictions;

import models.UserModel;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import play.db.jpa.JPA;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

@With(Deadbolt.class)
public class Application extends Controller {

	@ExternalRestrictions("Dashboard")
	public static void index(String id) {
	   // List<Interview> interviews = Interview.getCurrentJWeekList();
		// List<Schedule> scheduleList = Schedule.findAll();
		 //Schedule.findAll();
		//Data.find("byBeneficiary", b);
		//Data.findById(100L);
		//Data.count();
		//Logger.info("Data Size: " + Data.count());
		//Beneficiary.findById(100L);
		//Schedule.findById(100L);
		
		//Form f = Form.findById(1L);
		//Data.find("byForm", f).fetch();
		
		/*Household h = Household.findById(10L);
		Beneficiary.find("byHousehold", h).fetch();*/
		
		/*User u = User.findById(1L);
		Beneficiary.find("byUser", u).fetch();*/
		
		/*Form f = Form.findById(1L);
		List<Data> data = Data.find("byForm", f).fetch();
		ok();*/
		
		//Form f = Form.findById(1L);
		//List<Data> data = Data.find("byForm", f).fetch(30000);
		
		//render(scheduleList);
		
		/*Data data = Data.findById(8L);
		FlatDataGenerator.generateFlat(data);*/

		UserModel currentUser = UserModel.findByName(controllers.Secure.Security.connected());
		/*
		if(currentUser.role.name.equals("Consultant"))
		{
			firmFacilitys = FirmFacility.find("Select f from FirmFacility as f join f.consultants as e where e = ?",currentUser).fetch();
		}
		else if(currentUser.role.name.equals("Veterinary Clinic"))
		{
			firmFacilitys = FirmFacility.find("Select f from FirmFacility as f join f.vetClinics as e where e = ?",currentUser).fetch();
		}
		*/
		UserModel sessUser = UserModel.findByName(session.get("webUser"));
		
    	render(currentUser);
		
    }	
	

	public static void getNotificationTest(String id) {

		Logger.info("I am  here" + id);

		renderJSON("abc");

	}
	
	public static void about(){
		render();
	}
	
	@ExternalRestrictions("Uplode CSV data")
	public static void uplodeCsv(){
		render();
	}
	
	public static void redirect(String url) {
        redirect(url, true);
    }
}
