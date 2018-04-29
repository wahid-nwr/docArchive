package models;

import javax.persistence.Entity;
import net.sf.oval.constraint.Length;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import javax.persistence.OneToOne;
import play.data.validation.Required;
import java.util.Date;

@Entity
public class DocDescType extends Model {
	@OneToOne
	public DocDescType parent;
	@Required
	public String name = null ;
	@Required
	public String descCategory = null ;
	@Required
	public int level;
}
