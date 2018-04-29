package models;

import javax.persistence.Entity;
import net.sf.oval.constraint.Length;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import javax.persistence.ManyToOne;
import play.data.validation.Required;


@Entity
public class Customer extends Model {
	@Required
	@Unique(message="Customer Id must be unique")
	public String customerId;
	@Required
	@Unique(message="Customer Name must be unique")
	public String customerName;
}
