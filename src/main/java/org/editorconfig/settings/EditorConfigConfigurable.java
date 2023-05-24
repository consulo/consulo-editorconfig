package org.editorconfig.settings;

import consulo.annotation.component.ExtensionImpl;
import consulo.configurable.ConfigurationException;
import consulo.language.codeStyle.CodeStyleSettings;
import consulo.language.codeStyle.ui.setting.GeneralCodeStyleOptionsProvider;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.awt.IdeBorderFactory;
import consulo.ui.ex.awt.JBCheckBox;
import consulo.ui.ex.awt.UIUtil;
import consulo.ui.ex.awt.VerticalFlowLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Dennis.Ushakov
 */
@ExtensionImpl
public class EditorConfigConfigurable implements GeneralCodeStyleOptionsProvider
{
	private JBCheckBox myEnabled;

	@RequiredUIAccess
	@Nullable
	@Override
	public JComponent createComponent()
	{
		myEnabled = new JBCheckBox("Enable EditorConfig support");
		final JPanel panel = new JPanel(new VerticalFlowLayout());
		panel.setBorder(IdeBorderFactory.createTitledBorder("EditorConfig", false));
		panel.add(myEnabled);
		final JLabel warning = new JLabel("EditorConfig may override the IDE code style settings");
		warning.setFont(UIUtil.getLabelFont(UIUtil.FontSize.SMALL));
		warning.setBorder(IdeBorderFactory.createEmptyBorder(0, 20, 0, 0));
		panel.add(warning);
		return panel;
	}

	@Override
	public boolean isModified(CodeStyleSettings settings)
	{
		return myEnabled.isSelected() != settings.getCustomSettings(EditorConfigSettings.class).ENABLED;
	}

	@Override
	public void apply(CodeStyleSettings settings)
	{
		settings.getCustomSettings(EditorConfigSettings.class).ENABLED = myEnabled.isSelected();
	}

	@Override
	public void reset(CodeStyleSettings settings)
	{
		myEnabled.setSelected(settings.getCustomSettings(EditorConfigSettings.class).ENABLED);
	}

	@RequiredUIAccess
	@Override
	public void disposeUIResources()
	{
		myEnabled = null;
	}

	@RequiredUIAccess
	@Override
	public boolean isModified()
	{
		return false;
	}

	@RequiredUIAccess
	@Override
	public void apply() throws ConfigurationException
	{
	}

	@RequiredUIAccess
	@Override
	public void reset()
	{
	}
}
