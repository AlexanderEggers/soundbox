# Sound of HAW
Targeting specific sensors of Android (distant sensor via beacons, "heartbeat", moving phone) and executing events (sending a value via bluetooth) which will modify sound samples.

## Bluetooth
You need two devices which will act as slave and master. The master device can be activated via the menu button "Master".
The slave device can be activated via one of the other menu buttons. It's important that the master device is connected to the bluetooth
system as soon as possible to prevent timeouts if a slave tries to connect to the master device.

## Sensors

##### General
The internal sensors of this project can be changed in the certain logic files. These files can be found in the ".logic" package.
Each sensor has specific methods which are important to the whole sensor concept.

* Every sensor logic class is implementing the SensorEventListener and the SlaveLogic interface.
* startLogic(Activity context) - This method is responsible for starting and picking the right sensor from the device. It's important
to check if this sensor is avaiable to prevent further problems!
* onSensorChanged(SensorEvent event) - This method is the main part of the sensor logic and will be called every time the internal sensor
is tracking a new value. Here we need to prepare the value which will send to the master device.
  * sendSensorData(int identifier, int beaconID, float value) - This method should be used to send the values of the sensor to the master device. It's important that the identifier is correct! The identifier ids are located in the ".Util" class.
* onAccuracyChanged(Sensor sensor, int accuracy) - We are probably not going use this method but you need to add this method due to the SensorEventListener.
* onResume() and onPause() - Both methods are responsible to register/unregister the event listener. 

##### Beacon
The beacon implementation can be found in the ".logic.BeaconLogic" file. The important part of this logic class is the method "didRangeBeaconsInRegion" which is attached to a listener. This listener is called in certain intervals. This method checks the current distance to all avaiable beacon in it's region (region = specific range of the device). If a certain distance to a beacon has been reached (can be changed in the final double value inside the class), the becon logic will send the current distance to the master device.

##### Heartbeat
TODO

##### Acceleration
TODO

## Audio
TODO

## Requirements
To run this app the device needs to match certain requirements:
* Bluetooth
* Min SDK Level: 21 (Android 5.0 - Lollipop)
* Sensors:
  * Acceleration sensor
  * Beacon(s) to track the distance
  * External tool to track the heartbeat
