package controllers;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.validation.Valid;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.ExternalRestrictions;
import controllers.deadbolt.JSON;

import models.Role;
import play.Logger;
import play.data.FileUpload;
import play.data.validation.Validation;
import play.mvc.With;

@With(Deadbolt.class)
public class MediaArchiving extends Controller{
	
	
	/**
	 * media Listing Page
	 */
	static Random rand = new Random();
	@ExternalRestrictions("Media Archiving")
    public static void register(){
		render();
    }
	
	@ExternalRestrictions("Media Archiving")
    public static void edit(Long id){
		models.MediaFileArchiving media= models.MediaFileArchiving.findById(id);
		render("@register",media);
    }
	
	@ExternalRestrictions("Media Archiving")
    public static void submit(@Valid models.MediaFileArchiving media){
		media.dateOfUpload=Calendar.getInstance().getTime();
		if(!params.get("media.uploadedFile").isEmpty())
		   {

			   Logger.info("In media File");
			   FileUpload uploads = (FileUpload) params.get("asset.uploadedFile", FileUpload.class);
			   if(uploads!=null)
			   {
				   int  n = rand.nextInt();
				   String path="media"+File.separator;
				   String fileName =n+ uploads.getFileName();
				   uploads.asFile(path + fileName);
				   media.uploadedFile=uploads.getFileName();
			   }
		   }
		if(Validation.hasErrors()) {
			Logger.info("Error!!  "+ Validation.errors().toString());
        	render("@register", media);
        }
		
		
		media.save();
		return;
    }
	
	@ExternalRestrictions("Media Archiving")
    public static void showData(){
		JsonArray JSON_TABLE_DATA = new JsonArray();
    	List<models.MediaFileArchiving> media = models.MediaFileArchiving.findAll();
		for(int i=0; i< media.size();i++)
		{
			 	JsonObject g5= new JsonObject();
			 	g5.addProperty("#", media.get(i).id);
                g5.addProperty("Upload Date", media.get(i).dateOfUpload.toString());
                g5.addProperty("Type of Media", media.get(i).typeOfMedia);
                g5.addProperty("Description", media.get(i).description);
                g5.addProperty("Search Keyward", media.get(i).searchKeyward);
                if(media.get(i).accessLink!=null)
                	g5.addProperty("Link", "<a href='"+media.get(i).accessLink+"'><i class='glyphicon glyphicon-download'></i>" + "Other Link" + "</a>");
                else g5.addProperty("Link", "");
                if(media.get(i).uploadedFile!=null)
                	g5.addProperty("File", "<a href='/MediaArchiving/downloadFile?id=" + media.get(i).id +"'><i class='glyphicon glyphicon-download'></i>" + media.get(i).uploadedFile + "</a>" );
                else 	
                	g5.addProperty("File", "");
				
                JSON_TABLE_DATA.add(g5);
		}
		String header="Media Archiving List";
		render(JSON_TABLE_DATA,header);
    }
	
	
	@ExternalRestrictions("Media Archiving")
   	public static void downloadFile(Long id)
   	{
    	models.MediaFileArchiving media=models.MediaFileArchiving.findById(id);
		String path = "media/" +media.uploadedFile;
       	response.setContentTypeIfNotSet("application/force-download");
       	        //response.setContentLength(-1);
       	response.setHeader("Content-Transfer-Encoding", "binary");
       	response.setHeader("Content-Disposition","attachment; filename=\"" + media.uploadedFile );
       	renderBinary(new File(path));
   	}
	@ExternalRestrictions("Media Archiving")
    public static String delete(Long id){
		Boolean code = false;
    	String msg ="User can not be deleted";
    	Logger.info("Deleted start!!!!" +id);
    	if(request.isAjax()) {
    		
	    	notFoundIfNull(id, "id not provided");
	    	models.MediaFileArchiving media = models.MediaFileArchiving.findById(id);
	    	notFoundIfNull(media, "user not found");
	    	try{
	    		media.delete();
	    		code = true;
	    		msg = "User deleted successfully";
	    		Logger.info("Deleted!!!!");
	    	}
	    	catch(Exception e){
	    		Logger.info("Not possible" + 1000);
	    		code=false;
	    		
	    	}
    	}
    	if(code){
    		response.status=200;
    		return msg;
    		
    	}
    	else{
    		
    		response.status=303;
    		return msg;
    		
    	}
    }
	
}
