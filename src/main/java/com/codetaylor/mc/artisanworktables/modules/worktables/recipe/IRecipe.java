package com.codetaylor.mc.artisanworktables.modules.worktables.recipe;

import com.codetaylor.mc.artisanworktables.modules.worktables.reference.EnumTier;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi.CraftingMatrixStackHandler;
import crafttweaker.api.item.IIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public interface IRecipe {

  boolean matches(
      Collection<String> unlockedStages,
      int playerExperienceTotal,
      int playerLevels,
      boolean isPlayerCreative,
      ItemStack[] tools,
      CraftingMatrixStackHandler craftingMatrix,
      FluidStack fluidStack,
      ISecondaryIngredientMatcher secondaryIngredientMatcher,
      EnumTier tier
  );

  int getWidth();

  int getHeight();

  boolean isShaped();

  boolean isMirrored();

  boolean matchGameStages(Collection<String> unlockedStages);

  ItemStack getSecondaryOutput();

  float getSecondaryOutputChance();

  ItemStack getTertiaryOutput();

  float getTertiaryOutputChance();

  ItemStack getQuaternaryOutput();

  float getQuaternaryOutputChance();

  @Nonnull
  List<IIngredient> getSecondaryIngredients();

  boolean isValidTool(ItemStack tool, int toolIndex);

  boolean hasSufficientToolDurability(ItemStack tool, int toolIndex);

  ItemStack[] getTools(int toolIndex);

  int getToolDamage(int toolIndex);

  int getToolCount();

  List<Ingredient> getIngredientList();

  @Nullable
  FluidStack getFluidIngredient();

  List<OutputWeightPair> getOutputWeightPairList();

  ItemStack selectOutput(Random random);

  ItemStack getBaseOutput();

  boolean hasMultipleWeightedOutputs();

  boolean matchTier(EnumTier tier);

  int getExperienceRequired();

  int getLevelRequired();

  boolean consumeExperience();
}
