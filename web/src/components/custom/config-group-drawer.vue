<script setup lang="ts">
import { computed, ref } from 'vue';
import { $t } from '@/locales';
import ConfigGroupForm from './config-group-form.vue';

defineOptions({ name: 'ConfigGroupDrawer' });

interface Props {
  configGroup: string;
  permCode: string;
  title: string;
  /** 只展示指定配置编码；不传则整组 */
  configCodes?: string[];
}

const props = withDefaults(defineProps<Props>(), {
  configCodes: undefined
});

const visible = defineModel<boolean>('visible', { default: false });

const formRef = ref<InstanceType<typeof ConfigGroupForm> | null>(null);

const canSave = computed(() => formRef.value?.canSave ?? false);
const saving = computed(() => formRef.value?.saving ?? false);

function handleSaved() {
  visible.value = false;
}

function handleSave() {
  formRef.value?.save();
}
</script>

<template>
  <!-- if：打开时才挂载表单，auto-load 在挂载时拉数，避免 show + formRef 未就绪导致空内容 -->
  <NDrawer v-model:show="visible" :width="420" display-directive="if">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
      <ConfigGroupForm
        ref="formRef"
        :config-group="props.configGroup"
        :perm-code="props.permCode"
        :config-codes="props.configCodes"
        :auto-load="true"
        @saved="handleSaved"
      />
      <template v-if="canSave" #footer>
        <div class="flex justify-end">
          <NButton type="primary" :loading="saving" @click="handleSave">{{ $t('common.update') }}</NButton>
        </div>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
