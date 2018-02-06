package com.codetaylor.mc.artisanworktables.modules.worktables.gui;

import com.codetaylor.mc.artisanworktables.modules.toolbox.tile.TileEntityToolbox;
import com.codetaylor.mc.artisanworktables.modules.worktables.gui.slot.*;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.IRecipeWorktable;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.RegistryRecipeWorktable;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi.CraftingMatrixStackHandler;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi.TileEntityBase;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi.TileEntityFluidBase;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi.TileEntityWorkstationBase;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.workstation.TileEntityWorkstationScribe;
import com.codetaylor.mc.athenaeum.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class ContainerWorktable
    extends ContainerBase {

  private final CraftingResultSlot craftingResultSlot;
  private World world;
  private TileEntityBase tile;
  private TileEntityToolbox toolbox;
  private final ItemStackHandler resultHandler;
  private FluidStack lastFluidStack;
  private final EntityPlayer player;

  private final int slotIndexResult;
  private final int slotIndexCraftingMatrixStart;
  private final int slotIndexCraftingMatrixEnd;
  private final int slotIndexInventoryStart;
  private final int slotIndexInventoryEnd;
  private final int slotIndexHotbarStart;
  private final int slotIndexHotbarEnd;
  private final int slotIndexToolsStart;
  private final int slotIndexToolsEnd;
  private final int slotIndexSecondaryOutputStart;
  private final int slotIndexSecondaryOutputEnd;
  private final int slotIndexToolboxStart;
  private final int slotIndexToolboxEnd;

  public ContainerWorktable(
      InventoryPlayer playerInventory,
      World world,
      TileEntityBase tile
  ) {

    super(playerInventory);

    this.world = world;
    this.tile = tile;
    this.toolbox = this.getToolbox(this.tile);

    this.player = playerInventory.player;
    Runnable slotChangeListener = () -> this.updateRecipeOutput(this.player);

    // ------------------------------------------------------------------------
    // Result
    this.slotIndexResult = this.nextSlotIndex;
    this.resultHandler = new ItemStackHandler(1);
    this.craftingResultSlot = new CraftingResultSlot(
        slotChangeListener,
        this.tile,
        resultHandler,
        0,
        115,
        35
    );
    this.containerSlotAdd(this.craftingResultSlot);

    // ------------------------------------------------------------------------
    // Crafting Matrix
    CraftingMatrixStackHandler craftingMatrixHandler = this.tile.getCraftingMatrixHandler();
    this.slotIndexCraftingMatrixStart = this.nextSlotIndex;

    for (int y = 0; y < craftingMatrixHandler.getHeight(); ++y) {
      for (int x = 0; x < craftingMatrixHandler.getWidth(); ++x) {
        this.containerSlotAdd(new CraftingIngredientSlot(
            slotChangeListener,
            craftingMatrixHandler,
            x + y * 3,
            20 + x * 18,
            17 + y * 18
        ));
      }
    }
    this.slotIndexCraftingMatrixEnd = this.nextSlotIndex - 1;

    // ------------------------------------------------------------------------
    // Player Inventory
    this.slotIndexInventoryStart = this.nextSlotIndex;
    this.containerPlayerInventoryAdd();
    this.slotIndexInventoryEnd = this.nextSlotIndex - 1;

    // ------------------------------------------------------------------------
    // Player HotBar
    this.slotIndexHotbarStart = this.nextSlotIndex;
    this.containerPlayerHotbarAdd();
    this.slotIndexHotbarEnd = this.nextSlotIndex - 1;

    // ------------------------------------------------------------------------
    // Tool Slots
    {
      this.slotIndexToolsStart = this.nextSlotIndex;
      ItemStackHandler toolHandler = this.tile.getToolHandler();

      int offsetY = 0;

      if (this.tile instanceof TileEntityWorkstationBase) {
        offsetY = -11;
      }

      for (int i = 0; i < toolHandler.getSlots(); i++) {
        final int slotIndex = i;
        this.containerSlotAdd(new CraftingToolSlot(
            slotChangeListener,
            itemStack -> this.tile.getWorktableRecipeRegistry().containsRecipeWithToolInSlot(itemStack, slotIndex),
            toolHandler,
            i,
            78,
            35 + 22 * i + offsetY
        ));
      }
      this.slotIndexToolsEnd = this.nextSlotIndex - 1;
    }

    // ------------------------------------------------------------------------
    // Secondary output
    this.slotIndexSecondaryOutputStart = this.nextSlotIndex;
    for (int i = 0; i < 3; i++) {
      this.containerSlotAdd(new ResultSlot(this.tile.getSecondaryOutputHandler(), i, 152, 17 + i * 18));
    }
    this.slotIndexSecondaryOutputEnd = this.nextSlotIndex - 1;

    // ------------------------------------------------------------------------
    // Secondary input
    // TODO

    // ------------------------------------------------------------------------
    // Side Toolbox
    this.slotIndexToolboxStart = this.nextSlotIndex;
    if (this.toolbox != null && !this.toolbox.isInvalid()) {
      ItemStackHandler itemHandler = this.toolbox.getItemHandler();

      for (int x = 0; x < 3; x++) {

        for (int y = 0; y < 9; y++) {
          this.containerSlotAdd(new ToolboxSlot(
              this.toolbox,
              itemHandler,
              y + x * 9,
              x * -18 - 26,
              y * 18 + 8
          ));
        }
      }
    }
    this.slotIndexToolboxEnd = this.nextSlotIndex - 1;

    this.updateRecipeOutput(this.player);
  }

  @Override
  protected int containerHotbarPositionGetY() {

    if (this.tile instanceof TileEntityWorkstationBase) {
      return super.containerHotbarPositionGetY() + 23;
    }

    return super.containerHotbarPositionGetY();
  }

  @Override
  protected int containerInventoryPositionGetY() {

    if (this.tile instanceof TileEntityWorkstationBase) {
      return super.containerInventoryPositionGetY() + 23;
    }

    return super.containerInventoryPositionGetY();
  }

  @Override
  protected int containerInventoryPositionGetX() {

    return super.containerInventoryPositionGetX();
  }

  @Override
  protected int containerHotbarPositionGetX() {

    return super.containerHotbarPositionGetX();
  }

  private TileEntityToolbox getToolbox(TileEntityBase tile) {

    return tile.getAdjacentToolbox();
  }

  public TileEntityToolbox getToolbox() {

    return this.toolbox;
  }

  private void updateRecipeOutput(EntityPlayer player) {

    FluidStack fluidStack = null;

    if (this.tile instanceof TileEntityFluidBase) {
      fluidStack = ((TileEntityFluidBase) this.tile).getTank().getFluid();

      if (fluidStack != null) {
        fluidStack = fluidStack.copy();
      }
    }

    RegistryRecipeWorktable registry = this.tile.getWorktableRecipeRegistry();
    IRecipeWorktable recipe = registry.findRecipe(
        player,
        this.tile.getTools(),
        this.tile.getCraftingMatrixHandler(),
        fluidStack,
        this.tile.getSecondaryIngredientMatcher()
    );

    if (recipe != null) {
      this.resultHandler.setStackInSlot(0, recipe.getBaseOutput());

    } else {
      this.resultHandler.setStackInSlot(0, ItemStack.EMPTY);
    }
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {

    //
  }

  @Override
  public boolean canInteractWith(EntityPlayer playerIn) {

    return this.tile.canPlayerUse(playerIn);
  }

  private boolean swapItemStack(int originSlotIndex, int targetSlotIndex) {

    Slot originSlot = this.inventorySlots.get(originSlotIndex);
    Slot targetSlot = this.inventorySlots.get(targetSlotIndex);

    ItemStack originStack = originSlot.getStack();
    ItemStack targetStack = targetSlot.getStack();

    if (originStack.isItemEqual(targetStack)) {
      return true;
    }

    if (!originStack.isEmpty()
        && targetSlot.isItemValid(originStack)) {

      if (targetStack.isEmpty()) {
        targetSlot.putStack(originStack);
        originSlot.putStack(ItemStack.EMPTY);

      } else {
        targetSlot.putStack(originStack);
        originSlot.putStack(targetStack);
      }

      return true;
    }

    return false;
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slotIn) {

    return slotIn != this.craftingResultSlot && super.canMergeSlot(stack, slotIn);
  }

  private boolean isSlotIndexResult(int slotIndex) {

    return slotIndex == this.slotIndexResult;
  }

  private boolean isSlotIndexInventory(int slotIndex) {

    return slotIndex >= this.slotIndexInventoryStart && slotIndex <= this.slotIndexInventoryEnd;
  }

  private boolean isSlotIndexHotbar(int slotIndex) {

    return slotIndex >= this.slotIndexHotbarStart && slotIndex <= this.slotIndexHotbarEnd;
  }

  private boolean isSlotIndexToolbox(int slotIndex) {

    return slotIndex >= this.slotIndexToolboxStart && slotIndex <= this.slotIndexToolboxEnd;
  }

  private boolean isSlotIndexTool(int slotIndex) {

    return slotIndex >= this.slotIndexToolsStart && slotIndex <= this.slotIndexToolsEnd;
  }

  private boolean mergeInventory(ItemStack itemStack, boolean reverse) {

    return this.mergeItemStack(itemStack, this.slotIndexInventoryStart, this.slotIndexInventoryEnd + 1, reverse);
  }

  private boolean mergeHotbar(ItemStack itemStack, boolean reverse) {

    return this.mergeItemStack(itemStack, this.slotIndexHotbarStart, this.slotIndexHotbarEnd + 1, reverse);
  }

  private boolean mergeCraftingMatrix(ItemStack itemStack, boolean reverse) {

    return this.mergeItemStack(
        itemStack,
        this.slotIndexCraftingMatrixStart,
        this.slotIndexCraftingMatrixEnd + 1,
        reverse
    );
  }

  private boolean mergeToolbox(ItemStack itemStack, boolean reverse) {

    return this.mergeItemStack(itemStack, this.slotIndexToolboxStart, this.slotIndexToolboxEnd + 1, reverse);
  }

  private boolean swapTools(int slotIndex) {

    for (int i = this.slotIndexToolsStart; i <= this.slotIndexToolsEnd; i++) {

      if (this.swapItemStack(slotIndex, i)) {
        return true; // Swapped tools
      }
    }

    return false;
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex) {

    ItemStack itemStackCopy = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(slotIndex);

    if (slot != null && slot.getHasStack()) {
      ItemStack itemStack = slot.getStack();
      itemStackCopy = itemStack.copy();

      if (this.isSlotIndexResult(slotIndex)) {
        // Result

        // This is executed on both the client and server for each craft. If the crafting
        // grid has multiple, complete recipes, this will be executed for each complete
        // recipe.

        IRecipeWorktable recipe = this.tile.getRecipe(playerIn);

        if (recipe == null) {
          return ItemStack.EMPTY;
        }

        if (recipe.hasMultipleWeightedOutputs()) {
          // Restrict the player from shift-clicking items out of the result slot when the recipe has
          // multiple weighted outputs. This allows us to replace the item the player picks up after
          // it has been retrieved.
          return ItemStack.EMPTY;
        }

        if (!this.mergeInventory(itemStack, false)
            && !this.mergeHotbar(itemStack, false)) {
          // Can't merge the craft result into any inventory or hotbar slot.
          return ItemStack.EMPTY;
        }

        itemStack.getItem().onCreated(itemStack, this.world, playerIn);
        slot.onSlotChange(itemStack, itemStackCopy);

      } else if (this.isSlotIndexInventory(slotIndex)) {
        // Inventory clicked, try to move to tool slot first, then crafting matrix, then hotbar

        if (this.swapTools(slotIndex)) {
          return ItemStack.EMPTY; // swapped tools
        }

        if (!this.mergeCraftingMatrix(itemStack, false)
            && !this.mergeHotbar(itemStack, false)) {
          return ItemStack.EMPTY;
        }

      } else if (this.isSlotIndexHotbar(slotIndex)) {
        // HotBar clicked, try to move to tool slot first, then crafting matrix, then inventory

        if (this.swapTools(slotIndex)) {
          return ItemStack.EMPTY; // swapped tools
        }

        if (!this.mergeCraftingMatrix(itemStack, false)
            && !this.mergeInventory(itemStack, false)) {
          return ItemStack.EMPTY;
        }

      } else if (this.isSlotIndexToolbox(slotIndex)) {
        // Toolbox clicked, try to move to tool slot first, then crafting matrix, then inventory

        if (this.swapTools(slotIndex)) {
          return ItemStack.EMPTY; // swapped tools
        }

        if (!this.mergeCraftingMatrix(itemStack, false)
            && !this.mergeInventory(itemStack, false)) {
          return ItemStack.EMPTY;
        }

      } else if (this.isSlotIndexTool(slotIndex)) {
        // Tool slot clicked, try to move to toolbox first, then player inventory

        if (this.toolbox != null) {

          if (!this.mergeToolbox(itemStack, false)
              && !this.mergeInventory(itemStack, false)) {
            return ItemStack.EMPTY;
          }

        } else if (!this.mergeInventory(itemStack, false)
            && !this.mergeHotbar(itemStack, false)) {
          return ItemStack.EMPTY;
        }

      } else if (!this.mergeInventory(itemStack, false)
          && !this.mergeHotbar(itemStack, false)) {
        // All others: crafting matrix
        return ItemStack.EMPTY;
      }

      if (itemStack.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);

      } else {
        slot.onSlotChanged();
      }

      if (itemStack.getCount() == itemStackCopy.getCount()) {
        return ItemStack.EMPTY;
      }

      ItemStack itemStack2 = slot.onTake(playerIn, itemStack);

      if (slotIndex == 0) {
        playerIn.dropItem(itemStack2, false);
      }
    }

    return itemStackCopy;
  }

  public List<Slot> getRecipeSlots(List<Slot> result) {

    // grid
    for (int i = 1; i < 10; i++) {
      result.add(this.inventorySlots.get(i));
    }

    // tool
    result.add(this.inventorySlots.get(46));

    return result;
  }

  public List<Slot> getInventorySlots(List<Slot> result) {

    for (int i = 10; i < 46; i++) {
      result.add(this.inventorySlots.get(i));
    }

    if (this.toolbox != null) {

      for (int i = 50; i < 77; i++) {
        result.add(this.inventorySlots.get(i));
      }
    }

    return result;
  }

  public TileEntityBase getTile() {

    return this.tile;
  }

  public boolean canHandleJEIRecipeTransfer(String name) {

    return this.tile.canHandleJEIRecipeTransfer(name);
  }

  @Override
  public void detectAndSendChanges() {

    super.detectAndSendChanges();

    if (!(this.tile instanceof TileEntityFluidBase)
        || this.tile.getWorld().isRemote) {
      return;
    }

    FluidTank tank = ((TileEntityFluidBase) this.tile).getTank();
    FluidStack fluidStack = tank.getFluid();

    if (this.lastFluidStack != null
        && fluidStack == null) {
      this.lastFluidStack = null;
      this.updateRecipeOutput(this.player);

    } else if (this.lastFluidStack == null
        && fluidStack != null) {
      this.lastFluidStack = fluidStack.copy();
      this.updateRecipeOutput(this.player);

    } else if (this.lastFluidStack != null) {

      if (!this.lastFluidStack.isFluidStackIdentical(fluidStack)) {
        this.lastFluidStack = fluidStack.copy();
        this.updateRecipeOutput(this.player);
      }
    }
  }
}
