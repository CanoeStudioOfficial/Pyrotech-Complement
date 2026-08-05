package com.canoestudios.pyrotechcomplement.tile;

import com.canoestudios.pyrotechcomplement.block.BlockQuern;
import com.canoestudios.pyrotechcomplement.init.ModBlocks;
import com.canoestudios.pyrotechcomplement.recipe.QuernRecipe;
import com.codetaylor.mc.athenaeum.spi.TileEntityBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileQuern extends TileEntityBase implements ITickable {

  public static final int SLOT_HANDSTONE = 0;
  public static final int SLOT_INPUT = 1;
  public static final int SLOT_OUTPUT = 2;

  private final ItemStackHandler inventory = new ItemStackHandler(3) {
    @Override
    protected void onContentsChanged(int slot) {

      TileQuern.this.updateCachedRecipe();
      TileQuern.this.updateHandstoneState();
      TileQuern.this.markDirty();
      TileQuern.this.notifyBlockUpdate();
    }

    @Override
    public int getSlotLimit(int slot) {

      return slot == SLOT_HANDSTONE ? 1 : 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {

      if (slot == SLOT_HANDSTONE) {
        return stack.getItem() == ModBlocks.HANDSTONE;
      }
      if (slot == SLOT_OUTPUT) {
        return true;
      }
      return slot == SLOT_INPUT && TileQuern.this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()
          && QuernRecipe.getRecipe(stack) != null;
    }
  };

  @Nullable
  private QuernRecipe recipe;
  private int progress;
  private boolean grinding;
  private long grindStartTime;

  public boolean onRightClick(EntityPlayer player, EnumHand hand, float hitX, float hitY, float hitZ) {

    if (this.world == null) {
      return false;
    }

    ItemStack held = player.getHeldItem(hand);
    boolean center = hitX >= 0.35f && hitX <= 0.65f && hitZ >= 0.35f && hitZ <= 0.65f && hitY >= 0.75f;
    boolean handle = hitY >= 0.82f && !center;

    if (this.grinding) {
      return true;
    }

    if (held.getItem() == ModBlocks.HANDSTONE && !this.hasHandstone()) {
      if (!this.world.isRemote) {
        ItemStack installed = held.splitStack(1);
        this.inventory.setStackInSlot(SLOT_HANDSTONE, installed);
        this.updateHandstoneState();
      }
      return true;
    }

    if (player.isSneaking() && held.isEmpty()) {
      if (this.removeStack(player, SLOT_HANDSTONE)) {
        this.removeStack(player, SLOT_INPUT);
        return true;
      }
      return this.removeStack(player, SLOT_INPUT);
    }

    if (handle && held.isEmpty()) {
      return this.startGrinding();
    }

    if (center) {
      if (held.isEmpty()) {
        return this.removeStack(player, SLOT_INPUT);
      }
      if (this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty() && QuernRecipe.getRecipe(held) != null) {
        if (!this.world.isRemote) {
          ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
          if (input.isEmpty()) {
            this.inventory.setStackInSlot(SLOT_INPUT, held.splitStack(1));
          } else if (input.isItemEqual(held) && ItemStack.areItemStackTagsEqual(input, held)
              && input.getCount() < 64) {
            held.shrink(1);
            input.grow(1);
            this.inventory.setStackInSlot(SLOT_INPUT, input);
          } else {
            return false;
          }
        }
        return true;
      }
    }

    if (held.isEmpty()) {
      return this.removeStack(player, SLOT_OUTPUT);
    }

    return false;
  }

  @Override
  public void update() {

    if (!this.grinding) {
      return;
    }

    this.progress++;
    if (this.recipe != null && this.progress >= this.recipe.getGrindingTicks()) {
      this.finishGrinding();
    }
  }

  private boolean startGrinding() {

    if (this.world == null || this.hasHandstone() == false || this.inventory.getStackInSlot(SLOT_INPUT).isEmpty()) {
      return false;
    }

    this.recipe = QuernRecipe.getRecipe(this.inventory.getStackInSlot(SLOT_INPUT));
    if (this.recipe == null) {
      return false;
    }

    this.progress = 0;
    this.grinding = true;
    this.grindStartTime = this.world.getTotalWorldTime();
    if (!this.world.isRemote) {
      this.world.playSound(null, this.pos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 0.8f, 0.8f);
      this.markDirty();
      this.notifyBlockUpdate();
    }
    return true;
  }

  private void finishGrinding() {

    this.grinding = false;
    this.progress = 0;

    if (this.world == null || this.world.isRemote) {
      return;
    }

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    if (this.recipe != null && !input.isEmpty() && this.recipe.matches(input)) {
      ItemStack remaining = this.inventory.insertItem(SLOT_OUTPUT, this.recipe.getOutput(), false);
      if (!remaining.isEmpty()) {
        this.spawnStack(remaining);
      }
      input.shrink(1);
      this.inventory.setStackInSlot(SLOT_INPUT, input);

      ItemStack handstone = this.inventory.getStackInSlot(SLOT_HANDSTONE);
      if (!handstone.isEmpty()) {
        int damage = handstone.getItemDamage() + 1;
        if (damage >= handstone.getMaxDamage()) {
          this.inventory.setStackInSlot(SLOT_HANDSTONE, ItemStack.EMPTY);
          this.world.playSound(null, this.pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 0.8f, 1.0f);
        } else {
          handstone.setItemDamage(damage);
          this.inventory.setStackInSlot(SLOT_HANDSTONE, handstone);
        }
      }
    }

    this.updateCachedRecipe();
    this.markDirty();
    this.notifyBlockUpdate();
  }

  private boolean removeStack(EntityPlayer player, int slot) {

    ItemStack stack = this.inventory.getStackInSlot(slot);
    if (stack.isEmpty()) {
      return false;
    }

    if (!this.world.isRemote) {
      ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
      this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
      if (slot == SLOT_INPUT) {
        this.progress = 0;
        this.recipe = null;
      }
      this.updateHandstoneState();
    }
    return true;
  }

  public void dropContents() {

    if (this.world == null || this.world.isRemote) {
      return;
    }

    for (int slot = 0; slot < this.inventory.getSlots(); slot++) {
      ItemStack stack = this.inventory.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        this.spawnStack(stack.copy());
      }
    }
  }

  private void spawnStack(ItemStack stack) {

    EntityItem entityItem = new EntityItem(this.world, this.pos.getX() + 0.5, this.pos.getY() + 0.8,
        this.pos.getZ() + 0.5, stack);
    this.world.spawnEntity(entityItem);
  }

  private void updateCachedRecipe() {

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    this.recipe = input.isEmpty() ? null : QuernRecipe.getRecipe(input);
    if (this.recipe == null && this.grinding) {
      this.grinding = false;
      this.progress = 0;
    }
  }

  private void updateHandstoneState() {

    if (this.world == null || this.world.isRemote || !(this.world.getBlockState(this.pos).getBlock() instanceof BlockQuern)) {
      return;
    }

    boolean hasHandstone = this.hasHandstone();
    if (this.world.getBlockState(this.pos).getValue(BlockQuern.HAS_HANDSTONE) != hasHandstone) {
      this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(BlockQuern.HAS_HANDSTONE, hasHandstone), 3);
    }
  }

  public boolean hasHandstone() {

    return !this.inventory.getStackInSlot(SLOT_HANDSTONE).isEmpty();
  }

  public boolean isGrinding() {

    return this.grinding;
  }

  public int getProgress() {

    return this.progress;
  }

  public int getGrindingTicks() {

    return this.recipe == null ? QuernRecipe.DEFAULT_GRINDING_TICKS : this.recipe.getGrindingTicks();
  }

  public float getRotationAngle(float partialTicks) {

    if (!this.grinding) {
      return 0.0f;
    }
    return -(this.progress + partialTicks) * 4.0f;
  }

  public ItemStack getHandstone() {

    return this.inventory.getStackInSlot(SLOT_HANDSTONE);
  }

  public ItemStack getInput() {

    return this.inventory.getStackInSlot(SLOT_INPUT);
  }

  public ItemStack getOutput() {

    return this.inventory.getStackInSlot(SLOT_OUTPUT);
  }

  @Nullable
  public QuernRecipe getRecipe() {

    return this.recipe;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {

    super.writeToNBT(compound);
    compound.setTag("inventory", this.inventory.serializeNBT());
    compound.setInteger("progress", this.progress);
    compound.setBoolean("grinding", this.grinding);
    compound.setLong("grindStartTime", this.grindStartTime);
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {

    super.readFromNBT(compound);
    this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    this.progress = compound.getInteger("progress");
    this.grinding = compound.getBoolean("grinding");
    this.grindStartTime = compound.getLong("grindStartTime");
    this.updateCachedRecipe();
  }

  @Override
  public NBTTagCompound getUpdateTag() {

    return this.writeToNBT(new NBTTagCompound());
  }

  @Override
  public void handleUpdateTag(NBTTagCompound tag) {

    this.readFromNBT(tag);
  }

  @Override
  public net.minecraft.network.play.server.SPacketUpdateTileEntity getUpdatePacket() {

    return new net.minecraft.network.play.server.SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
  }

  @Override
  public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity packet) {

    this.handleUpdateTag(packet.getNbtCompound());
  }
}
