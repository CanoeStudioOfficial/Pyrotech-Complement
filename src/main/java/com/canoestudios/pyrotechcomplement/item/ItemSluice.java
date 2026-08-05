package com.canoestudios.pyrotechcomplement.item;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSluice extends ItemBlock {

  public ItemSluice(Block block) {

    super(block);
  }

  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
      EnumFacing facing, float hitX, float hitY, float hitZ) {

    IBlockState clickedState = world.getBlockState(pos);
    if (!clickedState.getBlock().isReplaceable(world, pos)) {
      pos = pos.offset(facing);
    }

    ItemStack stack = player.getHeldItem(hand);
    if (stack.isEmpty() || !player.canPlayerEdit(pos, facing, stack)
        || !world.mayPlace(this.block, pos, false, facing, player)) {
      return EnumActionResult.FAIL;
    }

    IBlockState placementState = this.block.getStateForPlacement(
        world, pos, facing, hitX, hitY, hitZ, this.getMetadata(stack.getMetadata()), player, hand);

    // ItemBlock in 1.12.2 passes a null state to World#setBlockState, which
    // crashes instead of treating an invalid multi-block placement as a miss.
    if (placementState == null) {
      return EnumActionResult.FAIL;
    }

    if (this.placeBlockAt(stack, player, world, pos, facing, hitX, hitY, hitZ, placementState)) {
      IBlockState placedState = world.getBlockState(pos);
      SoundType sound = placedState.getBlock().getSoundType(placedState, world, pos, player);
      world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS,
          (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

      if (player instanceof EntityPlayerMP) {
        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
      }

      stack.shrink(1);
    }

    return EnumActionResult.SUCCESS;
  }
}
