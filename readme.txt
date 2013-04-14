Noise Logging App is and android application that used the microphone on the phone as a sensor to record the audio in
a location, calculate the sound pressure levels and send the data as a feed to the Cosm cloud.
 
The ES2 Library, written by Neal Lathia which can be found here: https://github.com/nlathia/SensorManager, has been used here use the microphone in android to record the surrounding noise.
 
The jar files imported from ES2 library are, sensordatamanager.jar, sensormanager.jar and json_simlpe-1.1.jar. The microphone gets used to record the data then using the csv formatter into a csv file and then split in to array so that the calculations can be applied to get the sound pressure level.
 
The duration of the recording here is 5 seconds.
 
To calculate the sound pressure level (Spl), the measures sound pressure (Prms) is calculated by taking the square root of the mean of all the sampled points.
Then dividing the Prms by the reference sound pressure in air, 20 and then taking log in base 10 of the result
and multiplying it by 20.
 
This app has been tested on android 4.0.4 successfully.