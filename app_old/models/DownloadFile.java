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
public class DownloadFile extends Model {
	@Required
	public String name = null ;
	public String description = null;
	@Required
	public String locations = null;
}
