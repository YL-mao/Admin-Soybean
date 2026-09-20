/** v5 的 package.json exports 没带上 types，这里补声明。 */
declare module '@wangeditor/editor-for-vue' {
  import type { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor';
  import type { DefineComponent } from 'vue';

  export const Editor: DefineComponent<{
    modelValue?: string;
    defaultConfig?: Partial<IEditorConfig>;
    mode?: string;
  }>;

  export const Toolbar: DefineComponent<{
    editor?: IDomEditor;
    defaultConfig?: Partial<IToolbarConfig>;
    mode?: string;
  }>;
}
