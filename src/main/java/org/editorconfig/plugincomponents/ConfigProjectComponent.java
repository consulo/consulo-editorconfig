package org.editorconfig.plugincomponents;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.codeEditor.Editor;
import consulo.codeEditor.EditorEx;
import consulo.codeEditor.EditorFactory;
import consulo.component.messagebus.MessageBus;
import consulo.disposer.Disposable;
import consulo.document.event.FileDocumentManagerListener;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.event.VirtualFileEvent;
import consulo.virtualFileSystem.event.VirtualFileListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.editorconfig.configmanagement.EditorSettingsManager;
import org.editorconfig.configmanagement.EncodingManager;
import org.editorconfig.configmanagement.LineEndingsManager;
import org.jetbrains.annotations.NotNull;

@Singleton
@ServiceAPI(value = ComponentScope.PROJECT, lazy = false)
@ServiceImpl
public class ConfigProjectComponent implements Disposable
{
	@Inject
	public ConfigProjectComponent(final Project project, final EditorFactory editorFactory, VirtualFileManager virtualFileManager)
	{
		// Register project-level config managers
		MessageBus bus = project.getMessageBus();
		EditorSettingsManager editorSettingsManager = new EditorSettingsManager(project);
		EncodingManager encodingManager = new EncodingManager(project);
		LineEndingsManager lineEndingsManager = new LineEndingsManager(project);
		bus.connect().subscribe(FileDocumentManagerListener.class, encodingManager);
		bus.connect().subscribe(FileDocumentManagerListener.class, editorSettingsManager);
		bus.connect().subscribe(FileDocumentManagerListener.class, lineEndingsManager);
		virtualFileManager.addVirtualFileListener(new VirtualFileListener()
		{
			@Override
			public void fileCreated(@NotNull VirtualFileEvent event)
			{
				updateOpenEditors(event);
			}

			@Override
			public void fileDeleted(@NotNull VirtualFileEvent event)
			{
				updateOpenEditors(event);
			}

			@Override
			public void contentsChanged(@NotNull VirtualFileEvent event)
			{
				updateOpenEditors(event);
			}

			private void updateOpenEditors(VirtualFileEvent event)
			{
				if(".editorconfig".equals(event.getFile().getName()))
				{
					for(Editor editor : editorFactory.getAllEditors())
					{
						((EditorEx) editor).reinitSettings();
					}
				}
			}
		}, this);
	}

	@Override
	public void dispose()
	{

	}
}
