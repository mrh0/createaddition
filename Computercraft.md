# Get started

Supported Blocks:
- Electric Motor

# Electric Motor Peripheral Lua API

In this example:
```
motor = peripheral.wrap("left")
motor.setSpeed(32)
sleep(5)
motor.stop()
```

To set the speed of the Electric Motor, call setSpeed(rpm) where rpm is a number between -256 and 256:
```
motor.setSpeed(rpm)
```
