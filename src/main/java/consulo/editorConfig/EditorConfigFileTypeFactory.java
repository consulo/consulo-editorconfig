package consulo.editorConfig;

import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileNameMatcher;
import consulo.virtualFileSystem.fileType.FileNameMatcherFactory;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;

/**
 * @author VISTALL
 * @since 25/05/2023
 */
@ExtensionImpl
public class EditorConfigFileTypeFactory extends FileTypeFactory {
  private final FileNameMatcherFactory myFileNameMatcherFactory;

  @Inject
  public EditorConfigFileTypeFactory(FileNameMatcherFactory fileNameMatcherFactory) {
    myFileNameMatcherFactory = fileNameMatcherFactory;
  }

  @Override
  public void createFileTypes(@Nonnull FileTypeConsumer fileTypeConsumer) {
    FileNameMatcher matcher = myFileNameMatcherFactory.createExactFileNameMatcher(".editorconfig");
    fileTypeConsumer.consume(EditorConfigFileType.INSTANCE, matcher);
  }
}
