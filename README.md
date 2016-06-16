# Soundbox
Targeting the acceleration sensor inside an android device and executing events if the device is in range of an iBeacon (sending a value via bluetooth) which will modify sound filters.

## Usage
1. Choose a master device
2. Place the beacons in the room (each beacon should have a min distance of 3 meters to the other beacons)
3. Beacon configuration
4. Take the master device and go to the first beacon. 
5. Push the "settings" button. The settings page should show a bluetooth address at the top. 
6. Now decide a color you want to show the slave devices of they are in range of this beacon. This color can include 0-9 and A-Z (no special characters!). 
7. After this you decide an audio mode. The mode decides the audio filter which will modified later.
8. At last, decide if your beacon should allow the usage of gravity based accerlation calculation (make it possible to address movement of the device, like shaking)
9. Hit save.
10. Repeat the steps (6 - 9) for all beacons.
11. Take a slave device, push the "sensor" button and go to a beacon.

## Functions
##### Bluetooth
You need two devices which will act as slave and master. The master device can be activated via the menu button "Master".
The slave device will be activated via acceleration button. It's important that the master device is connected to the bluetooth system as soon as possible to prevent timeouts if a slave tries to connect to the master device.

##### Acceleration
The only sensor we are going to use is the acceleration sensor which will only be triggered if the device is in range of a beacon (within the min range). The slave device will perform a handshake to the master to make sure that it's device is allow to use this specific beacon acceleration sensor.

The acceleration logic can be found in the .logic package. All three axis of the acceleration (x,y,z) are merged into one string value which will be send to the master device.

##### Beacon
The beacon implementation can be found in the ".logic.BeaconLogic" file. We are using the android beacon library (https://github.com/AltBeacon/android-beacon-library) to access the beacons. The important part of this logic class is the method "didRangeBeaconsInRegion" which is attached to a listener. This listener is called in certain intervals. This method checks the current distance to all avaiable beacon in it's region (region = specific range of the device). If a certain distance to a beacon has been reached (can be changed in the final double value inside the class), the device will try to "login" to this specific beacon by sending a login request to the master device. It can take around 1-3 secounds until a device has been fully (un-)registered at the master device.

##### Audio
The audio part is seperated into several audio modes. Each audio mode is modifing another sound filter. We are using the pure data implementation (https://github.com/libpd/pd-for-android) to create specific audio filters. Those audio filter are accessed via the class logic.AudioModeLogic. The pure data sound file (which includes all sound samples and filter) can be found in res/raw/ folder.

##### Settings
Settings are responsible for changing the audio mode and the color of a certain beacon. All beacons are mapped by the master device regarding these values. The color value will change the slave device background color and the audio mode is responsible for the specific modification of the audio sample by the slave acceleration sensor.

## Requirements
To run this app the device needs to match certain requirements:
* Bluetooth
* Min SDK Level: 19 (Android 4.4.x - KitKat)
* Acceleration sensor
* Beacon(s)
* Fine location permission
