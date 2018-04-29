package controllers;

import play.Play;
import javax.persistence.Query;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Http.Header;
import play.server.ServletWrapper;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.ExternalRestrictions;
import controllers.deadbolt.JSON;

import play.Logger;
import play.data.FileUpload;
import play.data.validation.Validation;
import play.mvc.With;
import controllers.deadbolt.Unrestricted;

import play.db.DB;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;
import javax.validation.Valid;

@With(Deadbolt.class)
public class CustomerManagementController extends Controller{

	@ExternalRestrictions("Customer Management")
	public static void list(){
	List<models.Customer> customerList = models.Customer.findAll();
		render(customerList);
	}

	@ExternalRestrictions("Customer Management")
	public static void create(){
		render();
	}

	@ExternalRestrictions("Customer Management")
	public static void edit(Long id){
		models.Customer customer = models.Customer.findById(id);
		render("@create",customer);
	}
	
	@ExternalRestrictions("Customer Management")
    	public static void submit(@Valid models.Customer customer){
		validation.valid(customer);
		if(Validation.hasErrors()) {
			flash.error("Customer "+customer.customerName+" could not be saved!Error="+Validation.errors().toString());
			Logger.info("Error!!  "+ Validation.errors().toString());
			render("@create", customer);
		}
		customer.save();
		List<models.Customer> customerList = models.Customer.findAll();
		render("@list",customerList);
	}
	@ExternalRestrictions("Customer Management")
	public static void delete(Long id){
	}
	
	@ExternalRestrictions("Customer Management")
	public static void customergrouplist(){
	List<models.CustomerGroup> customerGroupList = models.CustomerGroup.findAll();
		render(customerGroupList);
	}

	@ExternalRestrictions("Customer Management")
	public static void createcustomergroup(){
		render();
	}

	@ExternalRestrictions("Customer Management")
	public static void editcustomergroup(Long id){
		models.CustomerGroup customerGroup = models.CustomerGroup.findById(id);
		render("@createcustomergroup",customerGroup);
	}
	
	@ExternalRestrictions("Customer Management")
    	public static void submitcustomergroup(@Valid models.CustomerGroup customerGroup){
		validation.valid(customerGroup);
		if(Validation.hasErrors()) {
			flash.error("Group "+customerGroup.groupName+" could not be saved!! Error="+Validation.errors().toString());
			Logger.info("Error!!  "+ Validation.errors().toString());
			render("@createcustomergroup", customerGroup);
		}
		customerGroup.save();
		List<models.CustomerGroup> customerGroupList = models.CustomerGroup.findAll();
		render("@customergrouplist",customerGroupList);
	}
	
	
	@ExternalRestrictions("Customer Management")
	public static void idmaplist(){
	List<models.AccountIdMap> accountIdMapList = models.AccountIdMap.findAll();
		render(accountIdMapList);
	}
	
	@ExternalRestrictions("Customer Management")
    	public static void submitAccountIdMap(@Valid models.AccountIdMap accountIdMap){
		System.out.println("accountIdMap:::"+accountIdMap.id);
		String id = params.get("accountIdMap.customerGroupId");
		String type = params.get("accountIdMap.clienttype");
		models.AccountIdMap accountIdMapPrev = null;
		if(type!=null && !type.equals("null") && type.length()>0)
		{
			if(type.equals("customer"))
			{
				models.Customer customer = models.Customer.findById(Long.parseLong(id));
				System.out.println("customer::"+customer);
				accountIdMap.customer = customer;
				accountIdMapPrev = models.AccountIdMap.find("customer=?",customer).first();
			}
			else if(type.equals("group"))
			{
				models.CustomerGroup customerGroup = models.CustomerGroup.findById(Long.parseLong(id));
				System.out.println("customerGroup::"+customerGroup);
				accountIdMap.customerGroup = customerGroup;
				accountIdMapPrev = models.AccountIdMap.find("customerGroup=?",customerGroup).first();
			}
			
		}
		validation.valid(accountIdMap);
		boolean idExists = false;
		if(accountIdMapPrev!=null)
		{
			idExists = true;
		}
		
		if(Validation.hasErrors() || idExists) {
			if(!idExists)
			{
				flash.error("Account "+accountIdMap.accountNo+" could not be saved!! Error="+Validation.errors().toString());
			}
			else
			{
				flash.error("Account "+accountIdMap.accountNo+" could not be saved!! Error=Already mapped!!");
			}
			Logger.info("Error!!  "+ Validation.errors().toString());
			render("@createidmap", accountIdMap);
		}
		accountIdMap.save();
		List<models.AccountIdMap> accountIdMapList = models.AccountIdMap.findAll();
		render("@idmaplist",accountIdMapList);
	}
	
	@ExternalRestrictions("Customer Management")
	public static void editidmap(Long id){
		models.AccountIdMap accountIdMap = models.AccountIdMap.findById(id);
		render("@createidmap",accountIdMap);
	}
	
	@ExternalRestrictions("Customer Management")
	public static void createidmap(){
		render();
	}
	
	@ExternalRestrictions("Customer Management")
	public static void getId(){
		String json = "[ {\"name\": \"Afghanistan\", \"code\": \"AF\"},{\"name\": \"Aland Islands\", \"code\": \"AX\"}, "+
		" {\"name\": \"Albania\", \"code\": \"AL\"}, {\"name\": \"Algeria\", \"code\": \"DZ\"}, {\"name\": \"American Samoa\", \"code\": \"AS\"}]";
		json = "{\"status\":true,\"error\":null,\"data\":{\"user\":[{\"id\":4152589,\"username\":\"TheTechnoMan\",\"avatar\":\"https:\\/\\/avatars2.githubusercontent.com\\/u\\/4152589\"},"+
		
		"{\"id\":748137,\"username\":\"juliocastrop\",\"avatar\":\"https:\\/\\/avatars3.githubusercontent.com\\/u\\/748137\"},"+
		"{\"id\":619726,\"username\":\"cfreear\",\"avatar\":\"https:\\/\\/avatars0.githubusercontent.com\\/u\\/619726\"},"+
		"{\"id\":906237,\"username\":\"nilovna\",\"avatar\":\"https:\\/\\/avatars2.githubusercontent.com\\/u\\/906237\"},"+
		"{\"id\":2,\"project\":\"jQuery Validation\",\"image\":\"http:\\/\\/www.runningcoder.org\\/assets\\/jqueryvalidation\\/img\\/jqueryvalidation-preview.jpg\",\"version\":\"1.4.0\",\"demo\":11,\"option\":14,\"callback\":8}]}}";
		
		/*
		json = "[{\"id\":\"Cuculus canorus\",\"label\":\"Common Cuckoo\",\"value\":\"Common Cuckoo\"},"+
		"{\"id\":\"Clamator glandarius\",\"label\":\"Great Spotted Cuckoo\",\"value\":\"Great Spotted Cuckoo\"}"+
		",{\"id\":\"Coccyzus americanus\",\"label\":\"Yellow-Billed Cuckoo\",\"value\":\"Yellow-Billed Cuckoo\"}]";
		*/
		
		List<models.CustomerGroup> customerGroupList = models.CustomerGroup.findAll();
		List<models.Customer> customerList = models.Customer.findAll();
		String clienttype = request.params.get("type");
		String qry = request.params.get("q");
		System.out.println("clienttype::"+clienttype);
		String customers = "";
		json = "{\"status\":true,\"error\":null,\"data\":{\"user\":[";
		if(clienttype!=null && !clienttype.equals("null") && clienttype.length()>0)
		{
			if(clienttype.equals("group"))
			{
				customerGroupList = models.CustomerGroup.find("groupName like '%"+qry+"%'").fetch();
				for(models.CustomerGroup customerGroup:customerGroupList)
				{
					if(customers.length()>0)
					{
						customers += ",{\"id\":\""+customerGroup.id+"\",\"name\":\""+customerGroup.groupName+"\"}";
					}
					else
					{
						customers += "{\"id\":\""+customerGroup.id+"\",\"name\":\""+customerGroup.groupName+"\"}";
					}
				}
				System.out.println("group list::"+customerGroupList.size());
			}
			else if(clienttype.equals("customer"))
			{
				customerList = models.Customer.find("customerName like '%"+qry+"%'").fetch();
				System.out.println("customer list::"+customerList.size());
				for(models.Customer customerGroup:customerList)
				{
					if(customers.length()>0)
					{
						customers += ",{\"id\":\""+customerGroup.id+"\",\"name\":\""+customerGroup.customerName+"\"}";
					}
					else
					{
						customers += "{\"id\":\""+customerGroup.id+"\",\"name\":\""+customerGroup.customerName+"\"}";
					}
				}
			}
		}
		json = json+customers+"]}}";
		
		renderText(json);
	}
	
	@ExternalRestrictions("Customer Management")
	public static void getMappedId(){
		String json = "[ {\"name\": \"Afghanistan\", \"code\": \"AF\"},{\"name\": \"Aland Islands\", \"code\": \"AX\"}, "+
		" {\"name\": \"Albania\", \"code\": \"AL\"}, {\"name\": \"Algeria\", \"code\": \"DZ\"}, {\"name\": \"American Samoa\", \"code\": \"AS\"}]";
		json = "{\"status\":true,\"error\":null,\"data\":{\"user\":[{\"id\":4152589,\"username\":\"TheTechnoMan\",\"avatar\":\"https:\\/\\/avatars2.githubusercontent.com\\/u\\/4152589\"},"+
		
		"{\"id\":748137,\"username\":\"juliocastrop\",\"avatar\":\"https:\\/\\/avatars3.githubusercontent.com\\/u\\/748137\"},"+
		"{\"id\":619726,\"username\":\"cfreear\",\"avatar\":\"https:\\/\\/avatars0.githubusercontent.com\\/u\\/619726\"},"+
		"{\"id\":906237,\"username\":\"nilovna\",\"avatar\":\"https:\\/\\/avatars2.githubusercontent.com\\/u\\/906237\"},"+
		"{\"id\":2,\"project\":\"jQuery Validation\",\"image\":\"http:\\/\\/www.runningcoder.org\\/assets\\/jqueryvalidation\\/img\\/jqueryvalidation-preview.jpg\",\"version\":\"1.4.0\",\"demo\":11,\"option\":14,\"callback\":8}]}}";
		
		/*
		json = "[{\"id\":\"Cuculus canorus\",\"label\":\"Common Cuckoo\",\"value\":\"Common Cuckoo\"},"+
		"{\"id\":\"Clamator glandarius\",\"label\":\"Great Spotted Cuckoo\",\"value\":\"Great Spotted Cuckoo\"}"+
		",{\"id\":\"Coccyzus americanus\",\"label\":\"Yellow-Billed Cuckoo\",\"value\":\"Yellow-Billed Cuckoo\"}]";
		*/
		List<models.AccountIdMap> accountIdMapCustList = models.AccountIdMap.findAll();
		List<models.AccountIdMap> accountIdMapGroupList = models.AccountIdMap.findAll();
		
		
		String qry = request.params.get("q");
		String customers = "";
		json = "{\"status\":true,\"error\":null,\"data\":{\"user\":[";
		
		accountIdMapCustList = models.AccountIdMap.find("(accountNo like '%"+qry+"%') or (customer!=null and customer.customerName like '%"+qry+"%')").fetch();
		accountIdMapGroupList = models.AccountIdMap.find("(accountNo like '%"+qry+"%') or (customerGroup!= null and customerGroup.groupName like '%"+qry+"%')").fetch();
		
		List<models.AccountIdMap> accountIdMapList = new ArrayList<models.AccountIdMap>();
		accountIdMapList.addAll(accountIdMapCustList);
		accountIdMapList.addAll(accountIdMapGroupList);
		System.out.println("accountIdMapList::"+accountIdMapList.size());
		for(models.AccountIdMap accountIdMap:accountIdMapList)
		{
			if(customers.length()>0)
			{
				customers += ",";
			}		
			if(accountIdMap.customer!=null)	
			{
				customers += "{\"id\":\""+accountIdMap.id+"\",\"accountNo\":\""+accountIdMap.accountNo+"\",\"name\":\""+accountIdMap.customer.customerName+"\",\"customerId\":\""+accountIdMap.customer.customerId+"\"}";
			}
			else if(accountIdMap.customerGroup!=null)
			{
				customers += "{\"id\":\""+accountIdMap.id+"\",\"accountNo\":\""+accountIdMap.accountNo+"\",\"name\":\""+accountIdMap.customerGroup.groupName+"\",\"groupId\":\""+accountIdMap.customerGroup.groupId+"\"}";
			}
		}			
		
		json = json+customers+"]}}";
		
		renderText(json);
	}
}
