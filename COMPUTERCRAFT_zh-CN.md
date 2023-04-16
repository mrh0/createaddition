# Create Crafts & Additions: Computercraft Peripheral API 中文版本

>If you are using English, please view [COMPUTERCRAFT.md](COMPUTERCRAFT.md).

>本文件为纯手动翻译，如有不足或错误欢迎修改。内容可能不是最新，请以英文版本为准。

>This document is a purely manual translation. If there are any shortcomings or errors, please feel free to modify them. The content may not be the latest, please refer to the English version.

Version 1.1

Supported Blocks:
- [电动马达](#电动马达)
- Accumulator
- Portable Energy Interface
- Redstone Relay
- Digital Adapter

# 电动马达
可以通过调用 `setSpeed(rpm)` 设置电动马达的速度。参数 *rpm* 是一个介于 `-256` 和 `256` 之间的数字。如果此函数在每秒调用的次数过多，它会抛出一个异常。
```lua
motor.setSpeed(rpm)
```
函数 `stop()` 是 `setSpeed(0)` 的简写。
```lua
motor.stop()
```
在如下的例子中，连接到电脑左侧的电动马达会以 32RPM 旋转 5s 然后停止。
```lua
local motor = peripheral.wrap("left")
motor.setSpeed(32)
sleep(5)
motor.stop()
```

函数 `rotate(degrees, [rpm])` 会返回以当前速度所需轴旋转 *degrees* 角度所需的时间。如果传递了可选的 *rpm* 参数，它会将电动马达设置到该速度并返回以新的速度将轴旋转 *degrees* 角度所需的时间。
```lua
motor.setSpeed(32)
sleep(motor.rotate(90))
motor.stop()
```
在如下的例子中，电机将首先沿顺时针方向旋转180度，然后以逆时针方向一半速度旋转180度，最终停止。
```lua
local motor = peripheral.wrap("left")
sleep(motor.rotate(180, 32))
sleep(motor.rotate(-180, 16))
motor.stop()
```

函数 `translate(blocks, [rpm])` 会返回以当前速度推动动力活塞或起重机移动 *blocks* 距离所需的时间。如果传递了可选的 *rpm* 参数，它会将电动马达设置到该速度并返回以新的速度完成动作所需的时间。
```lua
motor.setSpeed(32)
sleep(motor.translate(5))
motor.stop()
```
在以下示例中，连接到动力活塞上的电机将使动力活塞推出 5 个方块的距离，等待一秒钟，缩回，最终停止。
```lua
local motor = peripheral.wrap("left")
sleep(motor.translate(5, 32))
sleep(1)
sleep(motor.translate(-5, 32))
motor.stop()
```

函数 `getSpeed()` 会返回当前电动马达的速度。
```lua
local rpm = motor.getSpeed()
```
函数 `getStressCapacity()` 将返回产生的应力（单位su）。
```lua
local su = motor.getStressCapacity()
```
函数 `getEnergyConsumption()` 将返回以FE/t为单位的电动马达能耗。
```lua
local fe = motor.getEnergyConsumption()
```
函数 `getMaxInsert()` 将返回以FE为单位的的电动马达最大输入。
```lua
local fe = motor.getMaxInsert()
```
函数 `getMaxExtract()` 将返回以FE为单位的电动马达最大输出（始终为0）。
```lua
local fe = motor.getMaxExtract()
```
函数 `getType()` 将返回电动马达的设备名称，该名称将始终为 “electric_motor”。
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
The function `isPowered()` will return the redstone state of the Relay.
```lua
local powered = relay.isPowered()
```
The function `getType()` will return the Relay peripheral name, which will always be "redstone_relay".
```lua
print("Peripheral: " .. relay.getType())
```
# Digital Adapter
In the following example, we get the peripheral of a Digital Adapter on the left.
```lua
local da = peripheral.wrap("left")
```
The function `setTargetSpeed(side, speed)` will set the target speed of a Rotational Speed Controller attached to the side of a Digital Adapter.
```lua
setTargetSpeed("up", 64)
```
The function `getTargetSpeed(side, speed)` will get the target speed of a Rotational Speed Controller attached to the side of a Digital Adapter.
```lua
local speed = da.getTargetSpeed("up")
```
The function `getKineticStress(side)` will get the stress of a Stressometer attached to the side of a Digital Adapter.
```lua
local stress = da.getKineticStress("up")
```
The function `getKineticCapacity(side)` will get the stress capacity of a Stressometer attached to the side of a Digital Adapter.
```lua
local capacity = da.getKineticCapacity("up")
```
The function `getKineticSpeed(side)` will get the speed of a Speedometer attached to the side of a Digital Adapter.
```lua
local speed = da.getKineticSpeed("up")
```
The function `print(text)` will print a string on the currently selected line to an internal buffer which can be read by a Display Link and put on a Display Board, print will increment the currently selected line.
```lua
print("Hello World!")
```
The function `clearLine()` will clear the text on the currently selected line.

The function `clear()` will clear all the text on all lines.

The function `getLine()` will return the currently selected line (starts at 1).

The function `setLine(line)` will set the currently selected line (starts at 1).
```lua
da.print("Text on first line")
da.print("Text on second line")
da.setLine(1)
da.print("Text on first line again")
```
The function `getMaxLines()` will return the max number of lines that can be displayable using the Digital Adapter (will always return 16).

The function `getType()` will return the Adapter peripheral name, which will always be "digital_adapter".
```lua
print("Peripheral: " .. da.getType())
```
