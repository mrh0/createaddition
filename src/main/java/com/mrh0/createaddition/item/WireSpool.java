package com.mrh0.createaddition.item;

import java.util.List;

import javax.annotation.Nullable;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireConnectResult;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.index.CAItems;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class WireSpool extends Item {

	public WireSpool(Properties props) {
		super(props);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext c) {
		//if(c.getWorld().isRemote())
		//	return ActionResultType.PASS;
		
		CompoundNBT nbt = c.getItem().getTag();
		if(nbt == null)
			nbt = new CompoundNBT();
		
		TileEntity te = c.getWorld().getTileEntity(c.getPos());
		if(te == null)
			return ActionResultType.PASS;
		if(!(te instanceof IWireNode))
			return ActionResultType.PASS;
		IWireNode node = (IWireNode) te;
		
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
			
			WireType connectionType = IWireNode.getTypeOfConnection(c.getWorld(), c.getPos(), getPos(nbt));
			
			if(isRemover(c.getItem().getItem()))
				result = IWireNode.disconnect(c.getWorld(), c.getPos(), getPos(nbt));
			else
				result = IWireNode.connect(c.getWorld(), getPos(nbt), getNode(nbt), c.getPos(), node.getNodeFromPos(c.getHitVec()), getWireType(c.getItem().getItem()));

			te.markDirty();
			
			if(!c.getPlayer().isCreative()) {
				if(result == WireConnectResult.REMOVED) {
					c.getItem().shrink(1);
					ItemStack stack = connectionType.getSourceDrop();
					boolean shouldDrop = !c.getPlayer().addItemStackToInventory(stack);
					if(shouldDrop)
						c.getPlayer().dropItem(stack, false);
				}
				else if(result.isLinked()) {
					c.getItem().shrink(1);
					ItemStack stack = new ItemStack(CAItems.SPOOL.get(), 1);
					boolean shouldDrop = !c.getPlayer().addItemStackToInventory(stack);
					if(shouldDrop)
						c.getPlayer().dropItem(stack, false);
				}
			}
			c.getItem().setTag(null);
			c.getPlayer().sendStatusMessage(result.getMessage(), true);
			
		}
		else {
			int index = node.getNodeFromPos(c.getHitVec());
			if(index < 0)
				return ActionResultType.PASS;
			if(!isRemover(c.getItem().getItem()))
				c.getPlayer().sendStatusMessage(WireConnectResult.getConnect(node.isNodeInput(index), node.isNodeOutput(index)).getMessage(), true);
			c.getItem().setTag(null);
			c.getItem().setTag(setContent(nbt, node.getMyPos(), index));
		}
		
		return ActionResultType.CONSUME;
	}
	
	public static boolean hasPos(CompoundNBT nbt) {
		if(nbt == null)
			return false;
    	return nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.contains("node");
    }
	
	public static BlockPos getPos(CompoundNBT nbt){
		if(nbt == null)
			return null;
    	return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }
	
	public static int getNode(CompoundNBT nbt){
		if(nbt == null)
			return -1;
    	return nbt.getInt("node");
    }
	
	public static CompoundNBT setContent(CompoundNBT nbt, BlockPos pos, int node){
		if(nbt == null)
			return new CompoundNBT();
    	nbt.putInt("x", pos.getX());
    	nbt.putInt("y", pos.getY());
    	nbt.putInt("z", pos.getZ());
    	nbt.putInt("node", node);
    	return nbt;
    }
	
	@Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	CompoundNBT nbt = stack.getTag();
    	super.addInformation(stack, worldIn, tooltip, flagIn);
    	if(hasPos(nbt))
    		tooltip.add(new TranslationTextComponent("item."+CreateAddition.MODID+".spool.nbt"));
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
