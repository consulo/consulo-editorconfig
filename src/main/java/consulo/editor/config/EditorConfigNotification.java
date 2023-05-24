package consulo.editor.config;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;
import jakarta.annotation.Nonnull;

import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 24/05/2023
 */
@ExtensionImpl
public class EditorConfigNotification implements NotificationGroupContributor
{
	public static final NotificationGroup GROUP = NotificationGroup.balloonGroup("editorconfig", LocalizeValue.localizeTODO("EditorConfig"));

	@Override
	public void contribute(@Nonnull Consumer<NotificationGroup> consumer)
	{
  		consumer.accept(GROUP);
	}
}
