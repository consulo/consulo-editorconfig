package org.editorconfig.configmanagement;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.codeStyle.CodeStyleSettings;
import consulo.language.codeStyle.CommonCodeStyleSettings;
import consulo.language.codeStyle.FileIndentOptionsProvider;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.editorconfig.util.Utils;
import org.editorconfig.core.EditorConfig;
import org.editorconfig.plugincomponents.SettingsProviderComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Dennis.Ushakov
 */
@ExtensionImpl
public class EditorConfigIndentOptionsProvider extends FileIndentOptionsProvider {
  // Handles the following EditorConfig settings:
  private static final String indentSizeKey = "indent_size";
  private static final String continuationSizeKey = "continuation_indent_size";
  private static final String tabWidthKey = "tab_width";
  private static final String indentStyleKey = "indent_style";

  @Nullable
  @Override
  public CommonCodeStyleSettings.IndentOptions getIndentOptions(@NotNull CodeStyleSettings settings, @NotNull PsiFile psiFile) {
    final VirtualFile file = psiFile.getVirtualFile();
    if (file == null) return null;

    final Project project = psiFile.getProject();
    if (!Utils.isEnabled(settings)) return null;

    // Get editorconfig settings
    final String filePath = Utils.getFilePath(project, file);
    final SettingsProviderComponent settingsProvider = SettingsProviderComponent.getInstance();
    final List<EditorConfig.OutPair> outPairs = settingsProvider.getOutPairs(project, filePath);
    // Apply editorconfig settings for the current editor
    return applyCodeStyleSettings(project, outPairs, file);
  }

  private static CommonCodeStyleSettings.IndentOptions applyCodeStyleSettings(Project project,
                                                                              final List<EditorConfig.OutPair> outPairs,
                                                                              final VirtualFile file) {
    // Apply indent options
    final String indentSize = Utils.configValueForKey(outPairs, indentSizeKey);
    final String continuationIndentSize = Utils.configValueForKey(outPairs, continuationSizeKey);
    final String tabWidth = Utils.configValueForKey(outPairs, tabWidthKey);
    final String indentStyle = Utils.configValueForKey(outPairs, indentStyleKey);
    final CommonCodeStyleSettings.IndentOptions indentOptions = new CommonCodeStyleSettings.IndentOptions();
    if (applyIndentOptions(project, indentOptions, indentSize, continuationIndentSize, tabWidth, indentStyle, file.getCanonicalPath())) {
      return indentOptions;
    }
    return null;
  }

  private static boolean applyIndentOptions(Project project, CommonCodeStyleSettings.IndentOptions indentOptions,
                                            String indentSize, String continuationIndentSize, String tabWidth,
                                            String indentStyle, String filePath) {
    boolean changed = false;
    final String calculatedIndentSize = calculateIndentSize(tabWidth, indentSize);
    final String calculatedContinuationSize = calculateContinuationIndentSize(calculatedIndentSize, continuationIndentSize);
    final String calculatedTabWidth = calculateTabWidth(tabWidth, indentSize);
    if (!calculatedIndentSize.isEmpty()) {
      if (applyIndentSize(indentOptions, calculatedIndentSize)) {
        Utils.appliedConfigMessage(project, calculatedIndentSize, indentSizeKey, filePath);
        changed = true;
      } else {
        Utils.invalidConfigMessage(project, calculatedIndentSize, indentSizeKey, filePath);
      }
    }
    if (!calculatedContinuationSize.isEmpty()) {
      if (applyContinuationIndentSize(indentOptions, calculatedContinuationSize)) {
        Utils.appliedConfigMessage(project, calculatedContinuationSize, continuationSizeKey, filePath);
        changed = true;
      }
      else {
        Utils.invalidConfigMessage(project, calculatedIndentSize, indentSizeKey, filePath);
      }
    }
    if (!calculatedTabWidth.isEmpty()) {
      if (applyTabWidth(indentOptions, calculatedTabWidth)) {
        Utils.appliedConfigMessage(project, calculatedTabWidth, tabWidthKey, filePath);
        changed = true;
      }
      else {
        Utils.invalidConfigMessage(project, calculatedTabWidth, tabWidthKey, filePath);
      }
    }
    if (!indentStyle.isEmpty()) {
      if (applyIndentStyle(indentOptions, indentStyle)) {
        Utils.appliedConfigMessage(project, indentStyle, indentStyleKey, filePath);
        changed = true;
      }
      else {
        Utils.invalidConfigMessage(project, indentStyle, indentStyleKey, filePath);
      }
    }
    return changed;
  }

  private static String calculateIndentSize(final String tabWidth, final String indentSize) {
    return indentSize.equals("tab") ? tabWidth : indentSize;
  }

  private static String calculateContinuationIndentSize(final String indentSize, final String continuationIndentSize) {
    return continuationIndentSize.isEmpty() ? indentSize : continuationIndentSize;
  }

  private static String calculateTabWidth(final String tabWidth, final String indentSize) {
    if (tabWidth.isEmpty() && indentSize.equals("tab")) {
      return "";
    }
    else if (tabWidth.isEmpty()) {
      return indentSize;
    }
    else {
      return tabWidth;
    }
  }

  private static boolean applyIndentSize(final CommonCodeStyleSettings.IndentOptions indentOptions, final String indentSize) {
    try {
      indentOptions.INDENT_SIZE = Integer.parseInt(indentSize);
      return true;
    }
    catch (NumberFormatException e) {
      return false;
    }
  }

  private static boolean applyContinuationIndentSize(final CommonCodeStyleSettings.IndentOptions indentOptions, final String continuationIndentSize) {
    try {
      indentOptions.CONTINUATION_INDENT_SIZE = Integer.parseInt(continuationIndentSize);
      return true;
    }
    catch (NumberFormatException e) {
      return false;
    }
  }
  private static boolean applyTabWidth(final CommonCodeStyleSettings.IndentOptions indentOptions, final String tabWidth) {
    try {
      indentOptions.TAB_SIZE = Integer.parseInt(tabWidth);
      return true;
    }
    catch (NumberFormatException e) {
      return false;
    }
  }

  private static boolean applyIndentStyle(CommonCodeStyleSettings.IndentOptions indentOptions, String indentStyle) {
    if (indentStyle.equals("tab") || indentStyle.equals("space")) {
      indentOptions.USE_TAB_CHARACTER = indentStyle.equals("tab");
      return true;
    }
    return false;
  }
}
