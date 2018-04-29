/*
 * Copyright (C) 2011 mPower Health
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
package controllers;

import models.UserModel;
import play.mvc.Scope.Session;

/**
 * Security - Security handler.
 *
 * @author Rifat Nabi <rnabi@mpower-health.com>
 * Created Dec 19, 2011
 *
 */

public class Security extends Secure.Security {

	/**
	 * Authenticate.
	 *
	 * @param username the username
	 * @param password the password
	 * @return true, if successful
	 */
	static boolean authenticate(String username, String password) {
		UserModel authenticatedUser = UserModel.authenticate(username, password);
		if(authenticatedUser!=null)
		{
			Session.current().put("webUser", username);
		}
		return  authenticatedUser!= null;
		
    }

}
