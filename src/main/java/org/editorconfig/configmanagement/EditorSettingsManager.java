package org.editorconfig.configmanagement;

import consulo.codeEditor.OverrideEditorFileKeys;
import consulo.document.Document;
import consulo.document.FileDocumentManager;
import consulo.document.event.FileDocumentManagerAdapter;
import consulo.language.codeStyle.CodeStyleSettingsManager;
import consulo.project.Project;
import consulo.util.dataholder.Key;
import consulo.virtualFileSystem.VirtualFile;
import org.editorconfig.core.EditorConfig;
import org.editorconfig.plugincomponents.SettingsProviderComponent;
import org.editorconfig.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorSettingsManager extends FileDocumentManagerAdapter {
  // Handles the following EditorConfig settings:
  private static final String trimTrailingWhitespaceKey = "trim_trailing_whitespace";
  private static final String insertFinalNewlineKey = "insert_final_newline";
  private static final Map<String, String> trimMap;

  // TODO replace it later by enum
  public static final String STRIP_TRAILING_SPACES_NONE = "None";
  public static final String STRIP_TRAILING_SPACES_WHOLE = "Whole";

  static {
    Map<String, String> map = new HashMap<String, String>();
    map.put("true", STRIP_TRAILING_SPACES_WHOLE);
    map.put("false", STRIP_TRAILING_SPACES_NONE);
    trimMap = Collections.unmodifiableMap(map);
  }

  private static final Map<String, Boolean> newlineMap;

  static {
    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("true", Boolean.TRUE);
    map.put("false", Boolean.FALSE);
    newlineMap = Collections.unmodifiableMap(map);
  }

  private final Project myProject;

  public EditorSettingsManager(Project project) {
    myProject = project;
  }

  @Override
  public void beforeDocumentSaving(@NotNull Document document) {
    // This is fired when any document is saved, regardless of whether it is part of a save-all or
    // a save-one operation
    final VirtualFile file = FileDocumentManager.getInstance().getFile(document);
    applySettings(file);
  }

  private void applySettings(VirtualFile file) {
    if (file == null) {
      return;
    }
    if (!Utils.isEnabled(CodeStyleSettingsManager.getInstance(myProject).getCurrentSettings())) {
      return;
    }
    // Get editorconfig settings
    final String filePath = Utils.getFilePath(myProject, file);
    final SettingsProviderComponent settingsProvider = SettingsProviderComponent.getInstance();
    final List<EditorConfig.OutPair> outPairs = settingsProvider.getOutPairs(myProject, filePath);
    // Apply trailing spaces setting
    final String trimTrailingWhitespace = Utils.configValueForKey(outPairs, trimTrailingWhitespaceKey);
    applyConfigValueToUserData(file, OverrideEditorFileKeys.OVERRIDE_STRIP_TRAILING_SPACES_KEY,
                               trimTrailingWhitespaceKey, trimTrailingWhitespace, trimMap);
    // Apply final newline setting
    final String insertFinalNewline = Utils.configValueForKey(outPairs, insertFinalNewlineKey);
    applyConfigValueToUserData(file, OverrideEditorFileKeys.OVERRIDE_ENSURE_NEWLINE_KEY,
                               insertFinalNewlineKey, insertFinalNewline, newlineMap);
  }

  private <T> void applyConfigValueToUserData(VirtualFile file, Key<T> userDataKey, String editorConfigKey,
                                              String configValue, Map<String, T> configMap) {
    if (configValue.isEmpty()) {
      file.putUserData(userDataKey, null);
    }
    else {
      final T data = configMap.get(configValue);
      if (data == null) {
        Utils.invalidConfigMessage(myProject, configValue, editorConfigKey, file.getCanonicalPath());
      }
      else {
        file.putUserData(userDataKey, data);
        Utils.appliedConfigMessage(myProject, configValue, editorConfigKey, file.getCanonicalPath());
      }
    }
  }
}
