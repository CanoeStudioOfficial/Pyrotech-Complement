package com.canoestudios.pyrotechcomplement.tile;

import com.codetaylor.mc.athenaeum.util.ParticleHelper;
import com.canoestudios.pyrotechcomplement.block.BlockSluice;
import com.canoestudios.pyrotechcomplement.recipe.SluiceRecipe;
import com.codetaylor.mc.athenaeum.spi.TileEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class TileSluice
    extends TileEntityBase
    implements ITickable {

  public static final int MAX_SOIL = 32;
  private static final int CRUDE_PROCESS_TICKS = 200;
  private static final int NORMAL_PROCESS_TICKS = 100;

  private final ItemStackHandler inventory = new ItemStackHandler(MAX_SOIL) {
    @Override
    protected void onContentsChanged(int slot) {

      TileSluice.this.notifyBlockUpdate();
    }

    @Override
    public int getSlotLimit(int slot) {

      return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {

      return SluiceRecipe.isValidInput(stack);
    }
  };

  private int ticksRemaining;

  public TileSluice() {

    this.ticksRemaining = 0;
  }

  @Override
  public void update() {

    if (this.world == null || !this.isUpperBlock()) {
      return;
    }

    State state = this.getRepresentativeState();
    if (this.world.isRemote) {
      if ((state == State.BOTH || state == State.INPUT_ONLY)
          && this.hasInputItems()
          && this.world.getTotalWorldTime() % 40 == 0) {
        ParticleHelper.spawnProgressParticlesClient(
            1,
            this.pos.getX() + 0.5,
            this.pos.getY() + 0.95,
            this.pos.getZ() + 0.5,
            0.45,
            0.15,
            0.45
        );
      }
      return;
    }

    // A flowing water block is enough to operate the sluice. The outlet is not
    // part of the machine's input and must never be consumed or cleared.
    if (state != State.BOTH && state != State.INPUT_ONLY) {
      return;
    }

    if (this.world.getTotalWorldTime() % 20 == 0) {
      this.collectItemsFromWorld();
    }

    if (--this.ticksRemaining <= 0) {
      this.processOne();
      this.ticksRemaining = this.getProcessingTicks();
      this.notifyBlockUpdate();
    }
  }

  private boolean hasInputItems() {

    for (int slot = 0; slot < this.inventory.getSlots(); slot++) {
      if (!this.inventory.getStackInSlot(slot).isEmpty()) {
        return true;
      }
    }
    return false;
  }

  public boolean onRightClick(EntityPlayer player, EnumHand hand) {

    ItemStack heldItem = player.getHeldItem(hand);

    if (player.isSneaking() && heldItem.isEmpty()) {
      return this.removeOne(player);
    }

    if (!heldItem.isEmpty() && SluiceRecipe.isValidInput(heldItem)) {
      if (!this.hasSpace()) {
        return true;
      }

      if (!this.world.isRemote && this.tryInsertOne(heldItem)) {
        heldItem.shrink(1);
      }
      return true;
    }

    return false;
  }

  private boolean removeOne(EntityPlayer player) {

    for (int slot = 0; slot < this.getCapacity(); slot++) {
      ItemStack stack = this.inventory.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        if (!this.world.isRemote) {
          ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
          this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
        return true;
      }
    }
    return false;
  }

  private boolean hasSpace() {

    for (int slot = 0; slot < this.getCapacity(); slot++) {
      if (this.inventory.getStackInSlot(slot).isEmpty()) {
        return true;
      }
    }
    return false;
  }

  private boolean tryInsertOne(ItemStack stack) {

    if (!SluiceRecipe.isValidInput(stack)) {
      return false;
    }

    for (int slot = 0; slot < this.getCapacity(); slot++) {
      if (this.inventory.getStackInSlot(slot).isEmpty()) {
        ItemStack inserted = stack.copy();
        inserted.setCount(1);
        this.inventory.setStackInSlot(slot, inserted);
        return true;
      }
    }
    return false;
  }

  private void collectItemsFromWorld() {

    if (!this.hasSpace()) {
      return;
    }

    AxisAlignedBB bounds = new AxisAlignedBB(
        this.pos.getX(), this.pos.getY(), this.pos.getZ(),
        this.pos.getX() + 1, this.pos.getY() + 1.35, this.pos.getZ() + 1
    );
    List<EntityItem> entities = this.world.getEntitiesWithinAABB(
        EntityItem.class,
        bounds,
        entity -> entity != null && !entity.isDead && !entity.getItem().isEmpty()
    );

    for (EntityItem entity : entities) {
      ItemStack stack = entity.getItem();
      while (!stack.isEmpty() && this.hasSpace() && this.tryInsertOne(stack)) {
        stack.shrink(1);
      }

      if (stack.isEmpty()) {
        entity.setDead();
      } else {
        entity.setItem(stack);
      }
    }
  }

  private void processOne() {

    for (int slot = 0; slot < this.getCapacity(); slot++) {
      ItemStack input = this.inventory.getStackInSlot(slot);
      if (input.isEmpty()) {
        continue;
      }

      ItemStack output = SluiceRecipe.rollOutput(input, this.world.rand, this.isCrudeTier());
      if (output != null && !output.isEmpty()) {
        this.spawnOutput(output);
      }

      this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
      this.world.playSound(null, this.pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.65f, 0.8f + this.world.rand.nextFloat() * 0.3f);
      return;
    }
  }

  private void spawnOutput(ItemStack output) {

    EnumFacing facing = this.getFacing();
    BlockPos outputPos = this.getWaterOutputPos();
    EntityItem entity = new EntityItem(
        this.world,
        outputPos.getX() + 0.5,
        outputPos.getY() + 1.05,
        outputPos.getZ() + 0.5,
        output.copy()
    );
    entity.motionX = facing.getXOffset() * (0.08 + this.world.rand.nextFloat() * 0.06);
    entity.motionY = 0.08;
    entity.motionZ = facing.getZOffset() * (0.08 + this.world.rand.nextFloat() * 0.06);
    this.world.spawnEntity(entity);
  }

  public void dropContents() {

    if (this.world == null || this.world.isRemote) {
      return;
    }

    for (int slot = 0; slot < this.inventory.getSlots(); slot++) {
      ItemStack stack = this.inventory.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        this.world.spawnEntity(new EntityItem(
            this.world,
            this.pos.getX() + 0.5,
            this.pos.getY() + 0.75,
            this.pos.getZ() + 0.5,
            stack.copy()
        ));
      }
    }
  }

  private boolean isUpperBlock() {

    IBlockState state = this.world.getBlockState(this.pos);
    return state.getBlock() instanceof BlockSluice && state.getValue(BlockSluice.UPPER);
  }

  public int getCapacity() {

    IBlockState state = this.world.getBlockState(this.pos);
    if (state.getBlock() instanceof BlockSluice
        && ((BlockSluice) state.getBlock()).getTier() == BlockSluice.Tier.CRUDE) {
      return MAX_SOIL / 2;
    }
    return MAX_SOIL;
  }

  public int getProcessingTicks() {

    IBlockState state = this.world.getBlockState(this.pos);
    if (state.getBlock() instanceof BlockSluice
        && ((BlockSluice) state.getBlock()).getTier() == BlockSluice.Tier.CRUDE) {
      return CRUDE_PROCESS_TICKS;
    }
    return NORMAL_PROCESS_TICKS;
  }

  private boolean isCrudeTier() {

    IBlockState state = this.world.getBlockState(this.pos);
    return state.getBlock() instanceof BlockSluice
        && ((BlockSluice) state.getBlock()).getTier() == BlockSluice.Tier.CRUDE;
  }

  public EnumFacing getFacing() {

    return this.world.getBlockState(this.pos).getValue(BlockSluice.FACING);
  }

  public BlockPos getWaterOutputPos() {

    return BlockSluice.getFluidOutputPos(this.world.getBlockState(this.pos), this.pos);
  }

  public int getStoredInputCount() {

    int count = 0;
    for (int slot = 0; slot < this.getCapacity(); slot++) {
      if (!this.inventory.getStackInSlot(slot).isEmpty()) {
        count++;
      }
    }
    return count;
  }

  public ItemStack getFirstInput() {

    for (int slot = 0; slot < this.getCapacity(); slot++) {
      ItemStack stack = this.inventory.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        return stack.copy();
      }
    }
    return ItemStack.EMPTY;
  }

  public int getProgress() {

    int total = this.getProcessingTicks();
    return Math.max(0, Math.min(total, total - this.ticksRemaining));
  }

  public boolean isWaterReady() {

    State state = this.getRepresentativeState();
    return state == State.BOTH || state == State.INPUT_ONLY;
  }

  private BlockPos getWaterInputPos() {

    return this.pos.up().offset(this.getFacing().getOpposite());
  }

  private State getRepresentativeState() {

    BlockPos inputPos = this.getWaterInputPos();
    BlockPos outputPos = this.getWaterOutputPos();
    IBlockState inputState = this.world.getBlockState(inputPos);
    IBlockState outputState = this.world.getBlockState(outputPos);

    boolean inputValid = isWater(inputState) && isUsableInputWater(inputState);
    boolean outputValid = isWater(outputState);

    if (inputValid) {
      return outputValid ? State.BOTH : State.INPUT_ONLY;
    }
    return outputValid ? State.OUTPUT_ONLY : State.NONE;
  }

  private static boolean isWater(IBlockState state) {

    return state.getMaterial() == Material.WATER;
  }

  private static boolean isUsableInputWater(IBlockState state) {

    // Accept source and every flowing-water level. In 1.12.2 the LEVEL value
    // increases as water flows away from its source.
    return isWater(state);
  }

  public ItemStack getStackInSlot(int slot) {

    return this.inventory.getStackInSlot(slot);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {

    super.writeToNBT(compound);
    compound.setTag("inventory", this.inventory.serializeNBT());
    compound.setInteger("ticksRemaining", this.ticksRemaining);
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {

    super.readFromNBT(compound);
    this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    this.ticksRemaining = compound.getInteger("ticksRemaining");
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

  private enum State {
    NONE,
    INPUT_ONLY,
    OUTPUT_ONLY,
    BOTH
  }
}
