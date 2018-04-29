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
package jobs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import models.Aco;
import models.Role;
import play.Play;
import play.db.jpa.GenericModel;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Controller;
import play.test.Fixtures;



import controllers.deadbolt.ExternalRestrictions;

/**
 * Bootstrap Job - Process jobs when application starts
 */
@OnApplicationStart
public class Bootstrap extends Job {
	/* (non-Javadoc)
	 * @see play.jobs.Job#doJob()
	 */
	@Override
	public void doJob() {
		// loadTestData();
		loadInitialData();
		makeAccessControlObjects();
		createUploadesDir();
		//dummyInterview();
	}

	private void loadInitialData() {
		/*if(GenericModel.count() == 0) {
			Fixtures.loadModels("initial-data.yml");
		}*/
	}
	
	private void createUploadesDir(){
		try {
			String root = Play.applicationPath.getCanonicalPath();
			File upDir = new java.io.File(root + "/uploads");
			if(!upDir.exists()){
				upDir.mkdir();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void makeAccessControlObjects() {
		List<Class> controllers = Play.classloader.getAssignableClasses(Controller.class);
		for(Class controller : controllers) {
			Method[] methods = controller.getMethods();
			for(Method method : methods) {
				if (Modifier.isStatic(method.getModifiers())
						&& !method.getDeclaringClass().equals(Controller.class)
						&& method.isAnnotationPresent(ExternalRestrictions.class)) {

					ExternalRestrictions annotation = method.getAnnotation(ExternalRestrictions.class);
					for(String name : annotation.value()) {
						Aco aco = Aco.findByName(name);
						if(aco == null) {
							aco = new Aco(name, controller.getSimpleName());
							Role admin = Role.findById(1L);
							aco.roles.add(admin);
							aco.save();
						}
					}
				}
			}
		}
	}

	/*@SuppressWarnings("unused")
	private void loadTestData() {
		if (Form.count() == 0) {

            Fixtures.loadModels("test-data.yml");

            Event e1 = Event.findById(1L);
            // Event e2 = Event.findById(2L);
            // Event e3 = Event.findById(3L);
            Event e4 = Event.findById(4L);

            DateTime dateTime = new DateTime();
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

            Woman w1 = Woman.findById(1L);
            dateTime = formatter.parseDateTime("2011-03-28");
            w1.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-12-25");
            w1.events.put(e1, dateTime.toDate());
            w1.save();

            Woman w2 = Woman.findById(2L);
            dateTime = formatter.parseDateTime("2011-06-07");
            w2.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-01-21");
            w2.events.put(e1, dateTime.toDate());
            w2.save();

            Woman w3 = Woman.findById(3L);
            dateTime = formatter.parseDateTime("2012-02-04");
            w3.events.put(e1, dateTime.toDate());
            w3.save();

            Woman w4 = Woman.findById(4L);
            dateTime = formatter.parseDateTime("2011-02-16");
            w4.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-11-28");
            w4.events.put(e1, dateTime.toDate());
            w4.save();

            Woman w5 = Woman.findById(5L);
            dateTime = formatter.parseDateTime("2011-05-08");
            w5.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2012-02-06");
            w5.events.put(e1, dateTime.toDate());
            w5.save();

            Woman w6 = Woman.findById(6L);
            dateTime = formatter.parseDateTime("2011-02-17");
            w6.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-12-20");
            w6.events.put(e1, dateTime.toDate());
            w6.save();

            Woman w7 = Woman.findById(7L);
            dateTime = formatter.parseDateTime("2011-04-26");
            w7.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2012-01-14");
            w7.events.put(e1, dateTime.toDate());
            w7.save();

            Woman w8 = Woman.findById(8L);
            dateTime = formatter.parseDateTime("2011-05-03");
            w8.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2012-01-13");
            w8.events.put(e1, dateTime.toDate());
            w8.save();

            Woman w9 = Woman.findById(8L);
            dateTime = formatter.parseDateTime("2012-01-05");
            w9.events.put(e1, dateTime.toDate());
            w9.save();

            Woman w10 = Woman.findById(10L);
            dateTime = formatter.parseDateTime("2011-04-12");
            w10.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-12-12");
            w10.events.put(e1, dateTime.toDate());
            w10.save();

            Woman w11 = Woman.findById(11L);
            dateTime = formatter.parseDateTime("2011-03-22");
            w11.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-10-29");
            w11.events.put(e4, dateTime.toDate());
            w11.save();

            Woman w12 = Woman.findById(12L);
            dateTime = formatter.parseDateTime("2011-02-23");
            w12.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2012-02-01");
            w12.events.put(e1, dateTime.toDate());
            w12.save();

            Woman w13 = Woman.findById(13L);
            dateTime = formatter.parseDateTime("2011-05-05");
            w13.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-04-11");
            w13.events.put(e1, dateTime.toDate());
            w13.save();

            Woman w14 = Woman.findById(14L);
            dateTime = formatter.parseDateTime("2011-02-09");
            w14.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-12-16");
            w14.events.put(e1, dateTime.toDate());
            w14.save();

            Woman w15 = Woman.findById(15L);
            dateTime = formatter.parseDateTime("2011-03-01");
            w15.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-11-16");
            w15.events.put(e1, dateTime.toDate());
            w15.save();

            Woman w16 = Woman.findById(16L);
            dateTime = formatter.parseDateTime("2011-03-31");
            w16.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2011-12-21");
            w16.events.put(e1, dateTime.toDate());
            w16.save();

            Woman w17 = Woman.findById(17L);
            dateTime = formatter.parseDateTime("2011-05-16");
            w17.events.put(e4, dateTime.toDate());
            dateTime = formatter.parseDateTime("2012-02-29");
            w17.events.put(e1, dateTime.toDate());
            w17.save();
        }
	}*/

}