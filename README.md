## Robo4J-rpi-camera-client
Robo4J RaspberryPi Camera client example

The example is capable to take or post images to specific network destination. The example is designed 
for standard raspberryPi camera modules (v1.3, v2). 

The **robo4j.xml** system configuration descriptor allows to the user to specif camera properties.
###### available configuration properties:
* width
* height
* timeout
* quality
* sharpness
* brightness
* contrast
* saturation
* timelapse
* exposure

````
<roboUnit id="imageController">
  <class>com.robo4j.units.rpi.camera.RaspistillUnit</class>
  <config name="com.robo4j.core.root">
      <value name="targetOut" type="String">httpClient</value>
      <value name="storeTarget" type="String">imageProvider</value>
      <value name="client" type="String">127.0.0.1</value>
      <value name="clientPort" type="String">8027</value>
      <value name="clientUri" type="String">/imageController</value>
      <value name="width" type="String">640</value>
      <value name="height" type="String">480</value>
      <value name="exposure" type="String">nightpreview</value>
      <value name="brightness" type="String">60</value>
      <value name="contrast" type="String">30</value>
      <value name="timeout" type="String">1</value>
      <value name="timelapse" type="String">100</value>
  </config>
</roboUnit>
````
NOTE: options *client*, *clientPort* are not used in standalone example execution. Both define the Robo4J unit where the taken image should be send. Don't change them, you may want to change only camera realted properties.  

properties values are according to the 'raspstill' util documentation

### Requirements:
1. clone and install robo4j project from Robo4J Github organisation
    * Robo4J will be installed into the local maven repository
    * command inside the project root folder: 
    ````
    $ gradle clean build install
    ````
2. clone Robo4J-rpi-camera-client example from Github 
    * go the the directory and create FatJar file
    * command:
    ````
    $ gralde clean fatJar
    ````
3. upload jar file to raspberryPi
    * you can use scp command:
    ````
    $ scp ./robo4j-rpi-camera-client-alpha-0.3.jar pi@x.x.x.x:/home/pi
    ````
4. run example on raspberryPi client
    ````
    $ java -jar robo4j-rpi-camera-client-alpha-0.3.jar
    ````
5. image is available over http get request
   * open your favorite browser
   * type link: http://<your raspberrypi ip>:8025/imageProvider?image.jpg
   * imageProvider is the unit reponsible for providing taken image
   * URI option: image.jpg is the access to the taken image

Notes: 
RaspberryPi must have successfully installed camera module otherwise default image will be displayed

![Default Signal Unavailable](https://github.com/Robo4J/robo4j-rpi-camera-client/blob/master/src/main/resources/20161021_NoSignal_240.jpg)

Example of Running example on raspberryPi:
````
INFO: cameraCommand:raspistill -br 60 -ex nightpreview -co 30 -w 640 -t 1 -tl 100 -h 480 -n -e jpg --nopreview -o -
State before start:
RoboSystem state Uninitialized
================================================
    httpClient                    Initialized
    httpServer                    Initialized
    imageController               Initialized
    imageProvider                 Initialized
    scheduleController            Initialized

State after start:
RoboSystem state Started
================================================
    httpClient                        Started
    httpServer                        Started
    imageController                   Started
    imageProvider                     Started
    scheduleController                Started

Press enter to quit!
````


## Building from Source
The Robo4j framework uses [Gradle][] to build.
It's required to create fatJar file and run.

## Requirements
* [Java JDK 8][]
* [Robo4j.io][] :: version: alpha-0.3

## Staying in Touch
Follow [@robo4j][] or authors: [@miragemiko][] , [@hirt][]
on Twitter. In-depth articles can be found at [Robo4j.io][] or [miragemiko blog][]

## License
The Robo4j.io Framework is released under version 3.0 of the [General Public License][].

[Robo4j.io]: http://www.robo4j.io
[miragemiko blog]: http://www.miroslavkopecky.com
[General Public License]: http://www.gnu.org/licenses/gpl-3.0-standalone.html0
[@robo4j]: https://twitter.com/robo4j
[@miragemiko]: https://twitter.com/miragemiko
[@hirt]: https://twitter.com/hirt
[Gradle]: http://gradle.org
[Java JDK 8]: http://www.oracle.com/technetwork/java/javase/downloads
[Git]: http://help.github.com/set-up-git-redirect
[Robo4j documentation]: http://www.robo4j.io/p/documentation.html
