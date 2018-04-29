package models;

import javax.persistence.Entity;
import net.sf.oval.constraint.Length;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import javax.persistence.ManyToOne;
import play.data.validation.Required;


@Entity
public class BeneficiaryMasterProfile extends Model {
	@Required
	public String beneficiaryId;
	public String districtCode;
	public String upazillaCode;
	public String unionCode;
	public String wardCode;
	public String villageName;
	public String beneficiaryName;
	public String previousProfession;
	public int beneficiaryAge;
	public String fatherOrHusbandName;
	public String maritalStatus;
	public int beneficiaryStatus;
}
