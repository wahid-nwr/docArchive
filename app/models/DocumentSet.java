package models;

import javax.persistence.Entity;
import net.sf.oval.constraint.Length;

import play.data.validation.Unique;
import play.db.jpa.Model;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import play.data.validation.Required;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.annotations.Cascade;
import javax.persistence.CascadeType;

import javax.persistence.ElementCollection;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
public class DocumentSet extends Model {
	public String name = null ;
	public Calendar created = null ;
	//document dto
	@ManyToMany(cascade = {CascadeType.ALL})
	public List<UploadFile> documents = new ArrayList<UploadFile>();
}
