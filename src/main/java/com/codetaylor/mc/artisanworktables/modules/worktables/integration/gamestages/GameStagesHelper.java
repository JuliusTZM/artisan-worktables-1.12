package com.codetaylor.mc.artisanworktables.modules.worktables.integration.gamestages;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;

public class GameStagesHelper {

  public static Collection<String> getUnlockedStages(EntityPlayer player) {

    return GameStageHelper.getPlayerData(player).getStages();
  }

  private GameStagesHelper() {
    //
  }

}
