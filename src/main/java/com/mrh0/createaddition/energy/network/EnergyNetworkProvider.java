package com.mrh0.createaddition.energy.network;

import java.util.Optional;

public interface EnergyNetworkProvider {
	Optional<EnergyNetwork> getNetwork();
}
