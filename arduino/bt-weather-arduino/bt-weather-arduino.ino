// Basic Bluetooth sketch HC-06_01
// Connect the Hc-06 module and communicate using the serial monitor
//
// The HC-06 defaults to AT mode when first powered on.
// The default baud rate is 9600
// The Hc-06 requires all AT commands to be in uppercase. NL+CR should not be added to the command string
//
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
    
    int chk = DHT.read11(DHT11_PIN);/**
  Serial.print("Temperature = ");
 (DHT.temperature);
  Serial.print("Humidity = ");
  Serial.println(DHT.humidity);
  **/
    // Keep reading from HC-06 and send to Arduino Serial Monitor
    if (BTserial.available())
    {  
        Serial.write(BTserial.read());
    }
 
    // Keep reading from Arduino Serial Monitor and send to HC-06
         //dummy data
         BTserial.write("27.00;51.99;10.00##");
         delay(5000);
    if (Serial.available())
    {
        //BTserial.write(Serial.read());
    }
    
}
