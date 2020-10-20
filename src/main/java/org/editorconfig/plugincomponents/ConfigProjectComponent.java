package org.editorconfig.plugincomponents;

import jakarta.inject.Singleton;

import org.editorconfig.configmanagement.EditorSettingsManager;
import org.editorconfig.configmanagement.EncodingManager;
import org.editorconfig.configmanagement.LineEndingsManager;
import org.jetbrains.annotations.NotNull;
import com.intellij.AppTopics;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBus;

@Singleton
public class ConfigProjectComponent
{
	public ConfigProjectComponent(final Project project, final EditorFactory editorFactory, VirtualFileManager virtualFileManager)
	{
		// Register project-level config managers
		MessageBus bus = project.getMessageBus();
		EditorSettingsManager editorSettingsManager = new EditorSettingsManager(project);
		EncodingManager encodingManager = new EncodingManager(project);
		LineEndingsManager lineEndingsManager = new LineEndingsManager(project);
		bus.connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, encodingManager);
		bus.connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, editorSettingsManager);
		bus.connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, lineEndingsManager);
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
		}, project);
	}
}
