package org.editorconfig.plugincomponents;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.ide.ServiceManager;
import consulo.project.Project;
import jakarta.inject.Singleton;
import org.editorconfig.core.EditorConfig;
import org.editorconfig.core.EditorConfig.OutPair;
import org.editorconfig.core.EditorConfigException;
import org.editorconfig.util.Utils;

import java.util.ArrayList;
import java.util.List;

@Singleton
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
public class SettingsProviderComponent {
  private EditorConfig editorConfig;

  public SettingsProviderComponent() {
    editorConfig = new EditorConfig();
  }

  public static SettingsProviderComponent getInstance() {
    return ServiceManager.getService(SettingsProviderComponent.class);
  }

  public List<OutPair> getOutPairs(Project project, String filePath) {
    final List<OutPair> outPairs;
    try {
      outPairs = editorConfig.getProperties(filePath);
      return outPairs;
    }
    catch (EditorConfigException error) {
      Utils.invalidConfigMessage(project, error.getMessage(), "", filePath);
      return new ArrayList<OutPair>();
    }
  }
}
