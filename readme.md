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