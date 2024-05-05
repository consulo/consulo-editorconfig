package org.editorconfig.configmanagement;

import consulo.document.Document;
import consulo.document.FileDocumentManager;
import consulo.document.event.FileDocumentManagerAdapter;
import consulo.fileEditor.FileEditorManager;
import consulo.fileEditor.event.FileEditorManagerEvent;
import consulo.fileEditor.event.FileEditorManagerListener;
import consulo.language.codeStyle.CodeStyleSettingsManager;
import consulo.platform.LineSeparator;
import consulo.project.Project;
import consulo.project.ui.wm.IdeFrame;
import consulo.project.ui.wm.StatusBar;
import consulo.project.ui.wm.StatusBarWidget;
import consulo.project.ui.wm.WindowManager;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import org.editorconfig.core.EditorConfig;
import org.editorconfig.plugincomponents.SettingsProviderComponent;
import org.editorconfig.util.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Dennis.Ushakov
 */
public class LineEndingsManager extends FileDocumentManagerAdapter {
  // Handles the following EditorConfig settings:
  private static final String lineEndingsKey = "end_of_line";

  private final Project myProject;
  private boolean statusBarUpdated = false;

  public LineEndingsManager(Project project) {
    this.myProject = project;
  }

  @Override
  public void beforeAllDocumentsSaving() {
    statusBarUpdated = false;
  }

  private void updateStatusBar() {
    myProject.getUIAccess().give(new Runnable() {
      @Override
      public void run() {
        IdeFrame frame = WindowManager.getInstance().getIdeFrame(myProject);
        if (frame == null) {
          return;
        }
        
        StatusBar statusBar = frame.getStatusBar();
        if (statusBar == null) {
          return;
        }

        Optional<StatusBarWidget> optional = statusBar.findWidget(widget -> "lineSeparatorWidget".equals(widget.getId()));

        optional.ifPresent(widget -> {
          if (widget instanceof FileEditorManagerListener) {
            FileEditorManagerEvent event = new FileEditorManagerEvent(FileEditorManager.getInstance(myProject),
                                                                      null, null, null, null);
            ((FileEditorManagerListener)widget).selectionChanged(event);
          }
        });
      }
    });
  }

  @Override
  public void beforeDocumentSaving(@Nonnull Document document) {
    VirtualFile file = FileDocumentManager.getInstance().getFile(document);
    applySettings(file);
  }

  private void applySettings(VirtualFile file) {
    if (file == null) return;
    if (!Utils.isEnabled(CodeStyleSettingsManager.getInstance(myProject).getCurrentSettings())) return;

    final String filePath = Utils.getFilePath(myProject, file);
    final List<EditorConfig.OutPair> outPairs = SettingsProviderComponent.getInstance().getOutPairs(myProject, filePath);
    final String lineEndings = Utils.configValueForKey(outPairs, lineEndingsKey);
    if (!lineEndings.isEmpty()) {
      try {
        LineSeparator separator = LineSeparator.valueOf(lineEndings.toUpperCase(Locale.US));
        String oldSeparator = file.getDetectedLineSeparator();
        String newSeparator = separator.getSeparatorString();
        if (!StringUtil.equals(oldSeparator, newSeparator)) {
          file.setDetectedLineSeparator(newSeparator);
          if (!statusBarUpdated) {
            statusBarUpdated = true;
            updateStatusBar();
          }
          Utils.appliedConfigMessage(myProject, lineEndings, lineEndingsKey, filePath);
        }
      }
      catch (IllegalArgumentException e) {
        Utils.invalidConfigMessage(myProject, lineEndings, lineEndingsKey, filePath);
      }
    }
  }
}
