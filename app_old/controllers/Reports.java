package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Subselect;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

//import org.json.simple.JSONObject
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.ExternalRestrictions;
import play.Logger;
import play.mvc.With;

@With(Deadbolt.class)
public class Reports extends Controller {
	
	@ExternalRestrictions("Reports")
	public static void igaReport() {
		Statement stmt = null;
		ResultSet res = null;
		Connection conn = play.db.DB.getConnection();
		JsonArray JSON_ARRAY=new JsonArray();
		JsonObject JSON_OBJ= new JsonObject();
		JSON_OBJ.addProperty("name", "IGA Report");
		String sql="SELECT TOP 3  assetPurchase as 'asset',cashInHand as 'cash',clothingCost as 'foodcost',BeneficiaryMasterProfile.beneficiaryName as 'name'FROM IGAmonitoringMainForm ,BeneficiaryMasterProfile where IGAmonitoringMainForm.beneficiaryMasterProfile_id=BeneficiaryMasterProfile.id";
		JsonArray JSON_SUBARRAY=new JsonArray();
		try { 
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);
			while(res.next()) {
				JsonArray subdataArray=new JsonArray();
				JsonPrimitive x_axis = new JsonPrimitive(res.getString("name"));
				JsonPrimitive x_val = new JsonPrimitive(Integer.parseInt(res.getString("asset")));
				subdataArray.add(x_axis);	
				subdataArray.add(x_val);
				JSON_OBJ.add("data",subdataArray) ;
				//Creating Final Array
				JSON_SUBARRAY.add(subdataArray);
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
			Logger.info("Error Ocuured while Executing IGA report Query: "+sql);
		}
		Logger.info("CREATED JSON::   "+ JSON_ARRAY);
		JSON_OBJ.add("data", JSON_SUBARRAY);
		JSON_ARRAY.add(JSON_OBJ);
		render(JSON_ARRAY);
	}
	
	@ExternalRestrictions("Reports")
	public static void maritalstatusreport() {
		Statement stmt = null;
		ResultSet res = null;
		String status = "", statuscount = "";
		Connection conn = play.db.DB.getConnection();
		JsonArray JSON_ARRAY=new JsonArray();
		JsonObject JSON_OBJ= new JsonObject();
		//JSON_OBJ.addProperty("name", "IGA Report");
		String sql="SELECT count(maritalStatus) count,maritalStatus FROM BeneficiaryMasterProfile group by maritalStatus";
		JsonArray JSON_SUBARRAY=new JsonArray();
		try { 
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);
			while(res.next()) {
				JsonArray subdataArray=new JsonArray();
				status = res.getString("maritalStatus");
				statuscount = res.getString("count");
				System.out.println("status:::"+status);
				System.out.println("count:::"+statuscount);
				JsonPrimitive maritalStatus = new JsonPrimitive(status);
				JsonPrimitive count = new JsonPrimitive(Integer.parseInt(statuscount));
				subdataArray.add(maritalStatus);	
				subdataArray.add(count);
				//JSON_OBJ.add("data",subdataArray) ;
				//Creating Final Array
				JSON_SUBARRAY.add(subdataArray);
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
			Logger.info("Error Ocuured while Executing IGA report Query: "+sql);
		}
		
		//JSON_OBJ.add("data", JSON_SUBARRAY);
		//JSON_ARRAY.add(JSON_OBJ);
		//Logger.info("CREATED JSON::   "+ JSON_ARRAY);
		render(JSON_SUBARRAY);
	}
	@ExternalRestrictions("Reports")
	public static void levelofeducation() {
		render();
	}
}
