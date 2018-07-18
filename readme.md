ArduinoBT Weather Station

Version Alpha 1.0.3

Pre release for testing. Current versions graph is very limited. Sensor readings are stored in database once per 30 minutes after the device connects to bluetooth and receives properly formatted input.

Next Release:
Interval control for sensor storing. 
Will be adding a full page graph (day/week/month graph view), support for smaller screens (for now horizontal landscape is best on a small screen). 
Export for database of sensor readings. 

Database can be reached at from device at: access db http://localhost:8080)

or by visiting android device ip at http://192.168.254.24:8080 on a locally connected computer

This app requires Arduino with a bluetooth adapter and a temperature, humidity, and wind (anemometer) sensor. Sketch: bt-weather-arduino.ino

OR

a compatible setup that sends formatted output through the bluetooth adapter serial in the format @10.00;20.00;30.00@

Parts used:

Arduino (any variant)
HC-06 Bluetooth serial adapter
DHT11 - humidity and temperature sensor
Anemometer -- I included the code to allow for a wind sensor that I'll be adding later

Contact us with any comments and suggestions. Please submit any bugs, errors, or crashes to gnosisdevelop@gmail.com
Github repo - Developed by James Harley
	
	// Gnosis Development Arduino Bluetooth adapter paired with DHT11 Humidity/Temperature Sensor
	// Part of Android project ArduinoBT Weather Station
	#include <dht.h>

	dht DHT;

	#define DHT11_PIN 7



	 
	#include <SoftwareSerial.h>
	SoftwareSerial BTserial(2, 3); // RX | TX
	// Connect the HC-06 TX to the Arduino RX on pin 2. 
	// Connect the HC-06 RX to the Arduino TX on pin 3 through a voltage divider.
	// 
	 
	 
	void setup() 
	{
		Serial.begin(9600);
		Serial.println("Enter AT commands:");
	 
		// HC-06 default serial speed is 9600
		BTserial.begin(9600);  
	}
	 
	void loop()
	{
		String output = "";
		String tokenizer = "@";
		int chk = DHT.read11(DHT11_PIN);
	   
	  
		// Keep reading from HC-06 and send to Arduino Serial Monitor
			if (BTserial.available())
			{  
			  
			  Serial.write(BTserial.read());
			}
	 
		// Keep reading from Arduino Serial Monitor and send to HC-06
			 //dummy data
			  //BTserial.write("@");
			  output.concat(tokenizer);
			  if (DHT.temperature != -999.00 || DHT.humidity != -999.00){
					
				  if(DHT.temperature != -999.00){
					  output.concat(DHT.temperature);         
					  output.concat(";");  
				  }
				  if(DHT.humidity != -999.00){            
					output.concat(DHT.humidity);        
					output.concat(";");
				  }
				  output.concat("0.00@");
			  
				  Serial.println(output);
				  BTserial.write(output.c_str());
				 //BTserial.write("@100.25;100.00;100.00@");
			  }
			  delay(10);
		
	}