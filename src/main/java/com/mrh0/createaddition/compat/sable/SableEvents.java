package com.mrh0.createaddition.compat.sable;

import com.mrh0.createaddition.energy.IWireNode;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.neoforge.event.ForgeSablePostPhysicsTickEvent;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class SableEvents {
    private SableEvents() {}

    private static final int TICK_STEP = 10;
    private static final Map<ServerLevel, long[]> LAST_TICK = new WeakHashMap<>();

    private static Map<BlockPos, IWireNode> collectWireNodes(ServerSubLevelContainer container) {
        Map<BlockPos, IWireNode> result = new HashMap<>();
        for (SubLevel subLevel : container.getAllSubLevels()) {
            if (!(subLevel instanceof ServerSubLevel ssl) || ssl.isRemoved()) continue;
            for (PlotChunkHolder holder : ssl.getPlot().getLoadedChunks()) {
                LevelChunk chunk = holder.getChunk();
                if (chunk == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof IWireNode wn && !be.isRemoved())
                        result.put(be.getBlockPos().immutable(), wn);
                }
            }
        }
        return result;
    }

    static void onPostPhysicsTick(ForgeSablePostPhysicsTickEvent event) {
        ServerLevel level = event.getPhysicsSystem().getLevel();
        long tick = level.getGameTime();
        if (tick % TICK_STEP != 0) return;

        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null || container.getLoadedCount() == 0) return;

        long[] lastChecked = LAST_TICK.computeIfAbsent(level, k -> new long[]{-1});
        if (lastChecked[0] == tick) return;
        lastChecked[0] = tick;

        Map<BlockPos, IWireNode> wireNodes = collectWireNodes(container);
        if (wireNodes.isEmpty()) return;

        for (IWireNode node : new ArrayList<>(wireNodes.values())) {
            if (!(node instanceof BlockEntity be) || be.isRemoved()) continue;
            SableUtil.breakWires(level, node, wireNodes, false);
        }
    }
}
