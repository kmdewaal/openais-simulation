package org.eclipse.leshan.client.demo;

// import java.util.Date;
// import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.eclipse.leshan.core.model.ResourceModel.Type;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAISM33PushButtonSensor extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAISM33PushButtonSensor.class);


	// --- 5002 --- oA M33 Push-Button Sensor --------------------------------

    // IData
	private Integer   PushButtonStatusValue = 0;				//  201		enum 0, 1, 2
	private Integer   PushButtonEventValue  = 0;				//  202		enum 0, 1, 2, 3, 255
	private String    ObjectVersion	        = "1.0.1";			// 4001		string
	// IConfig
	private Integer   SingleClickTime       = 300;				//  203		uint16
	private List<Long>    ApplicationGroupID;					//  903		uint16 multi  max 8 elements
	private List<String>  BindingURI;							//  999		string multi  max 8 elements
	private Boolean   DigitalInputPolarity  = false;			// 5502		boolean
	private Integer   DigitalInputDebouncePeriod = 50;			// 5503		uint16

	public OpenAISM33PushButtonSensor() {
		this("1.0.5002");
	}

	public OpenAISM33PushButtonSensor(String version) {
		if (version != null) {
			this.ObjectVersion = version;
		}
		ApplicationGroupID = new ArrayList<Long>();
		ApplicationGroupID.add(0,10L);						// Group ID 10 in array element 0
		ApplicationGroupID.add(1,11L);						// Group ID 11 in array element 1
		
		BindingURI = new ArrayList<String>();
		BindingURI.add(0, "BindingUri#0");					// URI in array element 0
		BindingURI.add(1, "BindingUri#1");					// URI in array element 1
	}

	// --- 5002 --- oA M33 Push-Button Sensor --------------------------------

	@Override
	public ReadResponse read(int resourceid) {
		LOG.info("Read on oA M33 Push-Button Sensor resource " + resourceid);
		switch (resourceid) {

		// Object Version
		case 4001:
			return ReadResponse.success(resourceid, ObjectVersion);

			// Push-Button Status Value
		case 201:
			return ReadResponse.success(resourceid, PushButtonStatusValue);

			// Push-Button Event Value
		case 202:
			return ReadResponse.success(resourceid, PushButtonEventValue);

			// Digital Input Polarity
		case 5502:
			return ReadResponse.success(resourceid, DigitalInputPolarity);

			// Digital Input Debounce Period
		case 5503:
			return ReadResponse.success(resourceid, DigitalInputDebouncePeriod);

			// Single Click Time
		case 203: {
			return ReadResponse.success(resourceid, SingleClickTime);
		}

		// Application Group ID
		case 903: {
			Map<Integer, Long> map = new HashMap<Integer, Long>();
			for (int i=0; i<ApplicationGroupID.size(); i++) {
				map.put(i,  ApplicationGroupID.get(i));
			}
			return ReadResponse.success(resourceid, map, Type.INTEGER);
		}

		// Binding URI
		case 999: {
			Map<Integer, String> map = new HashMap<Integer, String>();
			for (int i=0; i<BindingURI.size(); i++) {
				map.put(i,  BindingURI.get(i));
			}
			return ReadResponse.success(resourceid, map, Type.STRING);
		}

		default:
			return super.read(resourceid);
		}
	}

	@Override
	public WriteResponse write(int resourceid, LwM2mResource value) {
		LOG.info("Write on oA M33 Push-Button Sensor resource " + resourceid);

		switch (resourceid) {

		// Single Click Time
		case 203: {
			Long lval = (Long) value.getValue();
			SingleClickTime = lval.intValue();
		}
		return WriteResponse.success();

		// Application Group ID						// TODO multi-instance resource
		case 903:{
			Long lval = (Long) value.getValue();
			ApplicationGroupID.set(0, lval);
		}
		return WriteResponse.success();

		// Binding URI								// TODO multi-instance resource
		case 999: {
			String sval = (String) value.getValue();
			BindingURI.set(0, sval);
		}
		return WriteResponse.success();

		// Digital Input Debounce Period
		case 5503: {
			Long lval = (Long) value.getValue();
			DigitalInputDebouncePeriod = lval.intValue();
		}
		return WriteResponse.success();

		default:
			return WriteResponse.badRequest("Resource does not exist or is not writeable");
		}
	}
   
}