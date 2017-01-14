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

public class OpenAISGroup extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAISGroup.class);


	// --- 4006 --- oA Group -------------------------------------------------
    //
	// IData
	private String  ObjectVersion			= "1.0.1";				// 4001		string
	private String  DocumentaryDescription	= "DocDes 901";			//  901		string
	private Integer ApplicationGroupID		= 0;					//  600 	integer
	private Integer SecurityGroupID         = 0;					//  601 	integer
	private Integer IPAddresses				= 0;					//  602	    integer multiple TBD map?

	// IConfig
	private String Members	= "CoRE Link Format [RFC6690]";			//  603		string multiple instances

	public OpenAISGroup() {
		this("1.0.4006");
	}

	public OpenAISGroup(String version) {
		if (version != null) {
			this.ObjectVersion = version;
		}
	}

	// --- 4006 --- oA Group -------------------------------------------------

	@Override
	public ReadResponse read(int resourceid) {
		LOG.info("Read on oA Group resource " + resourceid);
		switch (resourceid) {

		// oA Device resources

		// Object Version
		case 4001:
			return ReadResponse.success(resourceid, getObjectVersion());

			// Documentary Description
		case 901:
			return ReadResponse.success(resourceid, getDocumentaryDescription());

			// Application Group ID
		case 600:
			return ReadResponse.success(resourceid, getApplicationGroupID());

			// Security Group ID
		case 601:
			return ReadResponse.success(resourceid, getSecurityGroupID());

			// IP Addresses
		case 602:
			Map<Integer, Long> addresses = new HashMap<>();
			addresses.put(0, getIPAddresses());
			return ReadResponse.success(resourceid, addresses, Type.INTEGER);
			// KdW 20161219
			// Reason why this has to be a long:
			// The ReadResponse.success calls this one:
			// public static LwM2mMultipleResource newResource(int id, Map<Integer, ?> values, Type type) {
			//
			// In this function, when the type is defined as INTEGER it really wants a Long.
			// There is no value possible to make it accept an Integer.

			// Members
		case 603:
			return ReadResponse.success(resourceid, getMembers());

		default:
			return super.read(resourceid);
		}
	}

	// --- 4006 --- oA Group -------------------------------------------------

	// 4001	Object Version
	private String getObjectVersion() {
		return ObjectVersion;
	}

	// 901	Documentary Description
	private String getDocumentaryDescription() {
		return DocumentaryDescription;
	}

	// 600	Application Group ID
	private Integer getApplicationGroupID() {
		return ApplicationGroupID;
	}

	// 601	Security Group ID
	private Integer getSecurityGroupID() {
		return SecurityGroupID;
	}

    // 602	IP Addresses
	private Long getIPAddresses() {
		Integer i = IPAddresses;
		Long l = new Long(i);
		return l;
	}
//	Implementation options:
//	private Long getIP_Addresses() {
//		Integer i = IP_Addresses;
//		return Long.valueOf(i.longValue());
//	}
//	private Long getIP_Addresses() {
//		return Long.valueOf(IP_Addresses.longValue());
//}

	// 603	Members
	private String getMembers() {
		return Members;
	}
    
}