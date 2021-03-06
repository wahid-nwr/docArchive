package models;

import javax.persistence.Entity;
import net.sf.oval.constraint.Length;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import java.util.Date;

@Entity
public class UploadFile extends Model {
	@Required
	public String filename = null ;
	@Required
	public String filelocation = null ;
	@Required
	public Date dateOfUpload;
	@Required
	public String searchKeyward;
}
