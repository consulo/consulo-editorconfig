package org.editorconfig.settings;

import consulo.language.codeStyle.CodeStyleSettings;
import consulo.language.codeStyle.CustomCodeStyleSettings;

/**
 * @author Dennis.Ushakov
 */
public class EditorConfigSettings extends CustomCodeStyleSettings {
  public boolean ENABLED = true;

  public EditorConfigSettings(CodeStyleSettings container) {
    super("editorconfig", container);
  }
}
