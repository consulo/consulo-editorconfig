package consulo.editorConfig;

import consulo.editorConfig.icon.EditorConfigIconGroup;
import consulo.language.file.LanguageFileType;
import consulo.language.plain.PlainTextLanguage;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 05.05.2024
 */
public class EditorConfigFileType extends LanguageFileType {
  public static final EditorConfigFileType INSTANCE = new EditorConfigFileType();

  private EditorConfigFileType() {
    super(PlainTextLanguage.INSTANCE);
  }

  @Nonnull
  @Override
  public String getId() {
    return "EDITORCONFIG";
  }

  @Nonnull
  @Override
  public LocalizeValue getDescription() {
    return LocalizeValue.localizeTODO("EditorConfig files");
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return EditorConfigIconGroup.editorconfig();
  }
}
