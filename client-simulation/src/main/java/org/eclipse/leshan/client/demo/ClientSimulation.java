/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Zebra Technologies - initial API and implementation
 *     Sierra Wireless, - initial API and implementation
 *     Bosch Software Innovations GmbH, - initial API and implementation
 *******************************************************************************/

package org.eclipse.leshan.client.demo;

import static org.eclipse.leshan.LwM2mId.*;
import static org.eclipse.leshan.client.object.Security.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.util.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientSimulation {

	// OpenAIS objects, extension of codes defined in
	// import static org.eclipse.leshan.LwM2mId.LOCATION;
	public static final int oA_GROUP         = 4006;
	public static final int oA_DEVICE        = 4008;
	public static final int oA_M33ACTUATOR   = 5001;
	public static final int oA_M33PUSHBUTTON = 5002;
	public static final int oA_M33CONTROL    = 5005;
	
	private static final Logger LOG = LoggerFactory.getLogger(ClientSimulation.class);

    private final static String DEFAULT_ENDPOINT = "ClientSimulation";
    private final static String USAGE = "java -jar client-simulation.jar [OPTION]";
    
    public static void main(final String[] args) {

        // Define options for command line tools
        Options options = new Options();

        options.addOption("h", "help", false, "Display help information.");
        options.addOption("n", true, String.format(
                "Set the endpoint name of the Client.\nDefault: the local hostname or '%s' if any.", DEFAULT_ENDPOINT));
        options.addOption("b", false, "If present use bootstrap.");
        options.addOption("lh", true, "Set the local CoAP address of the Client.\n  Default: any local address.");
        options.addOption("lp", true,
                "Set the local CoAP port of the Client.\n  Default: A valid port value is between 0 and 65535.");
        options.addOption("slh", true, "Set the secure local CoAP address of the Client.\nDefault: any local address.");
        options.addOption("slp", true,
                "Set the secure local CoAP port of the Client.\nDefault: A valid port value is between 0 and 65535.");
        options.addOption("u", true, "Set the LWM2M or Bootstrap server URL.\nDefault: localhost:5683.");
        options.addOption("i", true,
                "Set the LWM2M or Bootstrap server PSK identity in ascii.\nUse none secure mode if not set.");
        options.addOption("p", true,
                "Set the LWM2M or Bootstrap server Pre-Shared-Key in hexa.\nUse none secure mode if not set.");

        // Control generation of OpenAIS objects
        options.addOption("lum", false, "Client is OpenAIS luminaire.");
        options.addOption("con", false, "Client is OpenAIS controller.");
        options.addOption("pbs", false, "Client is OpenAIS push-button sensor.");
        
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);

        // Parse arguments
        CommandLine cl = null;
        try {
            cl = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            formatter.printHelp(USAGE, options);
            return;
        }

        // Print help
        if (cl.hasOption("help")) {
            formatter.printHelp(USAGE, options);
            return;
        }

        // Abort if unexpected options
        if (cl.getArgs().length > 0) {
            System.err.println("Unexpected option or arguments : " + cl.getArgList());
            formatter.printHelp(USAGE, options);
            return;
        }

        // Abort if we have not identity and key for psk.
        if ((cl.hasOption("i") && !cl.hasOption("p")) || !cl.hasOption("i") && cl.hasOption("p")) {
            System.err.println("You should precise identity and Pre-Shared-Key if you want to connect in PSK");
            formatter.printHelp(USAGE, options);
            return;
        }

        // Get endpoint name
        String endpoint;
        if (cl.hasOption("n")) {
            endpoint = cl.getOptionValue("n");
        } else {
            try {
                endpoint = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                endpoint = DEFAULT_ENDPOINT;
            }
        }

        // Get server URI
        String serverURI;
        if (cl.hasOption("u")) {
            if (cl.hasOption("i"))
                serverURI = "coaps://" + cl.getOptionValue("u");
            else
                serverURI = "coap://" + cl.getOptionValue("u");
        } else {
            if (cl.hasOption("i"))
                serverURI = "coaps://localhost:5684";
            else
                serverURI = "coap://localhost:5683";
        }

        // get security info
        byte[] pskIdentity = null;
        byte[] pskKey = null;
        if (cl.hasOption("i") && cl.hasOption("p")) {
            pskIdentity = cl.getOptionValue("i").getBytes();
            pskKey = Hex.decodeHex(cl.getOptionValue("p").toCharArray());
        }

        // get local address
        String localAddress = null;
        int localPort = 0;
        if (cl.hasOption("lh")) {
            localAddress = cl.getOptionValue("lh");
        }
        if (cl.hasOption("lp")) {
            localPort = Integer.parseInt(cl.getOptionValue("lp"));
        }

        // get secure local address
        String secureLocalAddress = null;
        int secureLocalPort = 0;
        if (cl.hasOption("slh")) {
            secureLocalAddress = cl.getOptionValue("slh");
        }
        if (cl.hasOption("slp")) {
            secureLocalPort = Integer.parseInt(cl.getOptionValue("slp"));
        }


        // OpenAIS device type parsing to be improved.
        // Ideally we should be able to combine luminaire and control functions in one device.
        // For the time being we are either a luminaire, a controller or a push-button sensor
        // but we can be only one at a time.
        // Device and object configuration is conform the CT-M33-commissioning-flow document.
        
        // OpenAIS device types
        Boolean oalum = true;			// Luminaire by default
        Boolean oacon = false;			// Controller
        Boolean oapbs = false;			// Push-button sensor

        // OpenAIS luminaire
        if (cl.hasOption("lum")) {
        	oalum = true;
        }

        // OpenAIS controller
        if (cl.hasOption("con")) {
        	oalum = false;
        	oacon = true;
        }

        // OpenAIS push-button sensor
        if (cl.hasOption("pbs")) {
        	oalum = false;
        	oacon = false;
        	oapbs = true;
        }
        
        createAndStartClient(endpoint, localAddress, localPort, secureLocalAddress, secureLocalPort,
        		cl.hasOption("b"), serverURI, pskIdentity, pskKey, oalum, oacon, oapbs);
    }

    public static void createAndStartClient(String endpoint, String localAddress, int localPort,
            String secureLocalAddress, int secureLocalPort, boolean needBootstrap,
            String serverURI, byte[] pskIdentity, byte[] pskKey,
            Boolean oalum, Boolean oacon, Boolean oapbs) {

    	// Registration update interval, effectively minus 10%
        final int LIFETIME = 60;		// Default: 30
       
        
        // Initialize object list
        ObjectsInitializer initializer = new ObjectsInitializer();
        if (needBootstrap) {
            if (pskIdentity == null)
                initializer.setInstancesForObject(SECURITY, noSecBootstap(serverURI));
            else
                initializer.setInstancesForObject(SECURITY, pskBootstrap(serverURI, pskIdentity, pskKey));
        } else {
            if (pskIdentity == null) {
                initializer.setInstancesForObject(SECURITY, noSec(serverURI, 123));
                initializer.setInstancesForObject(SERVER, new Server(123, LIFETIME, BindingMode.U, false));
            } else {
                initializer.setInstancesForObject(SECURITY, psk(serverURI, 123, pskIdentity, pskKey));
                initializer.setInstancesForObject(SERVER, new Server(123, LIFETIME, BindingMode.U, false));
            }
        }
        initializer.setClassForObject(DEVICE, MyDevice.class);

        // === OpenAIS =======================================================

        // Common objects from LWM2M
        List<LwM2mObjectEnabler> enablers = initializer.create(
    			SECURITY,
    			SERVER,
    			DEVICE);

        // --- OpenAIS luminaire ---------------------------------------------

        if (oalum) {
        	// Create OpenAIS objects
        	OpenAISDevice                deviceInstance     = new OpenAISDevice();
        	OpenAISGroup                 groupInstance      = new OpenAISGroup();
        	OpenAISM33LightPointActuator actuatorInstance   = new OpenAISM33LightPointActuator();

        	// Initialize OpenAIS devices
        	initializer.setInstancesForObject(oA_DEVICE,        deviceInstance);
        	initializer.setInstancesForObject(oA_GROUP,         groupInstance);
        	initializer.setInstancesForObject(oA_M33ACTUATOR,   actuatorInstance);

        	enablers.add(initializer.create(oA_DEVICE));
        	enablers.add(initializer.create(oA_GROUP));
        	enablers.add(initializer.create(oA_M33ACTUATOR));
        }

        // --- OpenAIS controller --------------------------------------------

        if (oacon) {
        	// Create OpenAIS objects
        	OpenAISDevice                deviceInstance     = new OpenAISDevice();
        	OpenAISGroup                 groupInstance_1    = new OpenAISGroup();
        	OpenAISGroup                 groupInstance_2    = new OpenAISGroup();
        	OpenAISM33Control            controlInstance    = new OpenAISM33Control();

        	// Initialize OpenAIS devices
        	initializer.setInstancesForObject(oA_DEVICE,        deviceInstance);
        	initializer.setInstancesForObject(oA_GROUP,         groupInstance_1, groupInstance_2);
        	initializer.setInstancesForObject(oA_M33CONTROL,    controlInstance);

        	enablers.add(initializer.create(oA_DEVICE));
        	enablers.add(initializer.create(oA_GROUP));
        	enablers.add(initializer.create(oA_M33CONTROL));
        }

        // --- OpenAIS push-button sensor ------------------------------------

        if (oapbs) {
        	// Create OpenAIS objects
        	OpenAISDevice              deviceInstance     = new OpenAISDevice();
        	OpenAISGroup               groupInstance      = new OpenAISGroup();
        	OpenAISM33PushButtonSensor pushButtonInstance = new OpenAISM33PushButtonSensor();

        	// Initialize OpenAIS devices
        	initializer.setInstancesForObject(oA_DEVICE,        deviceInstance);
        	initializer.setInstancesForObject(oA_GROUP,         groupInstance);
        	initializer.setInstancesForObject(oA_M33PUSHBUTTON, pushButtonInstance);

        	enablers.add(initializer.create(oA_DEVICE));
        	enablers.add(initializer.create(oA_GROUP));
        	enablers.add(initializer.create(oA_M33PUSHBUTTON));
        }

        // -------------------------------------------------------------------
        
        // Create client
        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        builder.setLocalAddress(localAddress, localPort);
        builder.setLocalSecureAddress(secureLocalAddress, secureLocalPort);
        builder.setObjects(enablers);
        final LeshanClient client = builder.build();

        // Start the client
        client.start();

        // De-register on shutdown and stop client.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                client.destroy(true); // send de-registration request before destroy
            }
        });

    }
}
