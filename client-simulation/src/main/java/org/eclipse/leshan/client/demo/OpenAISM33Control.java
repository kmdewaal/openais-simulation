package org.eclipse.leshan.client.demo;

import java.util.HashMap;
import java.util.Map;

// import java.util.Date;
// import java.util.Random;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ResourceModel.Type;
// import org.eclipse.leshan.core.node.LwM2mMultipleResource;
import org.eclipse.leshan.core.response.ReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAISM33Control extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAISM33Control.class);


	// --- 5005 --- oA M33 Control -------------------------------------------
    //
	// IData
	private String  ObjectVersion			= "1.0.1";				// 4001		string
	private String  DocumentaryDescription	= "DocDes 901";			//  901		string
	private Integer CurrentProgram			= 0;					//    1 	uint16

	// IConfig
	private Integer ProgramInvocationConfiguration = 0;				//    2	    struct!! TBD

	// IDebug
	private Boolean DebugModeEnabled	= false;					//  905		boolean
	private Boolean ObjectEnabled		= false;					//  906		boolean
	
	public OpenAISM33Control() {
		this("1.0.5005");
	}

	public OpenAISM33Control(String version) {
		if (version != null) {
			this.ObjectVersion = version;
		}
	}

	// --- 5005 --- oA M33 Control -------------------------------------------

	@Override
	public ReadResponse read(int resourceid) {
		LOG.info("Read on oA M33 Control resource " + resourceid);
		switch (resourceid) {

		// oA Device resources

		// Object Version
		case 4001:
			return ReadResponse.success(resourceid, getObjectVersion());

			// Documentary Description
		case 901:
			return ReadResponse.success(resourceid, getDocumentaryDescription());

			// Current Program
		case 1:
			return ReadResponse.success(resourceid, getCurrentProgram());

			// Program Invocation Configuration
		case 2:
			return ReadResponse.success(resourceid, getProgramInvocationConfiguration());

//			// IP Addresses
//		case 602:
//			Map<Integer, Long> addresses = new HashMap<>();
//			addresses.put(0, getIPAddresses());
//			return ReadResponse.success(resourceid, addresses, Type.INTEGER);
//			// KdW 20161219
//			// Reason why this has to be a long:
//			// The ReadResponse.success calls this one:
//			// public static LwM2mMultipleResource newResource(int id, Map<Integer, ?> values, Type type) {
//			//
//			// In this function, when the type is defined as INTEGER it really wants a Long.
//			// There is no value possible to make it accept an Integer.

			// Debug Mode Enabled
		case 905:
			return ReadResponse.success(resourceid, getDebugModeEnabled());

			// Object Enabled
		case 906:
			return ReadResponse.success(resourceid, getObjectEnabled());

		default:
			return super.read(resourceid);
		}
	}

	// --- 5005 --- oA M33 Control -------------------------------------------

	// 4001	Object Version
	private String getObjectVersion() {
		return ObjectVersion;
	}

	// 901	Documentary Description
	private String getDocumentaryDescription() {
		return DocumentaryDescription;
	}

	// 1	Current Program
	private Integer getCurrentProgram() {
		return CurrentProgram;
	}

	// 2	Program Invocation Configuration		TBD Struct!!
	private Integer getProgramInvocationConfiguration() {
		return ProgramInvocationConfiguration;
	}

//    // 602	IP Addresses
//	private Long getIPAddresses() {
//		Integer i = IPAddresses;
//		Long l = new Long(i);
//		return l;
//	}
////	Implementation options:
////	private Long getIP_Addresses() {
////		Integer i = IP_Addresses;
////		return Long.valueOf(i.longValue());
////	}
////	private Long getIP_Addresses() {
////		return Long.valueOf(IP_Addresses.longValue());
////}

	// 905	Debug Mode Enabled
	private Boolean getDebugModeEnabled() {
		return DebugModeEnabled;
	}

	// 906	Object Enabled
	private Boolean getObjectEnabled() {
		return ObjectEnabled;
	}
    
}