# GyroBox
I was always bothered by some android apps not having sensor-based gestures and controls , although they could do it easily and if they did , that would be pretty convenient to use. So this is my silly attempt to implement that feature by using external app.

GyroBox can add gyroscope controls to every apps , every games that lacks these controls. You can scroll , swipe on your android screen just by rotating your phone accordingly. Playing games that does not have sensor controls and needs manual swipe on screen? GyroBox can help you. GyroBox can automatically start or stop itself whenever specified apps or games are launched/closed , that makes it easy to use. GyroBox has custom sensitivity and other custom settings, to adjust your need and increase accuracy.

DO NOT USE GYROBOX WITH APPS THAT ALREADY HAVE THESE CONTROLS, THAT CAN CAUSE SOME TEMPORARY PERFORMANCE IMPACT ON YOUR DEVICE.
<b>WARNING: THIS IS A BETA VERSION, THEREFORE YOU MAY ENCOUNTER BUGS. IF YOU DO, OPEN AN ISSUE VIA OUR GITHUB REPOSITORY.</b>

## Download GyroBox

Download Latest Apk version 1.1:
[Download]

## Usage FAQ
Q>What is GyroBox?

A>GyroBox is an app designed to give you more convenient controls over your phone , through your phone's sensor.

This App processes your devices's various sensors data and helps you to navigate through your phone or specific apps.

GyroBox also offers wide customisations over its controls, automatic actions and intensity of those actions to fit your needs.


Q>Why does it need accessibiity permissions?

A>GyroBox needs Accessibility Service to perform gestures on screen on behalf of the user..

Except this permission , functionalities of GyroBox would be corrupted and it won't work.

GyroBox does not collect any of your personal information, every action done on your device, remains in your device.


Q>Why does it need to override Battery Optimisations?

A>Android System prevents apps from working in background to save more battery, This method breaks some features of this app.

GyroBox needs to be active always in the background in order to provide you features and keep it's services alive.

Although, GyroBox doesnot do heavy processing in the background to highly impact phone's performance, It only collects device sensor information from background. 


Q>Why does it need usage access?

To use App AutoStart , GyroBox needs to know what apps are you using at any moment.

To access info about the current running app, this permission must be accessed.


Q>Why is GyroBox not working?

A>Your device needs to be minimum Android 7 Nougat (API Level 24) or latest in order to run GyroBox.

If your device is latest but still GyroBox does not work,try clearing App data and re-enabling permissions.


Q>Usage of 'Sensitivity' slider?

A>GyroBox performs gestures whenever your device sensor state changes.

To adjust how much sensor state change would matter to perform a single gesture, adjust sensitivity.


Usage:

set low sensitivity to perform gestures when high angular rotations happen,

set high sensitivity to perform gestures at very little angular rotations of the device.


Q>Usage of 'Configure Apps' or 'Apps' tab?

A>GyroBox can automatically start or stop when you are using a specific app,
Just go to apps section and enable autostart, then enable the apps you want to use GyroBox with.

Long press on an enabled app on the list to disable it , and long press on an disabled app on the list to enable it.


Q>Usage of 'Swipe Radius' slider?

A>You can customise the length of gestures performed on screen,
Just drag the swipe radius slider according to your preferred size.

(minimum value = 60px , maximum value= 600px)

Q>Usage of 'Auto Radius'?

A>Auto radius determines automatically how much length of a gesture should be performed.
It analyzes your device movement impact and calculates the radius value depending on the auto radius factor value.

Q>Usage of 'Speed' slider?
A>Choose how fast gestures occur, set the Speed slider to match your expected speed for convenient navigation.

(for lower end devices, it is recommended not to set speed more than 50%)

Q>What is 'Trigger Preference'?

A>Do you feel easy to rotate your phone around or to tilt your phone for gestures?
GyroBox works in both modes , but you can still set your preference to get more accuracy...

Q>What is 'Mode Selector'?

A>GyroBox has two modes of operation:

1.Simple Mode-

Simple mode keeps performing gestures continuously until you restore phone to its original rotation state, This mode can perform auto scrolls or auto swipes without any user interaction..

select Simple Mode, Start GyroBox and rotate your device once to keep performing corresponding gesture until you rotate back the device to its prior rotation.

2.Adjust Mode

Adjust mode performs one gesture for every rotation change.This mode performs the corresponding gesture when device rotated, and performs another opposite gesture when device is restored to its prior state.

select Adjust Mode,start GyroBox and rotate your device once towards any direction to perform that gesture action once, rotate back to prior rotation to perform a reverse action.

(when using simple mode, it is recommended to reduce sensitivity and speed. Simple mode uses more resources from your device, this may have temporary performance impact on your device)


Q>What is 'Invert Sensor'?

A>You can use invert sensor options to perform reversed gestures on that same device movement.

Left-Right or Up-Down gestures can be applied this setting independently.


Q>What is 'Turn Off Sensor'?

A>You can turn off sensor gestures for specific directions.

Left-Right or Up-Down gestures can be applied this setting independently.

turning off gestures for a specific direction also allows you to get more accuracy for the other direction. 


Q>What is 'Success Rate'?

A>GyroBox Shows you if it could perform all the gestures perfectly.

If it could perform all the past gestures successively, you will see Success Rate to go high..

If it was interrupted during a gesture or any other accessibility services are preventing GyroBox to work properly, you will see Success Rate to go low..


## License
[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.en.html)  

GyroBox is a free software, You can use, study share and improve it at your
will. Specifically you can redistribute and/or modify it under the terms of the
[GNU General Public License](https://www.gnu.org/licenses/gpl.html) as
published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version. 
