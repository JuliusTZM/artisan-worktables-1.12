package com.codetaylor.mc.artisanworktables.modules.requirement.gamestages.requirement;

import com.codetaylor.mc.artisanworktables.api.recipe.requirement.IRequirementBuilder;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GameStagesRequirementBuilder
    implements IRequirementBuilder<GameStagesRequirementContext, GameStagesRequirement> {

  private Set<String> excludeStages;
  private Set<String> requireOneStage;
  private Set<String> requireAllStages;

  public GameStagesRequirementBuilder() {

    this.excludeStages = Collections.emptySet();
    this.requireOneStage = Collections.emptySet();
    this.requireAllStages = Collections.emptySet();
  }

  @Nonnull
  @Override
  public String getRequirementId() {

    return GameStagesRequirement.REQUIREMENT_ID;
  }

  @Nonnull
  @Override
  public ResourceLocation getResourceLocation() {

    return GameStagesRequirement.LOCATION;
  }

  public GameStagesRequirementBuilder allOf(@Nonnull String... requireAllStages) {

    this.requireAllStages = new HashSet<>();

    for (String stage : requireAllStages) {
      this.requireAllStages.add(stage.toLowerCase());
    }
    return this;
  }

  public GameStagesRequirementBuilder anyOf(@Nonnull String... requireOneStage) {

    this.requireOneStage = new HashSet<>();

    for (String stage : requireOneStage) {
      this.requireOneStage.add(stage.toLowerCase());
    }
    return this;
  }

  public GameStagesRequirementBuilder exclude(@Nonnull String... excludeStages) {

    this.excludeStages = new HashSet<>();

    for (String stage : excludeStages) {
      this.excludeStages.add(stage.toLowerCase());
    }
    return this;
  }

  @Override
  public GameStagesRequirement create() {

    return new GameStagesRequirement(this.requireAllStages, this.requireOneStage, this.excludeStages);
  }
}
