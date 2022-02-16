package com.mrh0.createaddition.item;

import java.util.List;

import javax.annotation.Nullable;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.energy.IWireNode;

import com.simibubi.create.lib.util.LazyOptional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

public class Multimeter extends Item {

	public Multimeter(Properties props) {
		super(props);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext c) {
		BlockEntity te = c.getLevel().getBlockEntity(c.getClickedPos());
		if(te != null && !c.getLevel().isClientSide()) {
			LazyOptional<EnergyStorage> cap;
			cap = LazyOptional.ofObject(EnergyStorage.SIDED.find(c.getLevel(), c.getClickedPos(), c.getClickedFace()));
			
			if(te instanceof IWireNode) {
				IWireNode wn = (IWireNode) te;
				for(int i = 0; i < wn.getNodeCount(); i++)
				System.out.println(wn.getNetwork(i));//wn.getNodeFromPos(c.getHitVec())
			}
			
			if(cap != null) {
				EnergyStorage energy = cap.orElse(null);
				String measur = new TranslatableComponent("item."+CreateAddition.MODID+".multimeter.measuring").getString(Integer.MAX_VALUE);
				CompoundTag tag = c.getItemInHand().getTag();
				if(tag == null)
					tag = new CompoundTag();
				if(hasPos(tag)) {
					if(posEquals(tag, c.getClickedPos(), c.getClickedFace())) {
						long de = getDeltaEnergy(tag, energy != null ? energy.getAmount() : 0);
						long dt = getDeltaTime(tag, c.getLevel().getGameTime());
						measur = " ["+(dt > 0 ? de/dt : 0) + "fe/t ("+(dt)+(new TranslatableComponent("item."+CreateAddition.MODID+".multimeter.ticks").getString(Integer.MAX_VALUE))+")]";
						clearPos(tag);
					}
					else {
						setContent(tag, c.getClickedPos(), c.getClickedFace(), c.getLevel().getGameTime(), energy != null ? energy.getAmount() : 0);
					}
				}
				else {
					setContent(tag, c.getClickedPos(), c.getClickedFace(), c.getLevel().getGameTime(), energy != null ? energy.getAmount() : 0);
				}
				
				c.getItemInHand().setTag(tag);
				
				c.getPlayer().sendMessage(new TranslatableComponent("item."+CreateAddition.MODID+".multimeter.title")
						.append(new TextComponent(" ").append(getTextComponent(energy,
								new TranslatableComponent("item."+CreateAddition.MODID+".multimeter.no_capability").getString(Integer.MAX_VALUE), "fe")).append(new TextComponent(" " + measur))),
						Player.createPlayerUUID(c.getPlayer().getGameProfile()));
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}
	
	public static Component getTextComponent(EnergyStorage ies, String nan, String unit) {
		if(ies == null)
			return new TextComponent(nan);
		return new TextComponent(format(ies.getAmount())+unit).withStyle(ChatFormatting.AQUA).append(new TextComponent(" / ").withStyle(ChatFormatting.GRAY)).append(new TextComponent(format(ies.getCapacity())+unit));
	}
	
	public static Component getTextComponent(EnergyStorage ies) {
		return getTextComponent(ies, "NaN", "fe");
	}
	
	public static String format(long n) {
		if(n > 1000000)
			return Math.round((double)n/100000d)/10d + "M";
		if(n > 1000)
			return Math.round((double)n/100d)/10d + "K";
		return n + "";
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		CompoundTag nbt = stack.getTag();
    	super.appendHoverText(stack, worldIn, tooltip, flagIn);
    	if(hasPos(nbt))
    		tooltip.add(new TranslatableComponent("item."+CreateAddition.MODID+".multimeter.measuring"));
	}
	
	
	public static boolean hasPos(CompoundTag nbt) {
		if(nbt == null)
			return false;
    	return nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.contains("side");
    }
	
	public static BlockPos getPos(CompoundTag nbt){
		if(nbt == null)
			return null;
    	return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }
	
	public static long getDeltaEnergy(CompoundTag nbt, long now){
		if(nbt == null)
			return 0;
		long r = now - nbt.getLong("start");
    	return r;
    }
	
	public static long getDeltaTime(CompoundTag nbt, long now){
		if(nbt == null)
			return 0;
		long r = now - nbt.getLong("tick");
    	return r > 0 ? r : 0;
    }
	
	public static Direction getDirection(CompoundTag nbt){
		if(nbt == null)
			return null;
    	return Direction.from3DDataValue(nbt.getInt("side"));
    }
	
	public static boolean posEquals(CompoundTag nbt, BlockPos pos, Direction dir){
    	return nbt.getInt("x") == pos.getX() && nbt.getInt("y") == pos.getY() && nbt.getInt("z") == pos.getZ() && nbt.getInt("side") == dir.get3DDataValue();
    }
	
	public static void clearPos(CompoundTag nbt){
    	nbt.remove("x");
    	nbt.remove("y");
    	nbt.remove("z");
    	nbt.remove("side");
    	nbt.remove("tick");
    	nbt.remove("start");
    }
	
	public static CompoundTag setContent(CompoundTag nbt, BlockPos pos, Direction dir, long tick, long energy){
		if(nbt == null)
			return new CompoundTag();
    	nbt.putInt("x", pos.getX());
    	nbt.putInt("y", pos.getY());
    	nbt.putInt("z", pos.getZ());
    	nbt.putInt("side", dir.get3DDataValue());
    	nbt.putLong("tick", tick);
    	nbt.putLong("start", energy);
    	return nbt;
    }
}
