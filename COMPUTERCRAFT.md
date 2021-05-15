# Create Crafts & Additions: Computercraft Peripheral API

Version 1

Supported Blocks:
- Electric Motor

# Electric Motor

To set the speed of the Electric Motor, call setSpeed(rpm) where the argument *rpm* is a number between -256 and 256. The function will throw an exception if it is called too many times per second.
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

The function rotate(degrees, [rpm]) will return the time it will take to rotate the shaft by the argument *degrees* at the current speed. If the optional argument *rpm* is given it will set the speed of the motor and return the rotation time at the new speed.
```
motor.setSpeed(32)
sleep(motor.rotate(90))
motor.stop()
```
In the following example, the motor will first rotate 180 degrees in the clockwise direction, then 180 degrees at half the speed in the anti-clockwise direction and then finaly stop.
```
motor = peripheral.wrap("left")
sleep(motor.rotate(180, 32))
sleep(motor.rotate(-180, 16))
motor.stop()
```

The function translate(blocks, [rpm]) will return the time it will take to rotate the shaft to push a Piston or Gantry by distance given by the argument *blocks* at the current speed. If the optional argument *rpm* is given it will set the speed of the motor and return the action time at the new speed.
```
motor.setSpeed(32)
sleep(motor.translate(5))
motor.stop()
```
In the following example, the motor attached to a piston will extend the piston by 5 blocks, stop for a second, retract, and then finaly stop.
```
motor = peripheral.wrap("left")
sleep(motor.translate(5, 32))
sleep(1)
sleep(motor.translate(-5, 32))
motor.stop()
```

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
