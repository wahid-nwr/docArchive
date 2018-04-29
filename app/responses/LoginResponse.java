package responses;

import java.util.ArrayList;
import java.util.List;

//import models.GeoPSU;
import models.UserModel;

public class LoginResponse {
    public String role;
    //public List<String> PSU;

    public LoginResponse(UserModel user) {
        this.role = user.role.getRoleName();
        /*
	this.PSU = new ArrayList<String>();
        for (GeoPSU psu : user.geoPSUs){
        	this.PSU.add(psu.geoPSUId);
        }
	*/
    }
}
