package com.codetaylor.mc.artisanworktables.modules.requirement.gamestages.requirement;

import com.codetaylor.mc.artisanworktables.api.recipe.requirement.IMatchRequirement;
import com.codetaylor.mc.artisanworktables.modules.worktables.ModuleWorktables;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameStagesMatchRequirement
    implements IMatchRequirement<GameStagesMatchRequirementContext> {

  public static final String MOD_ID = "gamestages";
  public static final ResourceLocation LOCATION = new ResourceLocation(ModuleWorktables.MOD_ID, MOD_ID);

  private final List<String> requireAllStages;
  private final List<String> requireOneStage;
  private final List<String> excludeStages;

  /* package */ GameStagesMatchRequirement(
      @Nonnull Collection<String> requireAllStages,
      @Nonnull Collection<String> requireOneStage,
      @Nonnull Collection<String> excludeStages
  ) {

    this.requireAllStages = new ArrayList<>(requireAllStages);
    this.requireOneStage = new ArrayList<>(requireOneStage);
    this.excludeStages = new ArrayList<>(excludeStages);
  }

  @Override
  public ResourceLocation getResourceLocation() {

    return LOCATION;
  }

  @Override
  public boolean match(GameStagesMatchRequirementContext context) {

    Collection<String> unlockedStages = context.getUnlockedStages();

    // If the player has unlocked any excluded stage, fail.
    for (String excludedStage : this.excludeStages) {

      if (unlockedStages.contains(excludedStage)) {
        return false;
      }
    }

    // If the player hasn't unlocked all required stages, fail.
    if (!this.requireAllStages.isEmpty()
        && !unlockedStages.containsAll(this.requireAllStages)) {
      return false;
    }

    // If the player has unlocked any of the required stages, success!
    for (String oneStage : this.requireOneStage) {

      if (unlockedStages.contains(oneStage)) {
        return true;
      }
    }

    // Success only if there are no required stages in the 'any' list.
    return this.requireOneStage.isEmpty();
  }
}
