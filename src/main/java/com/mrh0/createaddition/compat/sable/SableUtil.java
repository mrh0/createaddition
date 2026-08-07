package com.mrh0.createaddition.compat.sable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.LocalNode;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import com.mrh0.createaddition.blocks.connector.base.AbstractConnectorBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class SableUtil {
    // Wire break helpers

    private record WireBreak(int localIdx, IWireNode other, int otherIdx) {}

    private static void severConnection(IWireNode node, LocalNode local, IWireNode other, int otherIdx) {
        LocalNode otherLocal = other.getConnectionTo(node.getPos());
        node.removeNode(local, true);
        if (otherLocal != null) other.removeNode(otherLocal, false);
        else if (other.hasConnection(otherIdx)) other.removeNode(otherIdx, false);
    }

    public static void breakWires(Level level, IWireNode node) {
        breakWires(level, node, Map.of(), true);
    }

    public static void breakWires(Level level, IWireNode node,
            Map<BlockPos, IWireNode> cache, boolean searchSubLevels) {
        List<WireBreak> toBreak = null;

        for (int i = 0; i < node.getNodeCount(); i++) {
            LocalNode local = node.getLocalNode(i);
            if (local == null) continue;

            int otherIdx = local.getOtherIndex();
            if (otherIdx < 0) continue;

            IWireNode other = getLoadedWireNode(level, local.getPos(), cache);
            if (other == null && searchSubLevels) other = lookupWireNode(level, local.getPos());
            if (other == null) continue;

            int maxLen = Math.min(node.getMaxWireLength(), other.getMaxWireLength());
            double distSq = localNodeDistSq(level,
                    node.getPos(), node.getNodeOffset(i),
                    other.getPos(), other.getNodeOffset(otherIdx));
            if (distSq > (double) maxLen * maxLen) {
                if (toBreak == null) toBreak = new ArrayList<>();
                toBreak.add(new WireBreak(i, other, otherIdx));
            }
        }

        if (toBreak == null) return;

        for (WireBreak wb : toBreak) {
            LocalNode local = node.getLocalNode(wb.localIdx());
            if (local == null) continue;
            severConnection(node, local, wb.other(), wb.otherIdx());
            AbstractConnectorBlock.playWireBreakSound(level, node.getPos());
            AbstractConnectorBlock.playWireBreakSound(level, wb.other().getPos());
        }
    }

    // Node lookup

    @Nullable
    public static IWireNode lookupWireNode(Level level, BlockPos pos) {
        SubLevelAccess access = SableCompanion.INSTANCE.getContaining(level, pos);
        return SableCompanion.INSTANCE.runIncludingSubLevels(level, (Position) Vec3.atCenterOf(pos),
                true, access,
                (context, bp) -> {
                    BlockEntity be = context instanceof SubLevel sl
                            ? sl.getPlot().getEmbeddedLevelAccessor().getBlockEntity(bp)
                            : level.getBlockEntity(bp);
                    return be instanceof IWireNode wn ? wn : null;
                });
    }

    @Nullable
    private static IWireNode getLoadedWireNode(Level level, BlockPos pos, Map<BlockPos, IWireNode> cache) {
        IWireNode cached = cache.get(pos);
        if (cached != null) return cached;
        if (!(level instanceof ServerLevel svl) || !svl.isLoaded(pos)) return null;
        LevelChunk chunk = svl.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null) return null;
        BlockEntity be = chunk.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
        return (be instanceof IWireNode wn && !be.isRemoved()) ? wn : null;
    }

    public static double localNodeDistSq(Level level, BlockPos pos1, Vec3 off1, BlockPos pos2, Vec3 off2) {
        Position p2 = SableCompanion.INSTANCE.projectOutOfSubLevel(level, (Position) Vec3.atCenterOf(pos2).add(off2));
        Position p1 = SableCompanion.INSTANCE.projectOutOfSubLevel(level, (Position) Vec3.atCenterOf(pos1).add(off1));
        return SableCompanion.INSTANCE.distanceSquaredWithSubLevels(level, p1, p2);
    }

    // Render helpers

    public static boolean isInSubLevel(Level level, BlockPos pos) {
        return SableCompanion.INSTANCE.getContaining(level, pos) != null;
    }

    public static Vec3 localDown(Level level, BlockPos pos, float partialTick) {
        SubLevelAccess access = SableCompanion.INSTANCE.getContaining(level, pos);
        if (!(access instanceof ClientSubLevelAccess csa)) return new Vec3(0, -1, 0);
        Vec3 anchor = Vec3.atCenterOf(pos);
        Vec3 worldAnchor = csa.renderPose(partialTick).transformPosition(anchor);
        return csa.renderPose(partialTick)
                  .transformPositionInverse(worldAnchor.add(0, -1, 0))
                  .subtract(anchor)
                  .normalize();
    }

    public static Vec3 nodeWireOffsetRelativeTo(Level level, BlockPos pos,
            BlockPos remotePos, Vec3 remoteOffset, float partialTick) {
        return transformRelativeTo(level, pos, Vec3.atCenterOf(remotePos).add(remoteOffset), partialTick);
    }

    public static Vec3 transformRelativeTo(Level level, BlockPos pos, Vec3 vec, float partialTick) {
        SubLevelAccess originAccess = SableCompanion.INSTANCE.getContaining(level, pos);
        SubLevelAccess vecAccess = SableCompanion.INSTANCE.getContaining(level, (Position) vec);
        Vec3 worldVec = vecAccess instanceof ClientSubLevelAccess csa ? csa.renderPose(partialTick).transformPosition(vec) : vec;
        if (originAccess instanceof ClientSubLevelAccess csa) return csa.renderPose(partialTick).transformPositionInverse(worldVec);
        return worldVec;
    }
}
