package com.mrh0.createaddition.mixin;

import com.mrh0.createaddition.compat.simulated.DockEnergyStorage;
import com.mrh0.createaddition.compat.simulated.DockingConnectorBEAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mrh0.createaddition.config.CommonConfig;


@Mixin(targets = "dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity")
abstract public class DockingConnectorBEMixin implements DockingConnectorBEAccess {
    @Unique
    private DockEnergyStorage energyStorage;

    @Shadow
    public BlockPos otherConnectorPosition;



    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void onInit(BlockEntityType<?> type, BlockPos pos,
                        BlockState state, CallbackInfo ci) {
        energyStorage = new DockEnergyStorage(
                (BlockEntity)(Object)this,
                CommonConfig.DOCKING_CONNECTOR_CAPACITY.getAsInt()
        );
    }
    // Hook into setDock, fires alongside tank.connect()
    @Inject(method = "setDock", at = @At(
            value = "INVOKE",
            target = "Ldev/simulated_team/simulated/content/blocks/docking_connector/DockingConnectorTank;connect(Lnet/minecraft/core/BlockPos;Ldev/simulated_team/simulated/content/blocks/docking_connector/DockingConnectorTank;)V"
    ), remap = false)
    private void onConnect(CallbackInfo ci) {
        BlockPos otherPos = ((DockingConnectorBEAccess)(Object)this).getOtherConnectorPosition();
        if (otherPos == null) return;

        Level level = ((BlockEntity)(Object)this).getLevel();
        if (level == null) return;

        BlockEntity other = level.getBlockEntity(otherPos);
        if (other instanceof DockingConnectorBEAccess otherAccess) {
            this.energyStorage.connect(otherPos, otherAccess.getEnergyStorage());

        }
    }

    // Hook into unDock, fires alongside tank.disconnect()
    @Inject(
            method = "unDock",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/simulated_team/simulated/content/blocks/docking_connector/DockingConnectorTank;disconnect()V"
            ),remap = false
    )
    private void onDisconnect(CallbackInfo ci) {

        this.energyStorage.disconnect();
    }

    @Override
    public DockEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public BlockPos getOtherConnectorPosition() {
        return this.otherConnectorPosition;
    }

}
