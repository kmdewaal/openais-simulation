package org.eclipse.leshan.client.demo;

// import java.util.Date;
// import java.util.Random;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAISM33LightPointActuator extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAISM33LightPointActuator.class);


	// 5001		oA M33 Light-Point Actuator
	// IData
	private String  ObjectVersion		= "1.0.1";			// 4001		string
	private Integer CurrentIntensity	= 100;				//  100		uint8
	private Integer TargetIntensity		= 101;				//  101		multi-instances [uint8]
	// IConfig
	private Integer DimmingTime			= 400;				//  106		multi-instances [uint16]
	private Integer TransitionTime		= 500;				//  108		multi-instances [uint16]
	// IControl
	private Integer Switch;									//  117		uint8
	private Boolean Dim;									//  118		boolean
	//  119		no type, execute only
	private Boolean Step;									//  120		boolean
    

	public OpenAISM33LightPointActuator() {
		this("1.0.2");
	}

	public OpenAISM33LightPointActuator(String version) {
		if (version != null) {
			this.ObjectVersion = version;
		}
	}

	@Override
	public ReadResponse read(int resourceid) {
        LOG.info("Read on oA M33 Light-Point Actuator resource " + resourceid);
		switch (resourceid) {

		// oA M33 Light-Point Actuator resources

		// Current Intensity
		case 100:
			return ReadResponse.success(resourceid, getCurrentIntensity());

			// Target Intensity
		case 101:
			return ReadResponse.success(resourceid, getTargetIntensity());

			// Dimming Time
		case 106:
			return ReadResponse.success(resourceid, getDimmingTime());

			// Transition Time
		case 108:
			return ReadResponse.success(resourceid, getTransitionTime());

			// Object Version
		case 4001:
			return ReadResponse.success(resourceid, getObjectVersion());


		default:
			return super.read(resourceid);
		}
	}


    @Override
    public WriteResponse write(int resourceid, LwM2mResource value) {
        LOG.info("Write on oA M33 Light-Point Actuator resource " + resourceid);
        return WriteResponse.notFound();
    }

    @Override
    public ExecuteResponse execute(int resourceid, String params) {
        LOG.info("Execute on oA M33 Light-Point Actuator resource " + resourceid + " " + "\"" + params + "\"");
		switch (resourceid) {

		// oA M33 Light-Point Actuator resources

		// Switch
		case 117:
	        return ExecuteResponse.success();

			// Dim
		case 118:
	        return ExecuteResponse.success();

			// Stop Transition
		case 119:
	        return ExecuteResponse.success();

			// Step
		case 120:
	        return ExecuteResponse.success();

		default:
	        return ExecuteResponse.badRequest("Resource does not exist");
		}
			
			
    }
	
	
	// oA M33 Light-Point Control


	// 4001	Object Version
	private String getObjectVersion() {
		return ObjectVersion;
	}

	// 100	Current Intensity
	private Integer getCurrentIntensity() {
		return CurrentIntensity;
	}

	//    
	// 101	Target Intensity
	private Integer getTargetIntensity() {
		return TargetIntensity;
	}

	// 107	Dimming Time
	private Integer getDimmingTime() {
		DimmingTime++;							// Modifying the value on GET is not very RESTful...
		return DimmingTime;
	}

	// 108	Transition Time
	private Integer getTransitionTime() {
		TransitionTime++;						// Modifying the value on GET is not very RESTful...
		return TransitionTime;
	}
    
}