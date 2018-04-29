/*
 * Copyright (C) 2012 mPower Social
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package extensions;

import org.codehaus.groovy.runtime.NullObject;

import play.templates.JavaExtensions;

import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.Months;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import play.templates.BaseTemplate.RawData;

public class PowerExtension extends JavaExtensions {
	
	
	/**
	 * If the passed template Object is equal null 
	 * then render the passed val 
	 * or render the original value
	 *
	 * @param var the variable
	 * @param val the value
	 * @return the string
	 */
	// TODO - Fix this broken function
	public static String defaultVal(Object var, String val) {
		
		if(var instanceof NullObject) {
			return val;
		} else {
			return var.toString();
		}
	}
	
	public static String removeNewLine(RawData inS){
		String temp;

		temp = inS.toString().replace("\n", "");
		return temp;
	}

	public static String removeNewLine(String inS){
		String temp;

		temp = inS.replace("\n", "");
		return temp;
	}

	public static String diff(Date base, Date comp,boolean dateDiff,boolean timeDiff) {
		DateTime end = new DateTime(base);
		DateTime start = new DateTime(comp);
		
		
		int years = Years.yearsBetween(end,start).getYears();
		int months = Months.monthsBetween(end,start).getMonths()%12;
		int monthsToDeduct = 0;
		if(years>0)
		{
			monthsToDeduct = 12*years;
		}
		if(months>0)
		{
			monthsToDeduct = monthsToDeduct+months;
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -monthsToDeduct);
		Date monthsDeduct = c.getTime();
		int days = Days.daysBetween(end,new DateTime(monthsDeduct)).getDays();
		int hours = Hours.hoursBetween(end,start).getHours()%24;
		int mins = Minutes.minutesBetween(end, start).getMinutes()%60;
		int secs = Seconds.secondsBetween(end, start).minus(mins*60).getSeconds()%60;
		String differanceValueAsString = "";
		if(dateDiff)
		{
			differanceValueAsString = (years>0 ? years +" years, ":"");			
			differanceValueAsString += (months>0 ? +months +" months, ":"");
			differanceValueAsString += (days>0 ? +days +" days, ":"");
		}
		if(timeDiff)
		{
			if(differanceValueAsString!=null && !differanceValueAsString.equals("null") && differanceValueAsString.length()>0)
			{
				differanceValueAsString += " and ";
			}
			differanceValueAsString += (hours>0 ? hours +" Hours":"")+(mins > 0 ? "  and "+mins + " Minutes and " : "") +"  and "+secs + " Seconds";
		}
		return differanceValueAsString;
	}
	
}
