package com.canoestudios.pyrotechcomplement.tile;

import com.canoestudios.pyrotechcomplement.recipe.ForgingTableRecipe;
import com.codetaylor.mc.athenaeum.spi.TileEntityBase;
import com.codetaylor.mc.pyrotech.modules.core.ModuleCore;
import com.codetaylor.mc.pyrotech.modules.core.ModuleCoreConfig;
import com.codetaylor.mc.pyrotech.modules.core.item.spi.ItemHammerBase;
import com.codetaylor.mc.pyrotech.modules.core.network.SCPacketParticleProgress;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Locale;

public class TileForgingTable
    extends TileEntityBase {

  private static final int SLOT_INPUT = 0;
  private static final int SLOT_SECONDARY_INPUT = 1;
  private static final int SLOT_OUTPUT = 2;

  private final ItemStackHandler inventory = new ItemStackHandler(3) {
    @Override
    protected void onContentsChanged(int slot) {

      TileForgingTable.this.updateCachedRecipe();
      TileForgingTable.this.notifyBlockUpdate();
    }

    @Override
    public int getSlotLimit(int slot) {

      if (slot == SLOT_INPUT && TileForgingTable.this.recipe != null) {
        return TileForgingTable.this.recipe.getInputCount();
      }

      if (slot == SLOT_SECONDARY_INPUT && TileForgingTable.this.recipe != null) {
        return TileForgingTable.this.recipe.getSecondaryInputCount();
      }

      if (slot == SLOT_OUTPUT) {
        return 64;
      }

      return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {

      if (TileForgingTable.this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
        if (slot == SLOT_INPUT) {
          return ForgingTableRecipe.getRecipe(stack) != null;
        }

        if (slot == SLOT_SECONDARY_INPUT && TileForgingTable.this.recipe != null) {
          return TileForgingTable.this.recipe.matchesSecondaryInput(stack);
        }
      }

      return false;
    }
  };

  @Nullable
  private ForgingTableRecipe recipe;
  private int progress;

  public boolean onRightClick(EntityPlayer player, EnumHand hand) {

    ItemStack heldItem = player.getHeldItem(hand);

    ItemStack output = this.inventory.getStackInSlot(SLOT_OUTPUT);
    if (!output.isEmpty()) {
      if (!this.world.isRemote) {
        ItemHandlerHelper.giveItemToPlayer(player, output.copy());
        this.inventory.setStackInSlot(SLOT_OUTPUT, ItemStack.EMPTY);
        this.clearProgress();
      }
      return true;
    }

    if (player.isSneaking()) {
      return this.removeInput(player);
    }

    if (TileForgingTable.isHammer(heldItem)) {
      return this.work(player, heldItem);
    }

    if (!heldItem.isEmpty()) {
      return this.insertHeldItem(heldItem);
    }

    return false;
  }

  private boolean insertHeldItem(ItemStack heldItem) {

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);

    if (input.isEmpty()) {
      ForgingTableRecipe matchingRecipe = ForgingTableRecipe.getRecipe(heldItem);

      if (matchingRecipe == null) {
        return false;
      }

      if (!this.world.isRemote) {
        ItemStack inserted = heldItem.splitStack(1);
        this.inventory.setStackInSlot(SLOT_INPUT, inserted);
        this.recipe = matchingRecipe;
        this.progress = 0;
      }
      return true;
    }

    if (this.recipe == null) {
      this.updateCachedRecipe();
    }

    if (this.recipe == null) {
      return false;
    }

    if (this.recipe.matchesInput(heldItem)
        && ItemHandlerHelper.canItemStacksStack(input, heldItem)
        && input.getCount() < this.recipe.getInputCount()) {
      if (!this.world.isRemote) {
        heldItem.shrink(1);
        input.grow(1);
        this.inventory.setStackInSlot(SLOT_INPUT, input);
        this.progress = 0;
      }
      return true;
    }

    ForgingTableRecipe secondaryRecipe = ForgingTableRecipe.getRecipeForSecondary(input, heldItem);
    if (secondaryRecipe != null) {
      ItemStack secondaryInput = this.inventory.getStackInSlot(SLOT_SECONDARY_INPUT);

      if (secondaryInput.isEmpty()) {
        if (!this.world.isRemote) {
          ItemStack inserted = heldItem.splitStack(1);
          this.recipe = secondaryRecipe;
          this.inventory.setStackInSlot(SLOT_SECONDARY_INPUT, inserted);
          this.progress = 0;
        }
        return true;
      }

      if (ItemHandlerHelper.canItemStacksStack(secondaryInput, heldItem)
          && secondaryInput.getCount() < secondaryRecipe.getSecondaryInputCount()) {
        if (!this.world.isRemote) {
          heldItem.shrink(1);
          secondaryInput.grow(1);
          this.recipe = secondaryRecipe;
          this.inventory.setStackInSlot(SLOT_SECONDARY_INPUT, secondaryInput);
          this.progress = 0;
        }
        return true;
      }
    }

    return false;
  }

  private boolean work(EntityPlayer player, ItemStack hammer) {

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    ItemStack secondaryInput = this.inventory.getStackInSlot(SLOT_SECONDARY_INPUT);
    ForgingTableRecipe matchingRecipe = ForgingTableRecipe.getRecipe(input, secondaryInput);

    if (matchingRecipe == null || !matchingRecipe.matchesWithCounts(input, secondaryInput)) {
      return false;
    }

    if (!this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
      return false;
    }

    if (!this.world.isRemote) {
      this.recipe = matchingRecipe;
      this.progress++;
      this.damageHammer(player, hammer);
      this.world.playSound(null, this.pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 0.75f, 0.9f + this.world.rand.nextFloat() * 0.2f);
      this.spawnProgressParticles();

      if (this.progress >= matchingRecipe.getHits()) {
        input.shrink(matchingRecipe.getInputCount());

        if (matchingRecipe.hasSecondaryInput()) {
          secondaryInput.shrink(matchingRecipe.getSecondaryInputCount());
        }

        this.inventory.setStackInSlot(SLOT_INPUT, input.isEmpty() ? ItemStack.EMPTY : input);
        this.inventory.setStackInSlot(SLOT_SECONDARY_INPUT, secondaryInput.isEmpty() ? ItemStack.EMPTY : secondaryInput);
        this.inventory.setStackInSlot(SLOT_OUTPUT, matchingRecipe.getOutput());
        this.progress = 0;
      } else {
        this.notifyBlockUpdate();
      }
    }

    return true;
  }

  private void spawnProgressParticles() {

    if (ModuleCore.PACKET_SERVICE != null) {
      ModuleCore.PACKET_SERVICE.sendToAllAround(
          new SCPacketParticleProgress(this.pos.getX() + 0.5, this.pos.getY() + 1, this.pos.getZ() + 0.5, 2),
          this.world.provider.getDimension(),
          this.pos
      );
    }
  }

  private void damageHammer(EntityPlayer player, ItemStack hammer) {

    if (!player.capabilities.isCreativeMode && hammer.isItemStackDamageable()) {
      hammer.damageItem(1, player);
    }
  }

  private boolean removeInput(EntityPlayer player) {

    ItemStack secondaryInput = this.inventory.getStackInSlot(SLOT_SECONDARY_INPUT);
    if (!secondaryInput.isEmpty()) {
      if (!this.world.isRemote) {
        ItemHandlerHelper.giveItemToPlayer(player, secondaryInput.copy());
        this.inventory.setStackInSlot(SLOT_SECONDARY_INPUT, ItemStack.EMPTY);
        this.clearProgress();
      }
      return true;
    }

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    if (!input.isEmpty()) {
      if (!this.world.isRemote) {
        ItemHandlerHelper.giveItemToPlayer(player, input.copy());
        this.inventory.setStackInSlot(SLOT_INPUT, ItemStack.EMPTY);
        this.clearProgress();
      }
      return true;
    }

    return false;
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

  public static boolean isHammer(ItemStack itemStack) {

    if (itemStack.isEmpty()) {
      return false;
    }

    if (itemStack.getItem() instanceof ItemHammerBase) {
      return true;
    }

    ResourceLocation registryName = itemStack.getItem().getRegistryName();
    if (registryName != null && ModuleCoreConfig.HAMMERS.getHammerHarvestLevel(registryName) > -1) {
      return true;
    }

    if (itemStack.getItem().getToolClasses(itemStack).contains("hammer")) {
      return true;
    }

    for (int id : OreDictionary.getOreIDs(itemStack)) {
      String oreName = OreDictionary.getOreName(id).toLowerCase(Locale.ROOT);

      if ("toolhammer".equals(oreName) || oreName.contains("hammer")) {
        return true;
      }
    }

    return false;
  }

  @Nullable
  public ForgingTableRecipe getRecipe() {

    return this.recipe;
  }

  public int getProgress() {

    return this.progress;
  }

  public ItemStack getInput() {

    return this.inventory.getStackInSlot(SLOT_INPUT);
  }

  public ItemStack getSecondaryInput() {

    return this.inventory.getStackInSlot(SLOT_SECONDARY_INPUT);
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
    ItemStack secondaryInput = this.inventory.getStackInSlot(SLOT_SECONDARY_INPUT);
    this.recipe = input.isEmpty() ? null : ForgingTableRecipe.getRecipe(input, secondaryInput);

    if (this.recipe == null && !input.isEmpty() && secondaryInput.isEmpty()) {
      this.recipe = ForgingTableRecipe.getRecipe(input);
    }

    if (this.recipe == null) {
      this.progress = 0;
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {

    super.writeToNBT(compound);
    compound.setTag("inventory", this.inventory.serializeNBT());
    compound.setInteger("progress", this.progress);
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {

    super.readFromNBT(compound);
    this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    this.progress = compound.getInteger("progress");
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
