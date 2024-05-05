package org.editorconfig.settings;

import consulo.annotation.component.ExtensionImpl;
import consulo.configurable.ConfigurationException;
import consulo.disposer.Disposable;
import consulo.language.codeStyle.CodeStyleSettings;
import consulo.language.codeStyle.ui.setting.GeneralCodeStyleOptionsProvider;
import consulo.localize.LocalizeValue;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.Label;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.DockLayout;
import consulo.ui.layout.LabeledLayout;
import consulo.ui.style.StandardColors;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Dennis.Ushakov
 */
@ExtensionImpl
public class EditorConfigConfigurable implements GeneralCodeStyleOptionsProvider {
  private CheckBox myEnabled;

  @RequiredUIAccess
  @Nullable
  @Override
  public Component createUIComponent(@Nonnull Disposable parentDisposable) {

    myEnabled = CheckBox.create(LocalizeValue.localizeTODO("Enable EditorConfig support"));

    Label warningLabel = Label.create(LocalizeValue.localizeTODO("May override the IDE code style settings"));
    warningLabel.setForegroundColor(StandardColors.GRAY);

    DockLayout layout = DockLayout.create();
    layout.left(myEnabled);
    layout.right(warningLabel);

    return LabeledLayout.create(LocalizeValue.localizeTODO("EditorConfig"), layout);
  }

  @Override
  public boolean isModified(CodeStyleSettings settings) {
    return myEnabled.getValue() != settings.getCustomSettings(EditorConfigSettings.class).ENABLED;
  }

  @Override
  public void apply(CodeStyleSettings settings) {
    settings.getCustomSettings(EditorConfigSettings.class).ENABLED = myEnabled.getValue();
  }

  @Override
  public void reset(CodeStyleSettings settings) {
    myEnabled.setValue(settings.getCustomSettings(EditorConfigSettings.class).ENABLED);
  }

  @RequiredUIAccess
  @Override
  public void disposeUIResources() {
    myEnabled = null;
  }

  @RequiredUIAccess
  @Override
  public boolean isModified() {
    return false;
  }

  @RequiredUIAccess
  @Override
  public void apply() throws ConfigurationException {
  }

  @RequiredUIAccess
  @Override
  public void reset() {
  }
}
