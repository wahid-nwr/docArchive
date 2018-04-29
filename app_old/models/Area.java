package models;

import javax.persistence.Entity;

import play.db.jpa.Model;
import play.modules.chronostamp.NoChronostamp;

@Entity
@NoChronostamp
public class Area extends Model
{
	public String areaType;
}
