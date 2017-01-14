package org.eclipse.leshan.client.demo;

// import java.util.Date;
// import java.util.Random;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.response.ReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAISDevice extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAISDevice.class);


	// 4008	 --- oA Device ---------------------------------------------------
    //
	// IData
	private String  ObjectVersion			= "1.0.1";				// 4001		string
	private String  DocumentaryDescription	= "DocDes 901";			//  901		string
	private Integer ErrorStatus				= 0;					//  907		opaque
	private String  OEMID					= "1 234567 890128";	//  500		string
	private String  OEMString				= "oA Device";			//  501		string
	private Integer CPUTemperature			= 42;					//  503		integer
	private Integer TotalEnergyUsage		= 123;					//  910		integer Ws
	private Integer ActualEnergyUsage		= 45;					//  911		integer 0.1W
	private String  AccuracyClass			= "123";				//  912		string

	// IConfig
	private String  MountingLocation		= "White Lady";			//  908     string
	private Integer SystemFailureTime		= 0;					//  505		integer
	private Integer TotalEnergyReportInterval	= 3600;				//  912
	private Integer ActualEnergyReportInterval	=   60;				//  913
	private Integer LessThan				= 0;					//  914
	private Integer GreaterThan				= 0;					//  915
	private Integer Step                    = 10;					//  916
	private Integer MinimumUpdateInterval	= 300;					//  917		s
    

	public OpenAISDevice() {
		this("1.0.2");
	}

	public OpenAISDevice(String version) {
		if (version != null) {
			this.ObjectVersion = version;
		}
	}

	// --- oA Device ---------------------------------------------------------

	@Override
	public ReadResponse read(int resourceid) {
		LOG.info("Read on oA Device resource " + resourceid);
		switch (resourceid) {

		// Object Version
		case 4001:
			return ReadResponse.success(resourceid, getObjectVersion());

			// Documentary Description
		case 901:
			return ReadResponse.success(resourceid, getDocumentaryDescription());

			// OEM ID
		case 500:
			return ReadResponse.success(resourceid, getOEMID());

			// OEM String
		case 501:
			return ReadResponse.success(resourceid, getOEMString());

		default:
			return super.read(resourceid);
		}
	}

	// --- oA Device ---------------------------------------------------------

	// 4001	Object Version
	private String getObjectVersion() {
		return ObjectVersion;
	}

	// 901	Documentary Description
	private String getDocumentaryDescription() {
		return DocumentaryDescription;
	}

	// 500	OEM ID
	private String getOEMID() {
		return OEMID;
	}

	// 501	OEM String
	private String getOEMString() {
		return OEMString;
	}
    
}