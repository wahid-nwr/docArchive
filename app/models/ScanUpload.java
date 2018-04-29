package models;

import javax.persistence.Entity;
import net.sf.oval.constraint.Length;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import play.data.validation.Required;
import java.util.Date;

import org.hibernate.annotations.Cascade;
import javax.persistence.CascadeType;
import java.util.List;

@Entity
public class ScanUpload extends Model {
	@Required
	public String name = null ;
	@Required
	public String customerId = null;
	public String accountId = null;
	public String groupId = null;
	public String agreementId = null;
	public String description = null;
	@Required
	public String locations = null;
	@ManyToMany(cascade = {CascadeType.ALL})
	public List<DocDescType> listOfDocDescType;
}
