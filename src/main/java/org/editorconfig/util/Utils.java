package org.editorconfig.util;

import consulo.language.codeStyle.CodeStyleSettings;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.editorconfig.core.EditorConfig.OutPair;
import org.editorconfig.plugincomponents.EditorConfigNotifier;
import org.editorconfig.settings.EditorConfigSettings;

import java.util.List;

public class Utils {
  public static String configValueForKey(List<OutPair> outPairs, String key) {
    for (OutPair outPair : outPairs) {
      if (outPair.getKey().equals(key)) {
        return outPair.getVal();
      }
    }
    return "";
  }

  public static boolean isEnabled(CodeStyleSettings currentSettings) {
    return currentSettings.getCustomSettings(EditorConfigSettings.class).ENABLED;
  }

  public static void invalidConfigMessage(Project project, String configValue, String configKey, String filePath) {
    EditorConfigNotifier.getInstance().error(project, configValue, "\"" + configValue + "\" is not a valid value" + (!configKey.isEmpty() ? " for " + configKey : "") + " for file " + filePath);
  }

  public static void appliedConfigMessage(Project project, String configValue, String configKey, String filePath) {
    EditorConfigNotifier.getInstance().info(project, "Applied .editorconfig settings");
  }

  public static String getFilePath(Project project, VirtualFile file) {
    if (!file.isInLocalFileSystem()) {
      return project.getBasePath() + "/" + file.getNameWithoutExtension() + "." + file.getFileType().getDefaultExtension();
    }
    return file.getCanonicalPath();
  }
}
