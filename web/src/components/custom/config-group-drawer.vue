<script setup lang="ts">
defineOptions({ name: 'ConfigGroupDrawer' });

interface Props {
  configGroup: string;
  permCode: string;
  title: string;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', { default: false });

function handleSaved() {
  visible.value = false;
}
</script>

<template>
  <!-- if：打开时才挂载表单，auto-load 在挂载时拉数，避免 show + formRef 未就绪导致空内容 -->
  <NDrawer v-model:show="visible" :width="420" display-directive="if">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <ConfigGroupForm
        :config-group="props.configGroup"
        :perm-code="props.permCode"
        :auto-load="true"
        @saved="handleSaved"
      />
    </NDrawerContent>
  </NDrawer>
</template>
