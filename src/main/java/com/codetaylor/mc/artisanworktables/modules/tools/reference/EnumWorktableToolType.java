package com.codetaylor.mc.artisanworktables.modules.tools.reference;

public enum EnumWorktableToolType {

  BLACKSMITHS_CUTTERS("blacksmiths_cutters"),
  BLACKSMITHS_HAMMER("blacksmiths_hammer"),
  CARPENTERS_HAMMER("carpenters_hammer"),
  CARPENTERS_HANDSAW("carpenters_handsaw"),
  CHEMISTS_BEAKER("chemists_beaker"),
  CHEMISTS_BURNER("chemists_burner"),
  ENGINEERS_DRIVER("engineers_driver"),
  ENGINEERS_SPANNER("engineers_spanner"),
  JEWELERS_GEMCUTTER("jewelers_gemcutter"),
  JEWELERS_PLIERS("jewelers_pliers"),
  MAGES_ATHAME("mages_athame"),
  MAGES_GRIMOIRE("mages_grimoire"),
  MASONS_CHISEL("masons_chisel"),
  MASONS_TROWEL("masons_trowel"),
  SCRIBES_COMPASS("scribes_compass"),
  SCRIBES_QUILL("scribes_quill"),
  TAILORS_NEEDLE("tailors_needle"),
  TAILORS_SHEARS("tailors_shears");

  private String name;

  EnumWorktableToolType(String name) {

    this.name = name;
  }

  public String getName() {

    return this.name;
  }
}
