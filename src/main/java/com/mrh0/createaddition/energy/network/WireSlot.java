package com.mrh0.createaddition.energy.network;

import com.mrh0.createaddition.energy.WireType;

record WireSlot(int index, long otherPos, int otherIndex, WireType type) {}
