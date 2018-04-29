package utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class ExtraUtils {

	public static Date getTodayStart(){
		 return getTodayPlus(0);
	}
	
	public static Date getTodayPlus(int plusDay){
		Calendar now =new GregorianCalendar();
        Calendar start = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        Date todayPlus = new Date(new DateTime(start.getTime()).plusDays(plusDay).getMillis());
        
        return todayPlus;
	}
	public static Date getTodayMinus(int plusDay){
		Calendar now =new GregorianCalendar();
        Calendar start = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        Date todayPlus = new Date(new DateTime(start.getTime()).minusDays(plusDay).getMillis());
        
        return todayPlus;
	}
	
	public static Date startPoint(Date date){
		Calendar now =new GregorianCalendar();
		now.setTime(date);
		Calendar start = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
	    return  new Date(new DateTime(start.getTime()).plusDays(0).getMillis());
	}

	public static Date endPoint(Date date){
		return  new Date (ExtraUtils.startPoint(
				new DateTime(date).plusDays(1).toDate()
				).getTime()-1
				);
	}
	
	/**
	 * convert a java.util.Date to a string to given format. i.e. if format = 'dd-mm-YYYY' then dateString = 08-05-2013 
	 * */
	public static String getDateString(Date date, String format) {
		String dateString = "";
		if(date != null) {
			DateTime dt = new DateTime(date);
			int month = dt.getMonthOfYear();
			int day = dt.getDayOfMonth();
			int year = dt.getYearOfEra();
			
			format = format.toLowerCase();
			format = format.replace("dd", StringUtils.leftPad(""+day, 2, "0"));
			format = format.replace("mm", StringUtils.leftPad(""+month, 2, "0"));
			format = format.replace("yyyy", ""+year);
			
			dateString = new String(format);
		}
		
		return dateString;
	}
	
		
}
