# Peripheral Lua API

Version 1

Supported Blocks:
- Electric Motor

# Electric Motor

To set the speed of the Electric Motor, call setSpeed(rpm) where rpm is a number between -256 and 256.
```
motor.setSpeed(rpm)
```
The function stop() is a shorthand for setSpeed(0).
```
motor.stop()
```
In the following example, the motor attached to the left of the computer will rotate at 32RPM for 5 seconds and then stop.
```
motor = peripheral.wrap("left")
motor.setSpeed(32)
sleep(5)
motor.stop()
```

The function rotate(degrees, rpm, [initial]) will start 

The function getSpeed() will return the current motor speed.
```
rpm = motor.getSpeed()
```
The function getStressCapacity() will return the produced stress capacity (output su).
```
su = motor.getStressCapacity()
```
The function getEnergyConsumption() will return the motor energy consumption in FE/t.
```
fe = motor.getEnergyConsumption()
```
The function getType() will return the motor peripheral name, which will allways be "electric_motor".
```
print("Peripheral: " .. motor.getType())
```
