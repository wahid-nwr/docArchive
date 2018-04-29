package models;

import javax.persistence.Entity;
import net.sf.oval.constraint.Length;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import javax.persistence.*;


@Entity
public class AccountIdMap extends Model {
	@Required
	@Unique(message="Account No must be unique")
	public String accountNo;
	//@Unique(message="Customer must be unique")
	//@OneToOne(mappedBy="accountIdMap", cascade=CascadeType.ALL)
	@OneToOne (cascade=CascadeType.ALL)
	@JoinColumn(name="customer_id", unique= true, nullable=true, insertable=true, updatable=true)
	public Customer customer;
	//@Unique(message="Group must be unique")
	@OneToOne (cascade=CascadeType.ALL)
	@JoinColumn(name="customerGroup_id", unique= true, nullable=true, insertable=true, updatable=true)
	public CustomerGroup customerGroup;
	
	@PrePersist
	@PreUpdate
	public void beforeSave() {
		System.out.println("customer::"+this.customer);
		System.out.println("group::"+this.customerGroup);
	}
}
