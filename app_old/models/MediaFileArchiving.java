package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class MediaFileArchiving extends Model{
	
	
	/*Work Register Form Fields*/
	
	@Required
	public Date dateOfUpload;
	
	@Required
	public String typeOfMedia;
	
	@Required
	public String description;
	
	@Required
	public String searchKeyward;
	
	
	public String uploadedFile;
	
	
	public String accessLink;
	
}
