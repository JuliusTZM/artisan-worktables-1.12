package com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi;

import com.codetaylor.mc.artisanworktables.modules.toolbox.tile.TileEntityMechanicalToolbox;
import com.codetaylor.mc.artisanworktables.modules.toolbox.tile.TileEntityToolbox;
import com.codetaylor.mc.artisanworktables.modules.worktables.ModuleWorktables;
import com.codetaylor.mc.artisanworktables.modules.worktables.api.WorktableAPI;
import com.codetaylor.mc.artisanworktables.modules.worktables.block.EnumType;
import com.codetaylor.mc.artisanworktables.modules.worktables.gui.Container;
import com.codetaylor.mc.artisanworktables.modules.worktables.gui.GuiContainerBase;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.IRecipe;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.ISecondaryIngredientMatcher;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.RegistryRecipe;
import com.codetaylor.mc.artisanworktables.modules.worktables.reference.EnumTier;
import com.codetaylor.mc.athenaeum.helper.StackHelper;
import com.codetaylor.mc.athenaeum.inventory.ObservableStackHandler;
import com.codetaylor.mc.athenaeum.tile.IContainer;
import com.codetaylor.mc.athenaeum.tile.IContainerProvider;
import com.codetaylor.mc.athenaeum.util.BlockHelper;
import com.codetaylor.mc.athenaeum.util.BottleHelper;
import com.codetaylor.mc.athenaeum.util.EnchantmentHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class TileEntityBase
    extends TileEntity
    implements IContainer,
    IContainerProvider<Container, GuiContainerBase> {

  private Random random = new Random();

  private EnumType type;
  private ObservableStackHandler toolHandler;
  private CraftingMatrixStackHandler craftingMatrixHandler;
  private ObservableStackHandler secondaryOutputHandler;
  private FluidTank tank;

  protected boolean initialized;

  protected TileEntityBase() {
    // serialization
  }

  public TileEntityBase(EnumType type) {

    this.type = type;
    this.initialize(type);

    /*
    this.craftingMatrixHandler = new CraftingMatrixStackHandler(this.getCraftingWidth(), this.getCraftingHeight());
    this.toolHandler = new ObservableStackHandler(this.getToolSlotCount());
    this.secondaryOutputHandler = new ObservableStackHandler(3);

    */
  }

  protected void initialize(EnumType type) {

    if (this.initialized) {
      return;
    }

    this.initialized = true;

    this.craftingMatrixHandler = this.createCraftingMatrixHandler();
    this.toolHandler = this.createToolHandler();
    this.secondaryOutputHandler = this.createSecondaryOutputHandler();
    this.tank = this.createFluidTank(type);

    ObservableStackHandler.IContentsChangedEventHandler contentsChangedEventHandler;
    contentsChangedEventHandler = (stackHandler, slotIndex) -> this.markDirty();
    this.craftingMatrixHandler.addObserver(contentsChangedEventHandler);
    this.toolHandler.addObserver(contentsChangedEventHandler);
    this.secondaryOutputHandler.addObserver(contentsChangedEventHandler);
  }

  public FluidTank getTank() {

    return this.tank;
  }

  public ItemStackHandler getToolHandler() {

    return this.toolHandler;
  }

  public CraftingMatrixStackHandler getCraftingMatrixHandler() {

    return this.craftingMatrixHandler;
  }

  public ItemStackHandler getSecondaryOutputHandler() {

    return this.secondaryOutputHandler;
  }

  protected String getWorktableName() {

    return this.type.getName();
  }

  protected int getGuiTextShadowColor() {

    return this.type.getTextOutlineColor();
  }

  public boolean canHandleJEIRecipeTransfer(
      String name,
      EnumTier tier
  ) {

    return this.type.getName().equals(name) && tier.getId() <= this.getTier().getId();
  }

  public int getWorktableGuiTabTextureYOffset() {

    return this.type.getGuiTabTextureOffsetY();
  }

  public EnumType getType() {

    return this.type;
  }

  @Override
  public boolean hasCapability(
      @Nonnull Capability<?> capability, @Nullable EnumFacing facing
  ) {

    return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
  }

  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {

    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      //noinspection unchecked
      return (T) this.tank;
    }

    return super.getCapability(capability, facing);
  }

  @Override
  public List<ItemStack> getBlockBreakDrops() {

    List<ItemStack> result = new ArrayList<>();

    for (int i = 0; i < this.toolHandler.getSlots(); i++) {
      ItemStack itemStack = this.toolHandler.getStackInSlot(i);

      if (!itemStack.isEmpty()) {
        result.add(itemStack);
      }
    }

    int slotCount = this.craftingMatrixHandler.getSlots();

    for (int i = 0; i < slotCount; i++) {
      ItemStack itemStack = this.craftingMatrixHandler.getStackInSlot(i);

      if (!itemStack.isEmpty()) {
        result.add(itemStack);
      }
    }

    if (this.secondaryOutputHandler != null) {

      for (int i = 0; i < this.secondaryOutputHandler.getSlots(); i++) {
        result.add(this.secondaryOutputHandler.getStackInSlot(i));
      }
    }

    return result;
  }

  public boolean canPlayerUse(EntityPlayer player) {

    return this.getWorld().getTileEntity(this.getPos()) == this
        && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;
  }

  @Override
  public boolean shouldRefresh(
      World world, BlockPos pos, IBlockState oldState, IBlockState newSate
  ) {

    return oldState.getBlock() != newSate.getBlock();
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {

    tag = super.writeToNBT(tag);
    tag.setInteger("type", this.type.getMeta());
    tag.setTag("craftingMatrixHandler", this.craftingMatrixHandler.serializeNBT());
    tag.setTag("toolHandler", this.toolHandler.serializeNBT());
    tag.setTag("secondaryOutputHandler", this.secondaryOutputHandler.serializeNBT());
    tag.setTag("tank", this.tank.writeToNBT(new NBTTagCompound()));
    return tag;
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {

    super.readFromNBT(tag);
    this.type = EnumType.fromMeta(tag.getInteger("type"));
    this.initialize(this.type);
    this.craftingMatrixHandler.deserializeNBT(tag.getCompoundTag("craftingMatrixHandler"));
    this.toolHandler.deserializeNBT(tag.getCompoundTag("toolHandler"));
    this.secondaryOutputHandler.deserializeNBT(tag.getCompoundTag("secondaryOutputHandler"));
    this.tank.readFromNBT(tag.getCompoundTag("tank"));
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {

    return new SPacketUpdateTileEntity(this.pos, -1, this.getUpdateTag());
  }

  @Override
  public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {

    this.readFromNBT(packet.getNbtCompound());
  }

  @Override
  public final NBTTagCompound getUpdateTag() {

    return this.writeToNBT(new NBTTagCompound());
  }

  public void onTakeResult(EntityPlayer player) {

    // Find the recipe
    IRecipe recipe = this.getRecipe(player);

    if (recipe == null) {
      return;
    }

    // Reduce player experience
    this.onCraftReduceExperience(player, recipe);

    // Decrease stacks in crafting matrix
    this.onCraftReduceIngredients(recipe);

    // Check if the recipe has multiple, weighted outputs and swap outputs accordingly.
    this.onCraftCheckAndSwapWeightedOutput(player, recipe);

    // Check for and populate secondary, tertiary and quaternary outputs
    this.onCraftProcessExtraOutput(recipe);

    // Damage or destroy tools
    // Check for replacement tool
    for (int i = 0; i < recipe.getToolCount(); i++) {
      this.onCraftDamageTool(player, i, recipe);
      this.onCraftCheckAndReplaceTool(recipe, i);
    }

    this.markDirty();

    if (!this.world.isRemote) {
      this.notifyBlockUpdate();
    }
  }

  private void onCraftReduceExperience(EntityPlayer player, IRecipe recipe) {

    if (player.isCreative()) {
      // Don't consume experience when the player is in creative mode.
      return;
    }

    if (recipe.consumeExperience()) {

      if (recipe.getExperienceRequired() > 0) {
        EnchantmentHelper.adjustPlayerExperience(player, -recipe.getExperienceRequired());

      } else if (recipe.getLevelRequired() > 0) {
        int experience = EnchantmentHelper.getExperienceToLevel(
            player.experienceLevel - recipe.getLevelRequired(),
            player.experienceLevel
        );
        EnchantmentHelper.adjustPlayerExperience(player, -experience);
      }
    }
  }

  private void onCraftCheckAndSwapWeightedOutput(
      EntityPlayer player,
      IRecipe recipe
  ) {

    if (!this.world.isRemote && !player.inventory.getItemStack().isEmpty()) {

      if (recipe.hasMultipleWeightedOutputs()) {
        ItemStack itemStack = recipe.selectOutput(this.random);
        player.inventory.setItemStack(itemStack);
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(-1, -1, itemStack));
      }
    }
  }

  private void onCraftProcessExtraOutput(IRecipe recipe) {

    if (!this.world.isRemote) {
      ItemStack extraOutput = recipe.getSecondaryOutput();

      if (!extraOutput.isEmpty()) {

        if (this.random.nextFloat() < recipe.getSecondaryOutputChance()) {
          this.generateExtraOutput(extraOutput);
        }
      }

      extraOutput = recipe.getTertiaryOutput();

      if (!extraOutput.isEmpty()) {

        if (this.random.nextFloat() < recipe.getTertiaryOutputChance()) {
          this.generateExtraOutput(extraOutput);
        }
      }

      extraOutput = recipe.getQuaternaryOutput();

      if (!extraOutput.isEmpty()) {

        if (this.random.nextFloat() < recipe.getQuaternaryOutputChance()) {
          this.generateExtraOutput(extraOutput);
        }
      }
    }
  }

  private void onCraftDamageTool(EntityPlayer player, int toolIndex, IRecipe recipe) {

    ItemStack itemStack = this.toolHandler.getStackInSlot(toolIndex);

    if (!itemStack.isEmpty() && recipe.isValidTool(itemStack, toolIndex)) {
      int itemDamage = itemStack.getMetadata() + recipe.getToolDamage(toolIndex);

      if (itemDamage >= itemStack.getItem().getMaxDamage(itemStack)) {
        this.toolHandler.setStackInSlot(toolIndex, ItemStack.EMPTY);

        if (!this.world.isRemote) {
          this.world.playSound(
              player,
              player.posX,
              player.posY,
              player.posZ,
              SoundEvents.ENTITY_ITEM_BREAK,
              SoundCategory.PLAYERS,
              1.0f,
              1.0f
          );
        }

      } else {
        ItemStack copy = itemStack.copy();
        copy.setItemDamage(itemDamage);
        this.toolHandler.setStackInSlot(toolIndex, copy);
      }
    }
  }

  private void onCraftCheckAndReplaceTool(IRecipe recipe, int toolIndex) {

    ItemStack itemStack = this.toolHandler.getStackInSlot(toolIndex);

    if (!recipe.hasSufficientToolDurability(itemStack, toolIndex)) {
      // Tool needs to be replaced
      TileEntityToolbox adjacentToolbox = this.getAdjacentToolbox();

      if (adjacentToolbox == null
          || !(adjacentToolbox instanceof TileEntityMechanicalToolbox)) {
        return;
      }

      IItemHandler capability = adjacentToolbox.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

      if (capability == null) {
        return;
      }

      int slotCount = capability.getSlots();

      for (int i = 0; i < slotCount; i++) {
        ItemStack potentialTool = capability.getStackInSlot(i);

        if (potentialTool.isEmpty()) {
          continue;
        }

        if (recipe.isValidTool(potentialTool, toolIndex)
            && recipe.hasSufficientToolDurability(potentialTool, toolIndex)) {
          // Found an acceptable tool
          potentialTool = capability.extractItem(i, 1, false);
          capability.insertItem(i, this.toolHandler.getStackInSlot(toolIndex), false);
          this.toolHandler.setStackInSlot(toolIndex, potentialTool);

          this.notifyBlockUpdate();
          adjacentToolbox.notifyBlockUpdate();
        }
      }
    }
  }

  public void notifyBlockUpdate() {

    this.markDirty();
    BlockHelper.notifyBlockUpdate(this.getWorld(), this.getPos());
  }

  protected void onCraftReduceIngredients(IRecipe recipe) {

    int slotCount = this.craftingMatrixHandler.getSlots();

    for (int i = 0; i < slotCount; i++) {
      ItemStack itemStack = this.craftingMatrixHandler.getStackInSlot(i);

      if (!itemStack.isEmpty()) {

        if (itemStack.getItem().hasContainerItem(itemStack)
            && itemStack.getCount() == 1) {
          this.craftingMatrixHandler.setStackInSlot(i, itemStack.getItem().getContainerItem(itemStack));

        } else {
          this.craftingMatrixHandler.setStackInSlot(i, StackHelper.decrease(itemStack.copy(), 1, false));
        }
      }
    }

    if (recipe != null) {
      FluidStack fluidIngredient = recipe.getFluidIngredient();

      if (fluidIngredient != null) {
        this.tank.drain(fluidIngredient, true);
      }
    }
  }

  public IRecipe getRecipe(EntityPlayer player) {

    FluidStack fluidStack = this.getTank().getFluid();

    if (fluidStack != null) {
      fluidStack = fluidStack.copy();
    }

    RegistryRecipe registry = this.getWorktableRecipeRegistry();
    return registry.findRecipe(
        player,
        this.getTools(),
        this.craftingMatrixHandler,
        fluidStack,
        this.getSecondaryIngredientMatcher(),
        this.getTier()
    );
  }

  public ISecondaryIngredientMatcher getSecondaryIngredientMatcher() {

    return ISecondaryIngredientMatcher.FALSE;
  }

  public ItemStack[] getTools() {

    int slotCount = this.toolHandler.getSlots();
    ItemStack[] tools = new ItemStack[slotCount];

    for (int i = 0; i < slotCount; i++) {
      tools[i] = this.toolHandler.getStackInSlot(i);
    }

    return tools;
  }

  public boolean hasTool() {

    ItemStack[] tools = this.getTools();
    boolean hasTool = false;

    for (ItemStack tool : tools) {

      if (!tool.isEmpty()) {
        hasTool = true;
        break;
      }
    }

    return hasTool;
  }

  private void generateExtraOutput(ItemStack extraOutput) {

    ItemStack result = extraOutput;

    for (int i = 0; i < 3; i++) {
      result = this.secondaryOutputHandler.insertItem(i, result, false);

      if (result.isEmpty()) {
        break;
      }
    }

    if (!result.isEmpty() && !this.world.isRemote) {
      StackHelper.spawnStackOnTop(this.world, result, this.pos.add(0, 1, 0));
    }
  }

  public List<TileEntityBase> getJoinedTables(List<TileEntityBase> result) {

    Map<String, TileEntityBase> joinedTableMap = new TreeMap<>();
    joinedTableMap.put(this.getClass().getName(), this);

    Set<BlockPos> searchedPositionSet = new HashSet<>();
    searchedPositionSet.add(this.pos);

    Queue<BlockPos> toSearchQueue = new ArrayDeque<>();
    toSearchQueue.offer(this.pos.offset(EnumFacing.NORTH));
    toSearchQueue.offer(this.pos.offset(EnumFacing.EAST));
    toSearchQueue.offer(this.pos.offset(EnumFacing.SOUTH));
    toSearchQueue.offer(this.pos.offset(EnumFacing.WEST));

    BlockPos searchPosition;

    while ((searchPosition = toSearchQueue.poll()) != null) {

      if (searchedPositionSet.contains(searchPosition)) {
        // we've already looked here, skip
        continue;
      }

      // record that we've looked here
      searchedPositionSet.add(searchPosition);

      TileEntity tileEntity = this.world.getTileEntity(searchPosition);

      if (tileEntity instanceof TileEntityBase) {
        String name = tileEntity.getClass().getName();

        if (joinedTableMap.containsKey(name)) {
          // this indicates two tables of the same type joined in the pseudo-multiblock
          // and we need to invalidate the structure by returning nothing
          return Collections.emptyList();
        }

        // found a table!
        joinedTableMap.put(name, (TileEntityBase) tileEntity);

        // check around this newly discovered table
        toSearchQueue.offer(tileEntity.getPos().offset(EnumFacing.NORTH));
        toSearchQueue.offer(tileEntity.getPos().offset(EnumFacing.EAST));
        toSearchQueue.offer(tileEntity.getPos().offset(EnumFacing.SOUTH));
        toSearchQueue.offer(tileEntity.getPos().offset(EnumFacing.WEST));
      }
    }

    result.addAll(joinedTableMap.values());
    //return result.size() < 2 ? Collections.emptyList() : result;
    return result;
  }

  /**
   * Searches cardinal directions around all joined tables and returns an adjacent toolbox.
   * <p>
   * If more than one toolbox is found, the first toolbox found is returned.
   * <p>
   * If no toolbox is found, null is returned.
   *
   * @return adjacent toolbox or null
   */
  @Nullable
  public TileEntityToolbox getAdjacentToolbox() {

    List<TileEntityBase> joinedTables = this.getJoinedTables(new ArrayList<>());

    for (TileEntityBase joinedTable : joinedTables) {
      BlockPos pos = joinedTable.getPos();
      TileEntity tileEntity;

      for (EnumFacing facing : EnumFacing.HORIZONTALS) {

        if ((tileEntity = this.world.getTileEntity(pos.offset(facing))) != null) {

          if (tileEntity instanceof TileEntityToolbox) {

            return (TileEntityToolbox) tileEntity;
          }
        }
      }
    }

    return null;
  }

  public RegistryRecipe getWorktableRecipeRegistry() {

    return WorktableAPI.getWorktableRecipeRegistry(this.getWorktableName());
  }

  public ItemStack getItemStackForTabDisplay(IBlockState state) {

    Block block = state.getBlock();
    Item item = Item.getItemFromBlock(block);
    return new ItemStack(item, 1, block.getMetaFromState(state));
  }

  public boolean onBlockActivated(
      World worldIn,
      BlockPos pos,
      IBlockState state,
      EntityPlayer playerIn,
      EnumHand hand,
      EnumFacing facing,
      float hitX,
      float hitY,
      float hitZ
  ) {

    if (BottleHelper.drainWaterFromBottle(this, playerIn, this.tank)) {
      return true;
    }

    if (BottleHelper.drainWaterIntoBottle(this, playerIn, this.tank)) {
      return true;
    }

    IFluidHandler fluidHandler = this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
    boolean result = false;

    if (fluidHandler != null) {
      result = FluidUtil.interactWithFluidHandler(playerIn, hand, fluidHandler);
    }

    if (result) {
      BlockHelper.notifyBlockUpdate(this.getWorld(), this.getPos());
      return true;
    }

    playerIn.openGui(ModuleWorktables.MOD_INSTANCE, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
    return true;
  }

  @Override
  public Container getContainer(
      InventoryPlayer inventoryPlayer, World world, IBlockState state, BlockPos pos
  ) {

    return new Container(inventoryPlayer, world, this);
  }

  public int getMaximumDisplayedTabCount() {

    return 6;
  }

  protected abstract String getTableTitleKey();

  protected abstract ResourceLocation getGuiBackgroundTexture();

  protected abstract ObservableStackHandler createToolHandler();

  protected abstract CraftingMatrixStackHandler createCraftingMatrixHandler();

  protected abstract ObservableStackHandler createSecondaryOutputHandler();

  protected abstract FluidTank createFluidTank(EnumType type);

  public abstract EnumTier getTier();
}
