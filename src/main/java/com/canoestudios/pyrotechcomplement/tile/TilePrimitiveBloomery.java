package com.canoestudios.pyrotechcomplement.tile;

import com.canoestudios.pyrotechcomplement.block.BlockPrimitiveBloomery;
import com.canoestudios.pyrotechcomplement.recipe.PrimitiveBloomeryRecipe;
import com.codetaylor.mc.athenaeum.spi.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TilePrimitiveBloomery
    extends TileEntityBase
    implements ITickable {

  private static final int SLOT_INPUT = 0;
  private static final int SLOT_FUEL = 1;
  private static final int SLOT_OUTPUT = 2;
  private static final int MAX_CHIMNEY_LEVELS = 4;
  private static final int CAPACITY_PER_CHIMNEY_LEVEL = 8;

  private final ItemStackHandler inventory = new ItemStackHandler(3) {
    @Override
    protected void onContentsChanged(int slot) {

      TilePrimitiveBloomery.this.updateCachedRecipe();
      TilePrimitiveBloomery.this.notifyBlockUpdate();
    }

    @Override
    public int getSlotLimit(int slot) {

      if (slot == SLOT_OUTPUT) {
        return 64;
      }

      return TilePrimitiveBloomery.this.getCapacity();
    }
  };

  @Nullable
  private PrimitiveBloomeryRecipe recipe;
  private int progress;
  private int burnTimeTicks;

  @Override
  public void update() {

    if (this.world == null || this.world.isRemote || this.world.getTotalWorldTime() % 20 != 0) {
      return;
    }

    IBlockState state = this.world.getBlockState(this.pos);
    if (!(state.getBlock() instanceof BlockPrimitiveBloomery)) {
      return;
    }

    if (state.getValue(BlockPrimitiveBloomery.LIT)) {
      this.updateLit(state);
    } else {
      this.collectItemsFromWorld();
    }
  }

  public boolean onRightClick(EntityPlayer player, EnumHand hand) {

    ItemStack output = this.inventory.getStackInSlot(SLOT_OUTPUT);
    if (!output.isEmpty()) {
      if (!this.world.isRemote) {
        ItemHandlerHelper.giveItemToPlayer(player, output.copy());
        this.inventory.setStackInSlot(SLOT_OUTPUT, ItemStack.EMPTY);
        this.clearProgress();
      }
      return true;
    }

    if (player.isSneaking() && !this.isLit()) {
      return this.removeInput(player);
    }

    return false;
  }

  public boolean ignite() {

    if (this.world == null) {
      return false;
    }

    IBlockState state = this.world.getBlockState(this.pos);
    if (!(state.getBlock() instanceof BlockPrimitiveBloomery) || state.getValue(BlockPrimitiveBloomery.LIT)) {
      return false;
    }

    this.updateCachedRecipe();
    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    ItemStack fuel = this.inventory.getStackInSlot(SLOT_FUEL);

    if (this.recipe == null
        || !this.recipe.matchesWithCounts(input, fuel)
        || !this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()
        || !this.isFormed()) {
      return false;
    }

    if (!this.world.isRemote) {
      this.progress = 0;
      this.burnTimeTicks = this.recipe.getBurnTimeTicks();
      this.world.setBlockState(this.pos, state.withProperty(BlockPrimitiveBloomery.LIT, true).withProperty(BlockPrimitiveBloomery.OPEN, false), 3);
      this.world.playSound(null, this.pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, 0.95f + this.world.rand.nextFloat() * 0.1f);
      this.notifyBlockUpdate();
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

  private void updateLit(IBlockState state) {

    if (this.recipe == null) {
      this.updateCachedRecipe();
    }

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    ItemStack fuel = this.inventory.getStackInSlot(SLOT_FUEL);

    if (this.recipe == null
        || !this.recipe.matchesWithCounts(input, fuel)
        || !this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()
        || !this.isFormed()) {
      this.extinguish(state);
      return;
    }

    this.progress += 20;

    if (this.progress >= this.burnTimeTicks) {
      this.completeRecipe(state);
    } else {
      this.notifyBlockUpdate();
    }
  }

  private void completeRecipe(IBlockState state) {

    if (this.recipe == null) {
      this.extinguish(state);
      return;
    }

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    ItemStack fuel = this.inventory.getStackInSlot(SLOT_FUEL);
    int batches = this.recipe.getBatches(input, fuel);

    if (batches <= 0) {
      this.extinguish(state);
      return;
    }

    input.shrink(this.recipe.getInputCount() * batches);
    fuel.shrink(this.recipe.getFuelCount() * batches);
    this.inventory.setStackInSlot(SLOT_INPUT, input.isEmpty() ? ItemStack.EMPTY : input);
    this.inventory.setStackInSlot(SLOT_FUEL, fuel.isEmpty() ? ItemStack.EMPTY : fuel);
    this.inventory.setStackInSlot(SLOT_OUTPUT, this.recipe.getOutput(batches, this.world.rand));

    this.progress = 0;
    this.burnTimeTicks = 0;
    this.world.setBlockState(this.pos, state.withProperty(BlockPrimitiveBloomery.LIT, false), 3);
    this.world.playSound(null, this.getExternalPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.7f, 0.75f + this.world.rand.nextFloat() * 0.15f);
    this.updateCachedRecipe();
    this.notifyBlockUpdate();
  }

  private void extinguish(IBlockState state) {

    this.progress = 0;
    this.burnTimeTicks = 0;
    this.world.setBlockState(this.pos, state.withProperty(BlockPrimitiveBloomery.LIT, false), 3);
    this.notifyBlockUpdate();
  }

  private void collectItemsFromWorld() {

    if (!this.inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
      return;
    }

    int capacity = this.getCapacity();
    if (capacity <= 0 || this.getStoredInputCount() >= capacity) {
      return;
    }

    BlockPos internalPos = this.getInternalPos();
    int chimneyLevels = Math.max(1, this.getChimneyLevels());
    AxisAlignedBB bounds = new AxisAlignedBB(
        internalPos.getX(),
        internalPos.getY(),
        internalPos.getZ(),
        internalPos.getX() + 1,
        internalPos.getY() + chimneyLevels + 1,
        internalPos.getZ() + 1
    );

    List<EntityItem> entities = this.world.getEntitiesWithinAABB(EntityItem.class, bounds, entity -> entity != null && !entity.isDead && !entity.getItem().isEmpty());

    for (EntityItem entity : entities) {
      ItemStack stack = entity.getItem();

      while (!stack.isEmpty() && this.getStoredInputCount() < capacity && this.tryInsertOne(stack)) {
        stack.shrink(1);
      }

      if (stack.isEmpty()) {
        entity.setDead();
      } else {
        entity.setItem(stack);
      }
    }
  }

  private boolean tryInsertOne(ItemStack stack) {

    if (this.canInsertInput(stack)) {
      this.insertOne(SLOT_INPUT, stack);
      return true;
    }

    if (this.canInsertFuel(stack)) {
      this.insertOne(SLOT_FUEL, stack);
      return true;
    }

    return false;
  }

  private boolean canInsertInput(ItemStack stack) {

    ItemStack fuel = this.inventory.getStackInSlot(SLOT_FUEL);
    if (fuel.isEmpty() && this.getStoredInputCount() >= this.getCapacity() - 1) {
      return false;
    }

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    if (!input.isEmpty() && (!ItemHandlerHelper.canItemStacksStack(input, stack) || input.getCount() >= this.getCapacity())) {
      return false;
    }

    if (!fuel.isEmpty()) {
      return PrimitiveBloomeryRecipe.getRecipe(stack, fuel) != null;
    }

    return PrimitiveBloomeryRecipe.getRecipeForInput(stack) != null;
  }

  private boolean canInsertFuel(ItemStack stack) {

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    if (input.isEmpty() && this.getStoredInputCount() >= this.getCapacity() - 1) {
      return false;
    }

    ItemStack fuel = this.inventory.getStackInSlot(SLOT_FUEL);
    if (!fuel.isEmpty() && (!ItemHandlerHelper.canItemStacksStack(fuel, stack) || fuel.getCount() >= this.getCapacity())) {
      return false;
    }

    if (!input.isEmpty()) {
      return PrimitiveBloomeryRecipe.getRecipe(input, stack) != null;
    }

    return PrimitiveBloomeryRecipe.getRecipeForFuel(stack) != null;
  }

  private void insertOne(int slot, ItemStack stack) {

    ItemStack current = this.inventory.getStackInSlot(slot);
    if (current.isEmpty()) {
      ItemStack inserted = stack.copy();
      inserted.setCount(1);
      this.inventory.setStackInSlot(slot, inserted);
    } else {
      current.grow(1);
      this.inventory.setStackInSlot(slot, current);
    }
  }

  private boolean removeInput(EntityPlayer player) {

    ItemStack fuel = this.inventory.getStackInSlot(SLOT_FUEL);
    if (!fuel.isEmpty()) {
      if (!this.world.isRemote) {
        ItemHandlerHelper.giveItemToPlayer(player, fuel.copy());
        this.inventory.setStackInSlot(SLOT_FUEL, ItemStack.EMPTY);
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

  public boolean isFormed() {

    BlockPos internalPos = this.getInternalPos();

    if (!this.world.isAirBlock(internalPos)
        || !this.isStoneBlock(internalPos.down())) {
      return false;
    }

    EnumFacing facing = this.getFacing();
    for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
      if (side != facing && !this.isStoneBlock(internalPos.offset(side))) {
        return false;
      }
    }

    return this.getChimneyLevels() > 0;
  }

  public int getChimneyLevels() {

    BlockPos internalPos = this.getInternalPos();

    for (int i = 1; i <= MAX_CHIMNEY_LEVELS; i++) {
      BlockPos center = internalPos.up(i);

      if (!this.world.isAirBlock(center)) {
        return i - 1;
      }

      for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
        if (!this.isStoneBlock(center.offset(side))) {
          return i - 1;
        }
      }
    }

    return MAX_CHIMNEY_LEVELS;
  }

  public int getCapacity() {

    if (this.world == null) {
      return MAX_CHIMNEY_LEVELS * CAPACITY_PER_CHIMNEY_LEVEL;
    }

    return this.getChimneyLevels() * CAPACITY_PER_CHIMNEY_LEVEL;
  }

  public int getStoredInputCount() {

    return this.inventory.getStackInSlot(SLOT_INPUT).getCount()
        + this.inventory.getStackInSlot(SLOT_FUEL).getCount();
  }

  private boolean isStoneBlock(BlockPos pos) {

    IBlockState state = this.world.getBlockState(pos);
    Block block = state.getBlock();

    if (state.getMaterial() == Material.ROCK) {
      return true;
    }

    ResourceLocation registryName = block.getRegistryName();
    if (registryName != null) {
      String path = registryName.getPath().toLowerCase(Locale.ROOT);
      if (path.contains("stone")
          || path.contains("cobble")
          || path.contains("rock")
          || path.contains("granite")
          || path.contains("diorite")
          || path.contains("andesite")
          || path.contains("limestone")
          || path.contains("basalt")
          || path.contains("slate")
          || path.contains("brick")) {
        return true;
      }
    }

    Item item = Item.getItemFromBlock(block);
    if (item == null) {
      return false;
    }

    ItemStack stack = new ItemStack(item, 1, block.damageDropped(state));
    for (int id : OreDictionary.getOreIDs(stack)) {
      String oreName = OreDictionary.getOreName(id).toLowerCase(Locale.ROOT);
      if (oreName.contains("stone") || oreName.contains("cobble") || oreName.contains("rock")) {
        return true;
      }
    }

    return false;
  }

  public BlockPos getInternalPos() {

    return this.pos.offset(this.getFacing().getOpposite());
  }

  public BlockPos getExternalPos() {

    return this.pos.offset(this.getFacing());
  }

  private EnumFacing getFacing() {

    if (this.world == null) {
      return EnumFacing.NORTH;
    }

    IBlockState state = this.world.getBlockState(this.pos);
    if (state.getBlock() instanceof BlockPrimitiveBloomery) {
      return state.getValue(BlockPrimitiveBloomery.FACING);
    }
    return EnumFacing.NORTH;
  }

  @Nullable
  public PrimitiveBloomeryRecipe getRecipe() {

    return this.recipe;
  }

  public int getProgress() {

    return this.progress;
  }

  public int getBurnTimeTicks() {

    return this.burnTimeTicks;
  }

  public ItemStack getInput() {

    return this.inventory.getStackInSlot(SLOT_INPUT);
  }

  public ItemStack getFuel() {

    return this.inventory.getStackInSlot(SLOT_FUEL);
  }

  public ItemStack getOutput() {

    return this.inventory.getStackInSlot(SLOT_OUTPUT);
  }

  private void clearProgress() {

    this.progress = 0;
    this.burnTimeTicks = 0;
    this.recipe = null;
    this.notifyBlockUpdate();
  }

  private boolean isLit() {

    if (this.world == null) {
      return false;
    }

    IBlockState state = this.world.getBlockState(this.pos);
    return state.getBlock() instanceof BlockPrimitiveBloomery
        && state.getValue(BlockPrimitiveBloomery.LIT);
  }

  private void updateCachedRecipe() {

    ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
    ItemStack fuel = this.inventory.getStackInSlot(SLOT_FUEL);

    this.recipe = PrimitiveBloomeryRecipe.getRecipe(input, fuel);

    if (this.recipe == null && !input.isEmpty() && fuel.isEmpty()) {
      this.recipe = PrimitiveBloomeryRecipe.getRecipeForInput(input);
    }

    if (this.recipe == null && input.isEmpty() && !fuel.isEmpty()) {
      this.recipe = PrimitiveBloomeryRecipe.getRecipeForFuel(fuel);
    }

    if (this.recipe == null) {
      this.progress = 0;
      this.burnTimeTicks = 0;
    } else if (this.burnTimeTicks <= 0) {
      this.burnTimeTicks = this.recipe.getBurnTimeTicks();
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {

    super.writeToNBT(compound);
    compound.setTag("inventory", this.inventory.serializeNBT());
    compound.setInteger("progress", this.progress);
    compound.setInteger("burnTimeTicks", this.burnTimeTicks);
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {

    super.readFromNBT(compound);
    this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    this.progress = compound.getInteger("progress");
    this.burnTimeTicks = compound.getInteger("burnTimeTicks");
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
