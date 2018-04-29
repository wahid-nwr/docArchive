package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import models.Aco;
//import models.GeoPSU;
import models.Role;
import models.UserModel;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.DB;
import play.mvc.With;
import responses.LoginResponse;
import annotations.Mobile;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.ExternalRestrictions;
import controllers.deadbolt.Unrestricted;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import play.db.jpa.JPA;

@With(Deadbolt.class)
public class Users extends Controller {

	@ExternalRestrictions("View User")
    public static void userlist() {
		List<UserModel> users = UserModel.find("id <> 1").fetch();
		render(users);
    }
    @ExternalRestrictions("View User")
    public static void usercreate() {
		List<Role> roles = Role.find("id<>1").fetch();
	
		render(roles);
    }
    @ExternalRestrictions("View User")
    public static void definerole() {
		List<Role> roles = Role.findAll();
    	List<Aco> acos = Aco.find("name <> 'ACL'").fetch();
    	render(roles, acos);
    }
    @ExternalRestrictions("View User")
    public static void fieldofficerlist() {
		render();
    }
	@ExternalRestrictions("View User")
    public static void list() {
		EntityManager em = JPA.em();
		UserModel sessUser = UserModel.findByName(session.get("webUser"));
	
		
		String sqlQuery = "";
		if(sessUser!=null && (sessUser.role.name).equals("Admin"))
		{
			sqlQuery = "Select * from UserModel where name<> 'Admin'";
		}
		else if((sessUser.role.name).equals("FirmManager"))
		{			
			sqlQuery = "Select * from UserModel where name<> 'Admin'";
		}
		
		System.out.println("query::"+sqlQuery);
		List<UserModel> users = em.createNativeQuery(sqlQuery, UserModel.class).getResultList();
		//List<User> users = User.find("name <> 'admin' and (firmFacility.id = "+firmFacility.id+")").fetch();
        render(users);
    }
    
    
    

	@ExternalRestrictions("View User")
    public static void details(Long id) {
		UserModel user = UserModel.findById(id);
		UserModel sessUser = UserModel.findByName(session.get("webUser"));
		notFoundIfNull(user, "user not found");
		
		String fieldName = "geoUpazilla.geoDistrict";
		render(user, /*geoPSUs,*/ sessUser,fieldName);
    }
    
    @ExternalRestrictions("Edit User")
    public static void create() {
    	List<Role> roles = null;
    	List<UserModel> users = UserModel.find("role = ?", Role.findByName("Mobilizer")).fetch();
    	
    	UserModel sessUser = UserModel.findByName(session.get("webUser"));
	
		
		if(sessUser!=null && (sessUser.role.name).equals("Admin"))
		{			
			roles = Role.findAll();
		}
		else if((sessUser.role.name).equals("FirmManager"))
		{
			roles = Role.find("name = ?","Farm Attendant").fetch();
		}
    	render("@edit", users, roles,sessUser);
    }
    

	@ExternalRestrictions("Edit User")
    public static void edit(Long id) {
		System.out.println("id:::"+id);
    	UserModel usermodel = UserModel.findById(id);
    	notFoundIfNull(usermodel, "user not found");
    	usermodel.password = null;
    	List<Role> roles = Role.find("id<>1").fetch();
    	
    	render("@usercreate",usermodel, roles);
    }

    @ExternalRestrictions("Edit User")
    public static void submit(@Valid UserModel usermodel) {
    	validation.valid(usermodel);
    	System.out.println("userL:::"+usermodel);
    	/*
    	if(Validation.hasErrors()) {
			System.out.println("has errors");
			for(play.data.validation.Error error : validation.errors()) {
				 System.out.println(error.message());
			 }
    		List<Role> roles = Role.find("id<>1").fetch();
        	render("@usercreate", usermodel, roles);
        }
		*/
        usermodel.save();
        flash.success("UserModel created successfully.");
        //list();
        userlist();
    }
    
    
    @ExternalRestrictions("Edit User")
    public static void loadMobilizerList(UserModel user) {
       	List<Role> roles = Role.findAll();

       	render("@edit",user, roles);
    }

    @ExternalRestrictions("Edit User")
    public static void delete(Long id) {
    	if(request.isAjax()) {
	    	notFoundIfNull(id, "id not provided");
	    	UserModel user = UserModel.findById(id);
	    	notFoundIfNull(user, "user not found");
	    	user.delete();
	    	ok();
    	}
    }

    /* Roles */
	@ExternalRestrictions("Edit User")
    public static void roleList() {
		List<Role> roles = Role.find("id <> 1").fetch();
        render(roles);
    }

    @ExternalRestrictions("Edit User")
    public static void roleCreate() {
    	render("@roleEdit");
    }

	@ExternalRestrictions("Edit User")
    public static void roleEdit(Long id) {
    	Role role = Role.findById(id);
    	notFoundIfNull(role, "user not found");
    	render(role);
    }

    @ExternalRestrictions("Edit User")
    public static void roleSubmit(@Valid Role role) {
        if(Validation.hasErrors()) {
        	render("@roleEdit", role);
        }
        role.save();
        flash.success("Record created successfully.");
        roleList();
    }

    @ExternalRestrictions("Edit User")
    public static void roleDelete(Long id) {
    	if(request.isAjax()) {
	    	notFoundIfNull(id, "id not provided");
	    	Role role = Role.findById(id);
	    	notFoundIfNull(role, "role not found");
	    	role.delete();
	    	ok();
    	}
    }

    
	/* Access Control List */
    @ExternalRestrictions("ACL")
    public static void acl() {
    	List<Role> roles = Role.findAll();
    	List<Aco> acos = Aco.find("name <> 'ACL'").fetch();
    	render(roles, acos);
    }

    @ExternalRestrictions("ACL")
    public static void updatePermission(long acoId, long roleId, boolean state) {
    	notFoundIfNull(acoId);
    	notFoundIfNull(roleId);
    	notFoundIfNull(state);
    	Aco aco = Aco.findById(acoId);
    	Role role = Role.findById(roleId);
		if(role.id == 1) {
			ok();
		}
    	notFoundIfNull(aco);
    	notFoundIfNull(role);
    	if(state) {
    		aco.roles.add(role);
    	} else {
    		aco.roles.remove(role);
    	}
    	aco.save();
    	ok();
    }

    /*
     * All mobile API end points are prefixed with the letter 'm'
     */
    @Unrestricted
    @Mobile
    public static void mLogin() {
		if(!session.contains("apiUser")) {
			error(424, "Session expired");
		}
		UserModel user = UserModel.find("byName", session.get("apiUser")).first();
		renderJSON(new LoginResponse(user));
    }
    
}
