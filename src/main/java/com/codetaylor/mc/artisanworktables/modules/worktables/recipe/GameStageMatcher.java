package com.codetaylor.mc.artisanworktables.modules.worktables.recipe;

import java.util.Collection;

public class GameStageMatcher
    implements IGameStageMatcher {

  private EnumGameStageRequire require;
  private Collection<String> include;
  private Collection<String> exclude;

  public GameStageMatcher(
      EnumGameStageRequire require,
      Collection<String> include,
      Collection<String> exclude
  ) {

    this.require = require;
    this.include = include;
    this.exclude = exclude;
  }

  @Override
  public boolean matches(Collection<String> unlockedStages) {

    for (String excludedStage : this.exclude) {

      if (unlockedStages.contains(excludedStage)) {
        return false;
      }
    }

    if (this.require == EnumGameStageRequire.ALL) {
      return this.include.containsAll(unlockedStages);

    } else {

      for (String includedStage : this.include) {

        if (unlockedStages.contains(includedStage)) {
          return true;
        }
      }

      return false;
    }
  }

}
