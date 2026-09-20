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

/** 默认模式：保留标题/引用/样式/表情/链接，去掉字号字体行高、列表对齐缩进、媒体表格代码分割线 */
const toolbarConfig: Partial<IToolbarConfig> = {
  excludeKeys: [
    'fontSize',
    'fontFamily',
    'lineHeight',
    'bulletedList',
    'numberedList',
    'todo',
    'group-justify',
    'group-indent',
    'group-image',
    'group-video',
    'insertTable',
    'codeBlock',
    'divider'
  ]
};

const editorConfig: Partial<IEditorConfig> = {
  placeholder: props.placeholder,
  // 选中悬浮条与工具栏能力对齐，避免浮出已禁用的列表入口
  hoverbarKeys: {
    text: {
      menuKeys: ['headerSelect', 'insertLink', '|', 'bold', 'through', 'color', 'bgColor', 'clearStyle']
    }
  }
};

function handleCreated(editor: IDomEditor) {
  editorRef.value = editor;
}

function handleUpdate(html: string) {
  // 空编辑器固定吐出 <p><br></p>，表单按空内容处理
  modelValue.value = html === '<p><br></p>' ? '' : html;
}

onBeforeUnmount(() => {
  // Editor 组件自己会 destroy，这里只清引用，避免重复销毁报错
  editorRef.value = undefined;
});
</script>

<template>
  <div class="wang-editor w-full border border-#ccc border-solid">
    <Toolbar
      class="border-b border-#ccc border-solid"
      :editor="editorRef"
      :default-config="toolbarConfig"
      mode="default"
    />
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

<style scoped>
/* 抽屉内下拉/颜色面板要压过 NDrawer，否则会被挡住 */
:deep(.w-e-bar-item-menus-container),
:deep(.w-e-select-list),
:deep(.w-e-drop-panel),
:deep(.w-e-modal),
:deep(.w-e-panel-content-color),
:deep(.w-e-panel-content-emotion) {
  z-index: 4000 !important;
}

/* 全局 reset 会把标题字号改成 inherit，编辑器内单独恢复 */
:deep(.w-e-text-container h1) {
  font-size: 2em;
  font-weight: bold;
  margin: 0.4em 0;
}

:deep(.w-e-text-container h2) {
  font-size: 1.5em;
  font-weight: bold;
  margin: 0.4em 0;
}

:deep(.w-e-text-container h3) {
  font-size: 1.17em;
  font-weight: bold;
  margin: 0.4em 0;
}

:deep(.w-e-text-container h4) {
  font-size: 1em;
  font-weight: bold;
  margin: 0.4em 0;
}

:deep(.w-e-text-container h5) {
  font-size: 0.83em;
  font-weight: bold;
  margin: 0.4em 0;
}

:deep(.w-e-text-container blockquote) {
  margin: 0.5em 0;
  padding: 0.4em 0.8em;
  border-left: 4px solid #ccc;
  color: #666;
}

:deep(.w-e-text-container a) {
  color: #2080f0;
  text-decoration: underline;
}
</style>
