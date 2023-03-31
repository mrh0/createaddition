# Create Crafts & Additions: Computercraft Peripheral API

Version 1

Supported Blocks:
- Electric Motor
- Accumulator
- Portable Energy Interface
- Redstone Relay

# Electric Motor
To set the speed of the Electric Motor, call `setSpeed(rpm)` where the argument *rpm* is a number between `-256` and `256`. The function will throw an exception if it is called too many times per second.
```lua
motor.setSpeed(rpm)
```
The function `stop()` is a shorthand for `setSpeed(0)`.
```lua
motor.stop()
```
In the following example, the motor attached to the left of the computer will rotate at 32RPM for 5 seconds and then stop.
```lua
local motor = peripheral.wrap("left")
motor.setSpeed(32)
sleep(5)
motor.stop()
```

The function `rotate(degrees, [rpm])` will return the time it will take to rotate the shaft by the argument *degrees* at the current speed. If the optional argument *rpm* is given it will set the speed of the motor and return the rotation time at the new speed.
```lua
motor.setSpeed(32)
sleep(motor.rotate(90))
motor.stop()
```
In the following example, the motor will first rotate 180 degrees in the clockwise direction, then 180 degrees at half the speed in the anti-clockwise direction and then finaly stop.
```lua
local motor = peripheral.wrap("left")
sleep(motor.rotate(180, 32))
sleep(motor.rotate(-180, 16))
motor.stop()
```

The function `translate(blocks, [rpm])` will return the time it will take to rotate the shaft to push a piston or gantry shaft by distance given by the argument *blocks* at the current speed. If the optional argument *rpm* is given it will set the speed of the motor and return the action time at the new speed.
```lua
motor.setSpeed(32)
sleep(motor.translate(5))
motor.stop()
```
In the following example, the motor attached to a piston will extend the piston by 5 blocks, stop for a second, retract, and then finally stop.
```lua
local motor = peripheral.wrap("left")
sleep(motor.translate(5, 32))
sleep(1)
sleep(motor.translate(-5, 32))
motor.stop()
```

The function `getSpeed()` will return the current motor speed.
```lua
local rpm = motor.getSpeed()
```
The function `getStressCapacity()` will return the produced stress capacity (output su).
```lua
local su = motor.getStressCapacity()
```
The function `getEnergyConsumption()` will return the motor energy consumption in FE/t.
```lua
local fe = motor.getEnergyConsumption()
```
The function `getMaxInsert()` will return the Motor max input in fe.
```lua
local fe = motor.getMaxInsert()
```
The function `getMaxExtract()` will return the Motor max output in fe (Always 0).
```lua
local fe = relay.getMaxExtract()
```
The function `getType()` will return the motor peripheral name, which will always be "electric_motor".
```lua
print("Peripheral: " .. motor.getType())
```
# Accumulator
In the following example, we get the peripheral of an Accumulator on the left.
```lua
local accumulator = peripheral.wrap("left")
```
The function `getEnergy()` will return the accumulator total stored charge in fe.
```lua
local fe = accumulator.getEnergy()
```
The function `getCapacity()` will return the accumulator total capacity in fe.
```lua
local fe = accumulator.getCapacity()
```
The function `getPercent()` will return the accumulator total charge in relation to the total capacity in percent.
```lua
local percent = accumulator.getPercent()
```
The function `getMaxInsert()` will return the accumulator max input per block face in fe.
```lua
local fe = accumulator.getMaxInsert()
```
The function `getMaxExtract()` will return the accumulator max output per block face in fe.
```lua
local fe = accumulator.getMaxExtract()
```
The function `getHeight()` will return the accumulator height in block.
```lua
local blocks = accumulator.getHeight()
```
The function `getWidth()` will return the accumulator width in block.
```lua
local blocks = accumulator.getWidth()
```
The function `getType()` will return the accumulator peripheral name, which will always be "modular_accumulator".
```lua
print("Peripheral: " .. accumulator.getType())
```
# Portable Energy Interface (PEI)
In the following example, we get the peripheral of a PEI on the left.
```lua
local pei = peripheral.wrap("left")
```
The function `getEnergy()` will return the connected contraption total stored charge in fe, (-1 if not connected).
```lua
local fe = pei.getEnergy()
```
The function `getCapacity()` will return the connected contraption total capacity in fe, (-1 if not connected).
```lua
local fe = pei.getCapacity()
```
The function `isConnected()` will return true if a contraption is connected.
```lua
local connected = pei.isConnected()
```
The function `getMaxInsert()` will return the PEI max input in fe.
```lua
local fe = accumulator.getMaxInsert()
```
The function `getMaxExtract()` will return the PEI max output in fe.
```lua
local fe = accumulator.getMaxExtract()
```
The function `getType()` will return the PEI peripheral name, which will always be "portable_energy_interface".
```lua
print("Peripheral: " .. pei.getType())
```
# Redstone Relay
In the following example, we get the peripheral of a Redstone Relay on the left.
```lua
local relay = peripheral.wrap("left")
```
The function `getMaxInsert()` will return the Relay max input in fe.
```lua
local fe = relay.getMaxInsert()
```
The function `getMaxExtract()` will return the Relay max output in fe.
```lua
local fe = relay.getMaxExtract()
```
The function `getThroughput()` will return the current throughput in fe.
```lua
local fe = relay.getThroughput()
```
The function `getType()` will return the Relay peripheral name, which will always be "redstone_relay".
```lua
print("Peripheral: " .. relay.getType())
```
