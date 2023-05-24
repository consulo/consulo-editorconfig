package org.editorconfig.plugincomponents;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.editor.config.EditorConfigNotification;
import consulo.ide.ServiceManager;
import consulo.project.Project;
import consulo.project.ProjectPropertiesComponent;
import consulo.project.ui.notification.NotificationType;
import jakarta.inject.Singleton;

/**
 * @author Dennis.Ushakov
 */
@Singleton
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
public class EditorConfigNotifier
{
	public static final String LAST_NOTIFICATION_STATUS = "editorconfig.notification";

	public static EditorConfigNotifier getInstance()
	{
		return ServiceManager.getService(EditorConfigNotifier.class);
	}

	public void error(Project project, String id, String message)
	{
		doNotify(project, id, message, NotificationType.ERROR);
	}

	protected void doNotify(Project project, String id, String message, final NotificationType type)
	{
		final String value = ProjectPropertiesComponent.getInstance(project).getValue("editorconfig.notification");
		if(id.equals(value))
		{
			return;
		}

		EditorConfigNotification.GROUP.createNotification(message, type).notify(project);

		ProjectPropertiesComponent.getInstance(project).setValue(LAST_NOTIFICATION_STATUS, id);
	}

	public void info(Project project, String message)
	{
		doNotify(project, message, message, NotificationType.INFORMATION);
	}
}
