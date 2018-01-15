package com.codetaylor.mc.artisanworktables.modules.worktables.integration.jei;

import com.codetaylor.mc.artisanworktables.modules.worktables.ModuleWorktables;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.OutputWeightPair;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.RecipeWorktable;
import com.codetaylor.mc.athenaeum.gui.GuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JEIRecipeWrapperWorktable
    implements IRecipeWrapper {

  private static final ResourceLocation RECIPE_BACKGROUND = new ResourceLocation(
      ModuleWorktables.MOD_ID,
      "textures/gui/recipe_background.png"
  );

  private RecipeWorktable recipe;
  private List<List<ItemStack>> inputs;
  private List<ItemStack> tools;
  private List<ItemStack> output;
  private ItemStack secondaryOutput;
  private ItemStack tertiaryOutput;
  private ItemStack quaternaryOutput;

  public JEIRecipeWrapperWorktable(
      RecipeWorktable recipe
  ) {

    this.recipe = recipe;
    this.inputs = new ArrayList<>();
    this.tools = new ArrayList<>();

    for (Ingredient input : this.recipe.getIngredients()) {
      this.inputs.add(Arrays.asList(input.getMatchingStacks()));
    }

    this.tools = Arrays.asList(this.recipe.getTools());

    List<OutputWeightPair> output = this.recipe.getOutput();
    this.output = new ArrayList<>(output.size());

    for (OutputWeightPair pair : output) {
      this.output.add(pair.getOutput());
    }

    this.secondaryOutput = recipe.getSecondaryOutput();
    this.tertiaryOutput = recipe.getTertiaryOutput();
    this.quaternaryOutput = recipe.getQuaternaryOutput();
  }

  public boolean isShaped() {

    return this.recipe.isShaped();
  }

  public int getWidth() {

    return this.recipe.getWidth();
  }

  public int getHeight() {

    return this.recipe.getHeight();
  }

  public List<ItemStack> getTools() {

    return this.tools;
  }

  public ItemStack getSecondaryOutput() {

    return this.secondaryOutput;
  }

  public ItemStack getTertiaryOutput() {

    return this.tertiaryOutput;
  }

  public ItemStack getQuaternaryOutput() {

    return this.quaternaryOutput;
  }

  @Override
  public void getIngredients(IIngredients ingredients) {

    ingredients.setInputLists(ItemStack.class, this.inputs);

    List<ItemStack> output = new ArrayList<>();
    output.addAll(this.output);
    output.add(this.secondaryOutput);
    output.add(this.tertiaryOutput);
    output.add(this.quaternaryOutput);
    ingredients.setOutputs(ItemStack.class, output);
  }

  @Override
  public void drawInfo(
      Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY
  ) {

    String label = "-" + this.recipe.getToolDamage();
    minecraft.fontRenderer.drawString(
        label,
        (80 - 3) - minecraft.fontRenderer.getStringWidth(label) * 0.5f,
        (55 - 3),
        0xFFFFFFFF,
        true
    );

    GlStateManager.pushMatrix();
    GlStateManager.scale(0.5, 0.5, 1);
    int xPos = 334;

    if (!this.recipe.getSecondaryOutput().isEmpty()) {
      label = (int) (this.recipe.getSecondaryOutputChance() * 100) + "%";
      minecraft.fontRenderer.drawString(
          label,
          (xPos - 3) - minecraft.fontRenderer.getStringWidth(label) * 0.5f,
          (35 - 3),
          0xFFFFFFFF,
          true
      );
    }

    if (!this.recipe.getTertiaryOutput().isEmpty()) {
      label = (int) (this.recipe.getTertiaryOutputChance() * 100) + "%";
      minecraft.fontRenderer.drawString(
          label,
          (xPos - 3) - minecraft.fontRenderer.getStringWidth(label) * 0.5f,
          (35 - 3 + 36),
          0xFFFFFFFF,
          true
      );
    }

    if (!this.recipe.getQuaternaryOutput().isEmpty()) {
      label = (int) (this.recipe.getQuaternaryOutputChance() * 100) + "%";
      minecraft.fontRenderer.drawString(
          label,
          (xPos - 3) - minecraft.fontRenderer.getStringWidth(label) * 0.5f,
          (35 - 3 + 72),
          0xFFFFFFFF,
          true
      );
    }

    if (!this.recipe.isShaped()) {
      GuiHelper.drawTexturedRect(minecraft, RECIPE_BACKGROUND, 221, 8, 18, 17, 100, 0, 0, 1, 1);
    }

    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.translate(0, -8, 0);

    if (!this.recipe.isShaped()) {
      if (mouseX >= 110 && mouseX <= 110 + 9 && mouseY >= 4 && mouseY <= 4 + 9) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format(ModuleWorktables.Lang.JEI_TOOLTIP_SHAPELESS_RECIPE));
        GuiUtils.drawHoveringText(
            tooltip,
            110,
            4,
            minecraft.displayWidth,
            minecraft.displayHeight,
            200,
            minecraft.fontRenderer
        );
      }
    }
    GlStateManager.popMatrix();
  }
}
