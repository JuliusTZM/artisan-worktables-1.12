package com.codetaylor.mc.artisanworktables.modules.worktables.tile.workstation;

import com.codetaylor.mc.artisanworktables.modules.worktables.block.EnumType;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.TileEntityMageDelegate;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi.TileEntityWorkstationBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;

public class TileEntityWorkstationMage
    extends TileEntityWorkstationBase
    implements ITickable {

  private final TileEntityMageDelegate delegate;

  public TileEntityWorkstationMage() {

    super(EnumType.MAGE);
    this.delegate = new TileEntityMageDelegate(this);
  }

  @Override
  public void update() {

    this.delegate.update();
  }

  @Override
  public void onDataPacket(
      NetworkManager manager, SPacketUpdateTileEntity packet
  ) {

    super.onDataPacket(manager, packet);
    this.delegate.onDataPacket(manager, packet);
  }

  @Override
  public ItemStack getItemStackForTabDisplay(IBlockState state) {

    return this.delegate.getItemStackForTabDisplay(state);
  }
}
