package org.editorconfig.configmanagement;

import consulo.annotation.component.ExtensionImpl;
import consulo.configurable.Configurable;
import consulo.language.codeStyle.CodeStyleSettings;
import consulo.language.codeStyle.CustomCodeStyleSettings;
import consulo.language.codeStyle.setting.CodeStyleSettingsProvider;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.editorconfig.settings.EditorConfigSettings;

/**
 * @author VISTALL
 * @since 25/05/2023
 */
@ExtensionImpl
public class EditorConfigCodeStyleSettingsProvider extends CodeStyleSettingsProvider
{
	@Nullable
	@Override
	public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings)
	{
		return new EditorConfigSettings(settings);
	}

	@Override
	public boolean hasSettingsPage()
	{
		return false;
	}

	@Nonnull
	@Override
	public Configurable createSettingsPage(CodeStyleSettings codeStyleSettings, CodeStyleSettings codeStyleSettings1)
	{
		throw new UnsupportedOperationException();
	}
}
