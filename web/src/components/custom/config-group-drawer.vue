<script setup lang="ts">
import { ref, watch } from 'vue';
import ConfigGroupForm from './config-group-form.vue';

defineOptions({ name: 'ConfigGroupDrawer' });

interface Props {
  configGroup: string;
  permCode: string;
  title: string;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', { default: false });

const formRef = ref<InstanceType<typeof ConfigGroupForm> | null>(null);

watch(visible, val => {
  if (val) {
    void formRef.value?.load();
  }
});

function handleSaved() {
  visible.value = false;
}
</script>

<template>
  <NDrawer v-model:show="visible" :width="420" display-directive="show">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <ConfigGroupForm
        ref="formRef"
        :config-group="props.configGroup"
        :perm-code="props.permCode"
        :auto-load="false"
        @saved="handleSaved"
      />
    </NDrawerContent>
  </NDrawer>
</template>
