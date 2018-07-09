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
    String output = "";
    String tokenizer = "@";
    int chk = DHT.read11(DHT11_PIN);
    if(DHT.temperature != -999.00){
          
      //Serial.print("{ Temperature = ") ;
      //Serial.print(DHT.temperature);
       //Serial.print("}");
    }
    if(DHT.humidity != -999.00){
      
     // Serial.print("{ Humidity = ");
     // Serial.print(DHT.humidity);
     // Serial.print("} ");
    }
  
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
             //BTserial.write("@10.00;20.00;30.00@");     
            // Serial.print( bytesSent);
             //BTserial.write("@100.25;100.00;100.00@");
          }
          delay(10);
    if (Serial.available())
    {
        //BTserial.write(Serial.read());
    }
    
}
