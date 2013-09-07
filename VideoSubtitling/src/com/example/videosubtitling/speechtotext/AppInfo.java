package com.example.videosubtitling.speechtotext;

public class AppInfo
{
    /**
     * The login parameters should be specified in the following manner:
     * 
     * public static final String SpeechKitServer = "ndev.server.name";
     * 
     * public static final int SpeechKitPort = 1000;
     * 
     * public static final String SpeechKitAppId = "ExampleSpeechKitSampleID";
     * 

     * 
     * Please note that all the specified values are non-functional
     * and are provided solely as an illustrative example.
     * 
     */

    /* Please contact Nuance to receive the necessary connection and login parameters */
	
    public static final String SpeechKitAppId = "NMDPTRIAL_clydetan20130907134142"/* Enter your ID here */;
    
	public static final byte[] SpeechKitApplicationKey =
	{
		(byte)0xbf, (byte)0x49, (byte)0x63, (byte)0x04,
		(byte)0xdd, (byte)0xc0, (byte)0x8a, (byte)0x10,
		(byte)0x20, (byte)0xca, (byte)0x77, (byte)0xb7,
		(byte)0x76, (byte)0x75, (byte)0x85, (byte)0x93,
		(byte)0x56, (byte)0x71, (byte)0x65, (byte)0xf6,
		(byte)0xad, (byte)0x37, (byte)0xe1, (byte)0x79,
		(byte)0x90, (byte)0x31, (byte)0x7a, (byte)0xc9,
		(byte)0x64, (byte)0x46, (byte)0x4a, (byte)0x23,
		(byte)0x8f, (byte)0xad, (byte)0x27, (byte)0x6c,
		(byte)0x6e, (byte)0xb1, (byte)0x7d, (byte)0x8e,
		(byte)0x7c, (byte)0xf4, (byte)0x45, (byte)0xf2,
		(byte)0x6a, (byte)0xcb, (byte)0x86, (byte)0xc0,
		(byte)0x3c, (byte)0xeb, (byte)0xb6, (byte)0xe9,
		(byte)0x22, (byte)0x98, (byte)0x62, (byte)0xb2,
		(byte)0xc9, (byte)0xdc, (byte)0x7c, (byte)0x2e,
		(byte)0x22, (byte)0xc6, (byte)0x51, (byte)0xb2
	};
	
	public static final String SpeechKitServer =
			"sandbox.nmdp.nuancemobility.net"; /* Enter your server here */;

    public static final int SpeechKitPort = 443 /* Enter your port here */;
    
    public static final boolean SpeechKitSsl = false;
}