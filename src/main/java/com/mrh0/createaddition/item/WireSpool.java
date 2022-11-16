package com.mrh0.createaddition.item;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireConnectResult;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.index.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("CommentedOutCode")
public class WireSpool extends Item {

	public WireSpool(Properties props) {
		super(props);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext c) {
		//if(c.getWorld().isRemote())
		//	return ActionResultType.PASS;
		
		CompoundTag nbt = c.getItemInHand().getTag();
		if(nbt == null)
			nbt = new CompoundTag();
		
		BlockEntity te = c.getLevel().getBlockEntity(c.getClickedPos());
		if(te == null)
			return InteractionResult.PASS;
		if(!(te instanceof IWireNode node))
			return InteractionResult.PASS;

		/*if(c.getPlayer().isSneaking()) {
			for(int i = 0; i < node.getNodeCount(); i++) {
				int index = node.getNodeIndex(i);
				if(index != -1)
					System.out.println(i+"->"+node.isNodeInput(i));
			}
			return ActionResultType.CONSUME;
		}*/

		if(hasPos(nbt)) {
			WireConnectResult result;
			
			WireType connectionType = IWireNode.getTypeOfConnection(c.getLevel(), c.getClickedPos(), getPos(nbt));
			
			if(isRemover(c.getItemInHand().getItem()))
				result = IWireNode.disconnect(c.getLevel(), c.getClickedPos(), getPos(nbt));
			else
				result = IWireNode.connect(c.getLevel(), getPos(nbt), getNode(nbt), c.getClickedPos(), node.getNodeFromPos(c.getClickLocation()), getWireType(c.getItemInHand().getItem()));

			te.setChanged();
			
			if(!Objects.requireNonNull(c.getPlayer()).isCreative()) {
				if(result == WireConnectResult.REMOVED) {
					c.getItemInHand().shrink(1);
					assert connectionType != null;
					ItemStack stack = connectionType.getSourceDrop();
					boolean shouldDrop = !c.getPlayer().addItem(stack);
					if(shouldDrop)
						c.getPlayer().drop(stack, false);
				}
				else if(result.isLinked()) {
					c.getItemInHand().shrink(1);
					ItemStack stack = new ItemStack(CAItems.SPOOL.get(), 1);
					boolean shouldDrop = !c.getPlayer().addItem(stack);
					if(shouldDrop)
						c.getPlayer().drop(stack, false);
				}
			}
			c.getItemInHand().setTag(null);
			c.getPlayer().displayClientMessage(result.getMessage(), true);
			
		}
		else {
			int index = node.getNodeFromPos(c.getClickLocation());
			if(index < 0)
				return InteractionResult.PASS;
			if(!isRemover(c.getItemInHand().getItem()))
				Objects.requireNonNull(c.getPlayer()).displayClientMessage(WireConnectResult.getConnect(node.isNodeInput(index), node.isNodeOutput(index)).getMessage(), true);
			c.getItemInHand().setTag(null);
			c.getItemInHand().setTag(setContent(nbt, node.getMyPos(), index));
		}
		
		return InteractionResult.CONSUME;
	}
	
	public static boolean hasPos(CompoundTag nbt) {
		if(nbt == null)
			return false;
    	return nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.contains("node");
    }
	
	public static BlockPos getPos(CompoundTag nbt){
		if(nbt == null)
			return null;
    	return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }
	
	public static int getNode(CompoundTag nbt){
		if(nbt == null)
			return -1;
    	return nbt.getInt("node");
    }
	
	public static CompoundTag setContent(CompoundTag nbt, BlockPos pos, int node){
		if(nbt == null)
			return new CompoundTag();
    	nbt.putInt("x", pos.getX());
    	nbt.putInt("y", pos.getY());
    	nbt.putInt("z", pos.getZ());
    	nbt.putInt("node", node);
    	return nbt;
    }
	
	@Override
    public void appendHoverText(ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
		CompoundTag nbt = stack.getTag();
    	super.appendHoverText(stack, worldIn, tooltip, flagIn);
    	if(hasPos(nbt))
    		tooltip.add(Component.translatable("item."+CreateAddition.MODID+".spool.nbt"));
    }
	
	public static WireType getWireType(Item item) {
		if(item == CAItems.COPPER_SPOOL.get())
			return WireType.COPPER;
		if(item == CAItems.GOLD_SPOOL.get())
			return WireType.GOLD;
		return WireType.COPPER;
	}
	
	public static boolean isRemover(Item item) {
		return item == CAItems.SPOOL.get();
	}
}
