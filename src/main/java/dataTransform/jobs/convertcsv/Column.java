package dataTransform.jobs.convertcsv;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dataTransform.jobs.convertcsv.transformations.Transformation;
import dataTransform.jobs.convertcsv.transformations.UnrecognisedValueException;
import dataTransform.jobs.convertcsv.transformations.date.DateConverter;
import dataTransform.jobs.convertcsv.transformations.id.SystemId;
import dataTransform.jobs.convertcsv.transformations.properties.Property;
import dataTransform.jobs.convertcsv.transformations.string.Concatenate;
import dataTransform.jobs.convertcsv.transformations.string.Email;
import dataTransform.jobs.convertcsv.transformations.string.LowerCase;
import dataTransform.jobs.convertcsv.transformations.string.Seperate;
import dataTransform.jobs.convertcsv.transformations.string.UpperCase;
import dataTransform.jobs.convertcsv.transformations.string.Validate;

@XmlRootElement(name="column")
public class Column {

	private static Logger log = LogManager.getLogger(Column.class);

	private String sourceName = "";
	private int sourceIndex = -1;
	private String destinationName = "";
	private String destinationDataType = "text";
	private String defaultValue = "";
	private String includeIfOtherColumnEquals = "";
	private String includeOtherColumnId = "";
	private boolean excludeRowIfEmpty = false;
	private boolean appendUnrecognisedValues = false;
	private List<Transformation> transformationList = new ArrayList<Transformation>();

	
	@XmlAttribute
	public String getSourceName() {return sourceName;}
	public void setSourceName(String $in) {
		sourceName = $in;
		log.trace("setting sourceName : " + sourceName);
	}
	
	@XmlAttribute
	public int getSourceIndex() {return sourceIndex;}
	public void setSourceIndex(int $in) {
		sourceIndex = $in;
		log.trace("setting sourceIndex : " + sourceIndex);
	}
	
	@XmlAttribute
	public String getDestinationName() {return destinationName;}
	public void setDestinationName(String $in) {
		destinationName = $in;
		log.trace("setting destinationName : " + destinationName);
	}
		
	@XmlAttribute
	public String getDestinationDataType() {return destinationDataType;}
	public void setDestinationDataType(String $in) {
		destinationDataType = $in;
		log.trace("setting destinationDataType : " + destinationDataType);
	}

	@XmlAttribute
	public String getIncludeIfOtherColumnEquals() {return includeIfOtherColumnEquals;}
	public void setIncludeIfOtherColumnEquals(String $in) {
		includeIfOtherColumnEquals = $in;
		log.trace("setting includeIfOtherColumnEquals : " + includeIfOtherColumnEquals);
	}

	@XmlAttribute
	public String getIncludeOtherColumnId() {return includeOtherColumnId;}
	public void setIncludeOtherColumnId(String $in) {
		includeOtherColumnId = $in;
		log.trace("setting includeOtherColumnId : " + includeOtherColumnId);
	}

	@XmlAttribute(name = "default")
	public String getDefaultValue() {return defaultValue;}
	public void setDefaultValue(String $in) {
		defaultValue = $in;
		log.trace("setting defaultValue : " + defaultValue);
	}
	
	@XmlAttribute
	public boolean getExcludeRowIfEmpty() {return excludeRowIfEmpty;}
	public void setExcludeRowIfEmpty(boolean $in) {
		excludeRowIfEmpty = $in;
		log.trace("setting excludeRowIfEmpty : " + excludeRowIfEmpty);
	}
	
	@XmlAttribute
	public boolean getAppendUnrecognisedValues() {return appendUnrecognisedValues;}
	public void setAppendUnrecognisedValues(boolean $in) {
		appendUnrecognisedValues = $in;
		log.trace("setting appendUnrecognisedValues : " + appendUnrecognisedValues);
	}
	
	@XmlElements({ 
	    @XmlElement(name="concatenate", type=Concatenate.class),
	    @XmlElement(name="lowercase", type=LowerCase.class),
	    @XmlElement(name="uppercase", type=UpperCase.class),
	    @XmlElement(name="email", type=Email.class),
	    @XmlElement(name="seperate", type=Seperate.class),
	    @XmlElement(name="systemId", type=SystemId.class),
	    @XmlElement(name="date", type=DateConverter.class),
	    @XmlElement(name="validate", type=Validate.class),
	    @XmlElement(name="property", type=Property.class)
	})
	@XmlElementWrapper(name = "transformations")
	public List<Transformation> getTransformationList() {return transformationList;}
	public void setTransformationList(List<Transformation> $in) {
		transformationList = $in;
		log.trace("setting transformationList : " + transformationList);
	}
	
	public String getValue(CSVRecord $csvRecord) throws UnrecognisedValueException {
		
		String initialValue = "";
		try {
			if (this.getSourceIndex() > -1) {
				initialValue = $csvRecord.get(this.getSourceIndex());
			} else {			
				initialValue = $csvRecord.get(this.getSourceName());
			}
			log.debug("INITIAL VALUE : " + initialValue);
		} catch (Exception e) {
			initialValue = "";

			log.debug("INITIAL VALUE Exception : " + e);
		}
		
		if (initialValue.toLowerCase().equals("null")) {
			initialValue = "";
		}
		
		String retVal = transform(initialValue, $csvRecord);
		
		
		if (retVal == null || retVal.equals("") || retVal.equals("NULL")) {
			retVal = this.getDefaultValue();
			
		}
		
		log.debug("0. Other Column Id = " + this.getIncludeOtherColumnId());
		
		if (!this.getIncludeOtherColumnId().equals("")) {
			
			String otherColumnValue = $csvRecord.get(this.getIncludeOtherColumnId());
			
			if (!otherColumnValue.equals(this.getIncludeIfOtherColumnEquals())) {
				retVal = this.getDefaultValue();
				
			} 
			
		}
		
		return retVal;
	}
	
	public String getRawValue(CSVRecord $csvRecord) {
		
		String initialValue = "";
		try {
			initialValue = $csvRecord.get(this.getSourceName());
			
		} catch (Exception e) {
			initialValue = "";
			
		}
		
		return initialValue;
	}
	
	private String transform(String $in, CSVRecord $csvRecord) throws UnrecognisedValueException {
		
		String retVal = $in;
		
		for (Transformation transformation : this.getTransformationList()) {
			retVal = transformation.transform(retVal, $csvRecord);
		}
		
		return retVal;
	}
	
}
