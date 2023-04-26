# Create Crafts & Additions: Computercraft Peripheral API 中文版本

>If you are using English, please view [COMPUTERCRAFT.md](COMPUTERCRAFT.md). Links to other languages (if any) are in that file too.

>本文件为纯手动翻译，如有不足或错误欢迎修改。
>我已经尽量保证译名与模组翻译文件同步。
>内容可能不是最新，请以英文版本为准。

>This document is a purely manual translation. If there are any shortcomings or errors, please feel free to modify them.
>I have tried my best to ensure that the translated name is synchronized with the mod translation file.
>The content may not be the latest, please refer to the English version.

Version 1.1

支持的方块:
- [电动马达](#电动马达)
- [蓄电池](#蓄电池)
- [移动式能量接口](#移动式能量接口-pei)
- [红石继电器](#红石继电器)
- [数字适配器](#数字适配器)
  - [速度表](#速度表)
  - [应力表](#应力表)
  - [绳索滑轮](#绳索滑轮)
  - [动力活塞](#动力活塞)
  - [动力轴承](#动力轴承)
  - [显示链接器](#显示链接器)
  - [Other](#other)

# 电动马达
可以通过调用 `setSpeed(rpm)` 设置电动马达的速度。参数 *rpm* 是一个介于 `-256` 和 `256` 之间的数字。如果此函数在每秒被调用的次数过多，它会抛出一个异常。
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

函数 `rotate(degrees, [rpm])` 会返回以当前速度将轴旋转 *degrees* 角度所需的时间。如果传递了可选的 *rpm* 参数，它会将电动马达设置到该速度并返回以新的速度将轴旋转 *degrees* 角度所需的时间。
```lua
motor.setSpeed(32)
sleep(motor.rotate(90))
motor.stop()
```
在如下的例子中，电机将首先沿顺时针方向旋转180度，然后沿逆时针方向以一半速度旋转180度，最终停止。
```lua
local motor = peripheral.wrap("left")
sleep(motor.rotate(180, 32))
sleep(motor.rotate(-180, 16))
motor.stop()
```

函数 `translate(blocks, [rpm])` 会返回以当前速度推动动力活塞或起重机移动 *blocks* 方块所需的时间。如果传递了可选的 *rpm* 参数，它会将电动马达设置到该速度并返回以新的速度完成动作所需的时间。
```lua
motor.setSpeed(32)
sleep(motor.translate(5))
motor.stop()
```
在如下的例子中，连接到动力活塞上的电机将使动力活塞推出 5 个方块的距离，等待一秒钟，缩回，最终停止。
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
函数 `getStressCapacity()` 将返回产生的应力（单位：su）。
```lua
local su = motor.getStressCapacity()
```
函数 `getEnergyConsumption()` 将返回电动马达当前能耗（单位：FE/t）。
```lua
local fe = motor.getEnergyConsumption()
```
函数 `getMaxInsert()` 将返回电动马达的最大输入（单位：FE）。
```lua
local fe = motor.getMaxInsert()
```
函数 `getMaxExtract()` 将返回以FE为单位的电动马达最大输出（始终为0）。
```lua
local fe = motor.getMaxExtract()
```
函数 `getType()` 将返回电动马达的设备名称，该名称将始终为 "electric_motor"。
```lua
print("Peripheral: " .. motor.getType())
```
# 蓄电池
在如下的例子中，我们获取左侧蓄电池的外围设备句柄。
```lua
local accumulator = peripheral.wrap("left")
```
函数 `getEnergy()` 会返回蓄电池已经存储的能量（单位：FE）。
```lua
local fe = accumulator.getEnergy()
```
函数 `getCapacity()` 会返回蓄电池的总容量（单位：FE）。
```lua
local fe = accumulator.getCapacity()
```
函数 `getPercent()` 将返回蓄电池相对于总容量的总充电量（单位：%）。
```lua
local percent = accumulator.getPercent()
```
函数 `getMaxInsert()` 将返回蓄电池每个方块表面的最大输入（单位：FE）。
```lua
local fe = accumulator.getMaxInsert()
```
函数 `getMaxExtract()` 将返回蓄电池每个方块表面的最大输出（单位：FE）。
```lua
local fe = accumulator.getMaxExtract()
```
函数 `getHeight()` 会返回蓄电池多方块结构的高度（单位：方块）。
```lua
local blocks = accumulator.getHeight()
```
函数 `getWidth()` 会返回蓄电池多方块结构的宽度（单位：块）。
```lua
local blocks = accumulator.getWidth()
```
函数 `getType()` 将返回蓄电池的设备名称，该名称将始终为  "modular_accumulator"。
```lua
print("Peripheral: " .. accumulator.getType())
```
# 移动式能量接口 (PEI)
在如下的例子中, 我们可以获得一个左侧 PEI 的外围设备接口。
```lua
local pei = peripheral.wrap("left")
```
函数 `getEnergy()` 会返回连接的可移动结构已经存储的能量（-1 如果没有连接）（单位：FE）。
```lua
local fe = pei.getEnergy()
```
函数 `getCapacity()` 会返回连接的可移动结构的总容量（-1 如果没有连接）（单位：FE）。
```lua
local fe = pei.getCapacity()
```
函数 `isConnected()` 会在有可移动结构连接时返回 true 。
```lua
local connected = pei.isConnected()
```
函数 `getMaxInsert()` 会返回 PEI 的最大输入（单位：FE）。
```lua
local fe = accumulator.getMaxInsert()
```
函数 `getMaxExtract()` 会返回 PEI 的最大输出（单位：FE）。
```lua
local fe = accumulator.getMaxExtract()
```
函数 `getType()` 将返回 PEI 的设备名称，该名称将始终为 "portable_energy_interface".
```lua
print("Peripheral: " .. pei.getType())
```
# 红石继电器
在如下的例子中, 我们可以获得一个左侧红石继电器的外围设备接口。
```lua
local relay = peripheral.wrap("left")
```
函数 `getMaxInsert()` 会返回继电器的最大输入（单位：FE）。
```lua
local fe = relay.getMaxInsert()
```
函数 `getMaxExtract()` 会返回继电器的最大输出（单位：FE）。
```lua
local fe = relay.getMaxExtract()
```
函数 `getThroughput()` 会返回继电器当前的流量（单位：FE）。
```lua
local fe = relay.getThroughput()
```
函数 `isPowered()` 会返回继电器的红石状态。
```lua
local powered = relay.isPowered()
```
函数 `getType()` 将返回红石继电器的外围设备名称，该名称将始终为 "redstone_relay".
```lua
print("Peripheral: " .. relay.getType())
```
函数 `getType()` 将返回数字适配器的外围设备名称，该名称将始终为 "digital_adapter"。
```lua
print("Peripheral: " .. da.getType())
```
### Rotation Speed Controller
函数 `setTargetSpeed(side, speed)` 可以设置数字适配器的 *side* 面上的 转速控制器`(create:rotation_speed_conroller)` 的目标转速到 *speed*。
```lua
setTargetSpeed("top", 64)
```
函数 `getTargetSpeed(side, speed)` 可以获得数字适配器的 *side* 面上的 转速控制器 的目标转速到 *speed*。
```lua
local speed = da.getTargetSpeed("top")
```
### 应力表
函数 `getKineticStress(side)` 可以获得数字适配器的 *side* 面上的 应力表`(create:stressometer)` 的当前应力。
```lua
local stress = da.getKineticStress("up")
```
函数 `getKineticCapacity(side)` 可以获得数字适配器的 *side* 面上的 应力表 的应力最大值。
```lua
local capacity = da.getKineticCapacity("up")
```
### 速度表
函数 `getKineticSpeed(side)`  可以获得数字适配器的 *side* 面上的 速度表`(create:speedometer)` 的转速。
```lua
local speed = da.getKineticSpeed("up")
```
### 绳索滑轮
函数 `getPulleyDistance(side)` 可以获得连接到数字适配器的 *side* 面的绳索滑轮的伸出长度。
```lua
local blocks = da.getPulleyDistance("south")
```
### 动力活塞
函数 `getPistonDistance(side)` 可以获得连接到数字适配器的 *side* 面的动力活塞的伸出长度。
```lua
local blocks = da.getPistonDistance("east")
```
### 动力轴承
函数 `getBearingAngle(side)` 可以获得连接到数字适配器的 *side* 面的动力轴承的角度。
```lua
local degrees = da.getBearingAngle("west")
```
### 显示链接器
函数 `print(text)` 将当前所选行上的字符串打印到内部缓冲区，该缓冲区可由 显示链接器`(create:display_link)` 读取并显示到 翻牌显示器`(create:display_board)` 上，打印将在当前所选行追加。

>_此段不保证完全准确，因为我无法准确翻译最后一句。请以英文文档为准。_
```lua
print("Hello World!")
```
函数 `clearLine()` 将清除所选行的文本。

函数 `clear()` 将清除所有行的所有文本。

函数 `getLine()` 会返回当前选中的行（从 1 开始）。

函数 `setLine(line)` 会设置当前选中的行到 *line*（从 1 开始）。
```lua
da.print("Text on first line")
da.print("Text on second line")
da.setLine(1)
da.print("Text on first line again")
```
函数 `getMaxLines()` 将返回使用数字适配器可以显示的最大行数（将始终返回16）。

### Other

The function `getDurationDistance(blocks, rpm)` will return the time needed to push a Mechanical Piston, Pulley or Gantry a number of blocks at the given rpm.

The function `getDurationAngle(degrees, rpm)` will return the time needed to rotate a Mechanical Bearing by a number of degrees at the given rpm.
