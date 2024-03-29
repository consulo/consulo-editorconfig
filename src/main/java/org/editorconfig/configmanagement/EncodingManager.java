package org.editorconfig.configmanagement;

import consulo.document.Document;
import consulo.document.FileDocumentManager;
import consulo.document.event.FileDocumentManagerAdapter;
import consulo.language.codeStyle.CodeStyleSettingsManager;
import consulo.project.Project;
import consulo.util.lang.Comparing;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.encoding.EncodingProjectManager;
import org.editorconfig.util.Utils;
import org.editorconfig.core.EditorConfig.OutPair;
import org.editorconfig.plugincomponents.SettingsProviderComponent;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncodingManager extends FileDocumentManagerAdapter {
  // Handles the following EditorConfig settings:
  private static final String charsetKey = "charset";

  private final Project myProject;

  private static final Map<String, Charset> encodingMap;

  static {
    Map<String, Charset> map = new HashMap<String, Charset>();
    map.put("latin1", Charset.forName("ISO-8859-1"));
    map.put("utf-8", Charset.forName("UTF-8"));
    map.put("utf-16be", Charset.forName("UTF-16BE"));
    map.put("utf-16le", Charset.forName("UTF-16LE"));
    encodingMap = Collections.unmodifiableMap(map);
  }

  private boolean isApplyingSettings;

  public EncodingManager(Project project) {
    this.myProject = project;
    isApplyingSettings = false;
  }

  @Override
  public void beforeDocumentSaving(@NotNull Document document) {
    final VirtualFile file = FileDocumentManager.getInstance().getFile(document);
    if (!isApplyingSettings) {
      applySettings(file);
    }
  }

  private void applySettings(VirtualFile file) {
    if (file == null) return;
    if (!Utils.isEnabled(CodeStyleSettingsManager.getInstance(myProject).getCurrentSettings())) return;

    // Prevent "setEncoding" calling "saveAll" from causing an endless loop
    isApplyingSettings = true;
    try {
      final String filePath = Utils.getFilePath(myProject, file);
      final List<OutPair> outPairs = SettingsProviderComponent.getInstance().getOutPairs(myProject, filePath);
      final EncodingProjectManager encodingProjectManager = EncodingProjectManager.getInstance(myProject);
      final String charset = Utils.configValueForKey(outPairs, charsetKey);
      if (!charset.isEmpty()) {
        final Charset newCharset = encodingMap.get(charset);
        if (newCharset != null) {
          if (Comparing.equal(newCharset, file.getCharset())) return;
          encodingProjectManager.setEncoding(file, newCharset);
          Utils.appliedConfigMessage(myProject, charset, charsetKey, filePath);
        } else {
          Utils.invalidConfigMessage(myProject, charset, charsetKey, filePath);
        }
      }
    } finally {
      isApplyingSettings = false;
    }
  }
}
