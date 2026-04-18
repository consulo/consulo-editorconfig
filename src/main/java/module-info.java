/**
 * @author VISTALL
 * @since 24/05/2023
 */
module org.editorconfig.consulo
{
  requires consulo.code.editor.api;
  requires consulo.component.api;
  requires consulo.configurable.api;
  requires consulo.disposer.api;
  requires consulo.document.api;
  requires consulo.file.editor.api;
  requires consulo.ide.api;
  requires consulo.language.api;
  requires consulo.language.code.style.api;
  requires consulo.language.code.style.ui.api;
  requires consulo.localize.api;
  requires consulo.platform.api;
  requires consulo.project.api;
  requires consulo.project.ui.api;
  requires consulo.ui.api;
  requires consulo.util.dataholder;
  requires consulo.util.lang;
  requires consulo.virtual.file.system.api;

  requires editorconfig.core;
}
