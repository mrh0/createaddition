# Peripheral Lua API

Version 1

Supported Blocks:
- Electric Motor

# Electric Motor

To set the speed of the Electric Motor, call setSpeed(rpm) where rpm is a number between -256 and 256:
```
motor.setSpeed(rpm)
```
In the following example, the motor attached to the left of the computer will rotate at 32RPM for 5 seconds and then stop:
```
motor = peripheral.wrap("left")
motor.setSpeed(32)
sleep(5)
motor.stop()
```
