package com.mrh0.createaddition.item;

import java.util.List;

import javax.annotation.Nullable;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.energy.IWireNode;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class Multimeter extends Item {

	public Multimeter(Properties props) {
		super(props);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext c) {
		TileEntity te = c.getWorld().getTileEntity(c.getPos());
		if(te != null && !c.getWorld().isRemote()) {
			LazyOptional<IEnergyStorage> cap;
			cap = te.getCapability(CapabilityEnergy.ENERGY, c.getFace());
			
			
			if(te instanceof IWireNode) {
				IWireNode wn = (IWireNode) te;
				for(int i = 0; i < wn.getNodeCount(); i++) {
					System.out.println(i + ":" + wn.getNodeEnergyStorage(i).toString());
				}
			}
			
			
			
			if(cap != null) {
				IEnergyStorage energy = cap.orElse(null);
				String measur = new TranslationTextComponent("item."+CreateAddition.MODID+".multimeter.measuring").getStringTruncated(Integer.MAX_VALUE);
				CompoundNBT tag = c.getItem().getTag();
				if(tag == null)
					tag = new CompoundNBT();
				if(hasPos(tag)) {
					if(posEquals(tag, c.getPos(), c.getFace())) {
						int de = getDeltaEnergy(tag, energy != null ? energy.getEnergyStored() : 0);
						long dt = getDeltaTime(tag, c.getWorld().getGameTime());
						measur = " ["+(dt > 0 ? de/dt : 0) + "fe/t ("+(dt)+(new TranslationTextComponent("item."+CreateAddition.MODID+".multimeter.ticks").getStringTruncated(Integer.MAX_VALUE))+")]";
						clearPos(tag);
					}
					else {
						setContent(tag, c.getPos(), c.getFace(), c.getWorld().getGameTime(), energy != null ? energy.getEnergyStored() : 0);
					}
				}
				else {
					setContent(tag, c.getPos(), c.getFace(), c.getWorld().getGameTime(), energy != null ? energy.getEnergyStored() : 0);
				}
				
				c.getItem().setTag(tag);
				
				c.getPlayer().sendMessage(new TranslationTextComponent("item."+CreateAddition.MODID+".multimeter.title")
						.append(new StringTextComponent(" " +getString(energy,
								new TranslationTextComponent("item."+CreateAddition.MODID+".multimeter.no_capability").getStringTruncated(Integer.MAX_VALUE), "fe") + " ").append(new StringTextComponent(measur))),
						PlayerEntity.getUUID(c.getPlayer().getGameProfile()));
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}
	
	public static String getString(IEnergyStorage ies, String nan, String unit) {
		if(ies == null)
			return nan;
		return format(ies.getEnergyStored()) + "/" + format(ies.getMaxEnergyStored()) + "fe";
	}
	
	public static String getString(IEnergyStorage ies) {
		return getString(ies, "NaN", "fe");
	}
	
	public static String format(int n) {
		if(n > 1000000)
			return Math.round((double)n/100000d)/10d + "M";
		if(n > 1000)
			return Math.round((double)n/100d)/10d + "K";
		return n + "";
	}
	
	@Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	CompoundNBT nbt = stack.getTag();
    	super.addInformation(stack, worldIn, tooltip, flagIn);
    	if(hasPos(nbt))
    		tooltip.add(new TranslationTextComponent("item."+CreateAddition.MODID+".multimeter.measuring"));
    }
	
	public static boolean hasPos(CompoundNBT nbt) {
		if(nbt == null)
			return false;
    	return nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.contains("side");
    }
	
	public static BlockPos getPos(CompoundNBT nbt){
		if(nbt == null)
			return null;
    	return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }
	
	public static int getDeltaEnergy(CompoundNBT nbt, int now){
		if(nbt == null)
			return 0;
		int r = now - nbt.getInt("start");
    	return r;
    }
	
	public static long getDeltaTime(CompoundNBT nbt, long now){
		if(nbt == null)
			return 0;
		long r = now - nbt.getLong("tick");
    	return r > 0 ? r : 0;
    }
	
	public static Direction getDirection(CompoundNBT nbt){
		if(nbt == null)
			return null;
    	return Direction.byIndex(nbt.getInt("side"));
    }
	
	public static boolean posEquals(CompoundNBT nbt, BlockPos pos, Direction dir){
    	return nbt.getInt("x") == pos.getX() && nbt.getInt("y") == pos.getY() && nbt.getInt("z") == pos.getZ() && nbt.getInt("side") == dir.getIndex();
    }
	
	public static void clearPos(CompoundNBT nbt){
    	nbt.remove("x");
    	nbt.remove("y");
    	nbt.remove("z");
    	nbt.remove("side");
    	nbt.remove("tick");
    	nbt.remove("start");
    }
	
	public static CompoundNBT setContent(CompoundNBT nbt, BlockPos pos, Direction dir, long tick, int energy){
		if(nbt == null)
			return new CompoundNBT();
    	nbt.putInt("x", pos.getX());
    	nbt.putInt("y", pos.getY());
    	nbt.putInt("z", pos.getZ());
    	nbt.putInt("side", dir.getIndex());
    	nbt.putLong("tick", tick);
    	nbt.putInt("start", energy);
    	return nbt;
    }
}
