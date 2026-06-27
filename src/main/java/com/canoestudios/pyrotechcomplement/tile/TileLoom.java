package com.canoestudios.pyrotechcomplement.tile;

import com.canoestudios.pyrotechcomplement.init.ModSounds;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import com.codetaylor.mc.athenaeum.spi.TileEntityBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileLoom
    extends TileEntityBase {

  private static final int SLOT_INPUT = 0;
  private static final int SLOT_OUTPUT = 1;

  private final ItemStackHandler inventory = new ItemStackHandler(2) {
    @Override
    protected void onContentsChanged(int slot) {

      TileLoom.this.updateCachedRecipe();
      TileLoom.this.notifyBlockUpdate();
    }

    @Override
    public int getSlotLimit(int slot) {

      if (slot == SLOT_INPUT && TileLoom.this.recipe != null) {
        return TileLoom.this.recipe.getInputCount();
      }
      return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {

      return slot == SLOT_INPUT && TileLoom.this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty() && LoomRecipe.getRecipe(stack) != null;
    }
  };

  @Nullable
  private LoomRecipe recipe;
  private int progress;
  private long lastPushed;

  public boolean onRightClick(EntityPlayer player, EnumHand hand) {

    ItemStack heldItem = player.getHeldItem(hand);

    if (player.isSneaking()) {
      return this.removeInput(player);
    }

    ItemStack output = this.inventory.getStackInSlot(SLOT_OUTPUT);
    if (!output.isEmpty()) {
      if (!this.world.isRemote) {
        ItemHandlerHelper.giveItemToPlayer(player, output.copy());
        this.inventory.setStackInSlot(SLOT_OUTPUT, ItemStack.EMPTY);
        this.clearProgress();
      }
      return true;
    }

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);

    if (input.isEmpty()) {
      LoomRecipe matchingRecipe = LoomRecipe.getRecipe(heldItem);
      if (matchingRecipe != null) {
        if (!this.world.isRemote) {
          ItemStack inserted = heldItem.splitStack(1);
          this.inventory.setStackInSlot(SLOT_INPUT, inserted);
          this.recipe = matchingRecipe;
        }
        return true;
      }
      return false;
    }

    if (!heldItem.isEmpty()
        && this.recipe != null
        && this.recipe.matches(heldItem)
        && input.isItemEqual(heldItem)
        && ItemStack.areItemStackTagsEqual(input, heldItem)
        && input.getCount() < this.recipe.getInputCount()) {
      if (!this.world.isRemote) {
        heldItem.shrink(1);
        input.grow(1);
        this.inventory.setStackInSlot(SLOT_INPUT, input);
      }
      return true;
    }

    if (heldItem.isEmpty()
        && this.recipe != null
        && input.getCount() >= this.recipe.getInputCount()
        && this.progress < this.recipe.getSteps()) {
      long now = this.world.getTotalWorldTime();
      if (now - this.lastPushed < 20) {
        return false;
      }
      if (!this.world.isRemote) {
        this.lastPushed = now;
        this.progress++;
        this.world.playSound(null, this.pos, ModSounds.LOOM_WEAVE, SoundCategory.BLOCKS, 1.0f, 0.95f + this.world.rand.nextFloat() * 0.1f);
        if (this.progress >= this.recipe.getSteps()) {
          ItemStack result = this.recipe.getOutput();
          this.inventory.setStackInSlot(SLOT_INPUT, ItemStack.EMPTY);
          this.inventory.setStackInSlot(SLOT_OUTPUT, result);
        } else {
          this.notifyBlockUpdate();
        }
      }
      return true;
    }

    return false;
  }

  private boolean removeInput(EntityPlayer player) {

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);

    if (input.isEmpty()) {
      return false;
    }

    if (!this.world.isRemote) {
      ItemHandlerHelper.giveItemToPlayer(player, input.copy());
      this.inventory.setStackInSlot(SLOT_INPUT, ItemStack.EMPTY);
      this.clearProgress();
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
        EntityItem entityItem = new EntityItem(
            this.world,
            this.pos.getX() + 0.5,
            this.pos.getY() + 0.5,
            this.pos.getZ() + 0.5,
            stack.copy()
        );
        this.world.spawnEntity(entityItem);
      }
    }
  }

  @Nullable
  public LoomRecipe getRecipe() {

    return this.recipe;
  }

  public int getProgress() {

    return this.progress;
  }

  public int getInputCount() {

    return this.inventory.getStackInSlot(SLOT_INPUT).getCount();
  }

  public ItemStack getInput() {

    return this.inventory.getStackInSlot(SLOT_INPUT);
  }

  public ItemStack getOutput() {

    return this.inventory.getStackInSlot(SLOT_OUTPUT);
  }

  private void clearProgress() {

    this.progress = 0;
    this.recipe = null;
    this.notifyBlockUpdate();
  }

  private void updateCachedRecipe() {

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    this.recipe = input.isEmpty() ? null : LoomRecipe.getRecipe(input);
    if (this.recipe == null) {
      this.progress = 0;
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {

    super.writeToNBT(compound);
    compound.setTag("inventory", this.inventory.serializeNBT());
    compound.setInteger("progress", this.progress);
    compound.setLong("lastPushed", this.lastPushed);
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {

    super.readFromNBT(compound);
    this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    this.progress = compound.getInteger("progress");
    this.lastPushed = compound.getLong("lastPushed");
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
  public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt) {

    this.handleUpdateTag(pkt.getNbtCompound());
  }
}
