package com.codetaylor.mc.artisanworktables.modules.worktables.gui.slot;

import com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi.TileEntityBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class CraftingResultSlot
    extends ResultSlot {

  private final Runnable slotChangeListener;
  private final TileEntityBase tile;

  public CraftingResultSlot(
      Runnable slotChangeListener,
      TileEntityBase tile,
      IItemHandler itemHandler,
      int index,
      int xPosition,
      int yPosition
  ) {

    super(itemHandler, index, xPosition, yPosition);
    this.tile = tile;
    this.slotChangeListener = slotChangeListener;
  }

  @Override
  public ItemStack onTake(
      EntityPlayer player, ItemStack stack
  ) {

    this.tile.onTakeResult(player);
    this.slotChangeListener.run();
    return stack;
  }
}
