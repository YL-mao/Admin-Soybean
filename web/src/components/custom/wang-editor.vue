<script setup lang="ts">
import { onBeforeUnmount, shallowRef } from 'vue';
import type { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import '@wangeditor/editor/dist/css/style.css';

defineOptions({ name: 'WangEditor' });

interface Props {
  /** 编辑区高度 */
  height?: number;
  placeholder?: string;
}

const props = withDefaults(defineProps<Props>(), {
  height: 280,
  placeholder: ''
});

const modelValue = defineModel<string>('value', { default: '' });

const editorRef = shallowRef<IDomEditor>();

/** 只放后端白名单能留下的格式，避免标题、图片保存后被洗掉 */
const toolbarConfig: Partial<IToolbarConfig> = {
  toolbarKeys: [
    'bold',
    'italic',
    'underline',
    'color',
    '|',
    'justifyLeft',
    'justifyCenter',
    'justifyRight',
    '|',
    'bulletedList',
    'numberedList',
    '|',
    'insertLink',
    '|',
    'undo',
    'redo'
  ]
};

const editorConfig: Partial<IEditorConfig> = {
  placeholder: props.placeholder
};

function handleCreated(editor: IDomEditor) {
  editorRef.value = editor;
}

function handleUpdate(html: string) {
  // 空编辑器固定吐出 <p><br></p>，表单按空内容处理
  modelValue.value = html === '<p><br></p>' ? '' : html;
}

onBeforeUnmount(() => {
  editorRef.value?.destroy();
  editorRef.value = undefined;
});
</script>

<template>
  <div class="wang-editor w-full border border-#ccc border-solid">
    <Toolbar class="border-b border-#ccc border-solid" :editor="editorRef" :default-config="toolbarConfig" mode="default" />
    <Editor
      :model-value="modelValue"
      :default-config="editorConfig"
      :style="{ height: `${height}px`, overflowY: 'hidden' }"
      mode="default"
      @on-created="handleCreated"
      @update:model-value="handleUpdate"
    />
  </div>
</template>
