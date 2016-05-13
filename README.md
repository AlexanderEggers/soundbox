# Sound of HAW
Targeting the acceleration sensor inside an android device and executing events if the device is in range of an iBeacon (sending a value via bluetooth) which will modify sound samples.

## Usage
TODO

## Functions
##### Bluetooth
You need two devices which will act as slave and master. The master device can be activated via the menu button "Master".
The slave device will be activated via acceleration button. It's important that the master device is connected to the bluetooth system as soon as possible to prevent timeouts if a slave tries to connect to the master device.

##### Acceleration
The only sensor we are going to use is the acceleration sensor which will only be triggered if the device is in range of a beacon (within the min range). The slave device will perform a handshake to the master to make sure that it's device is allow to use this specific beacon acceleration sensor.

The acceleration logic can be found in the .logic package. All three axis of the acceleration (x,y,z) are merged into one string value which will be send to the master device.

##### Beacon
The beacon implementation can be found in the ".logic.BeaconLogic" file. The important part of this logic class is the method "didRangeBeaconsInRegion" which is attached to a listener. This listener is called in certain intervals. This method checks the current distance to all avaiable beacon in it's region (region = specific range of the device). If a certain distance to a beacon has been reached (can be changed in the final double value inside the class), the device will try to "login" to this specific beacon by sending a login request to the master device.

##### Audio
TODO

##### Settings
Settings are responsible for changing the audio mode and the color of a certain beacon. All beacons are mapped by the master device regarding these values. The color value will change the slave device background color and the audio mode is responsible for the specific modification of the audio sample by the slave acceleration sensor.

## Requirements
To run this app the device needs to match certain requirements:
* Bluetooth
* Min SDK Level: 21 (Android 5.0 - Lollipop)
* Acceleration sensor
* Beacon(s)
  
