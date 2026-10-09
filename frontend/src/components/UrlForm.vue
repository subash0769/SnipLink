<template>
  <div class="card">
    <form @submit.prevent="handleSubmit" class="url-form">
      <div class="form-row">
        <div class="input-group" style="flex: 2;">
          <label class="input-label" for="targetUrl">Long Destination URL *</label>
          <input
            id="targetUrl"
            v-model="originalUrl"
            type="text"
            class="input-field"
            placeholder="https://example.com/very/long/destination/url"
            required
          />
        </div>

        <div class="input-group">
          <label class="input-label" for="customAlias">Custom Alias (optional)</label>
          <input
            id="customAlias"
            v-model="customCode"
            type="text"
            class="input-field"
            placeholder="e.g. summer-sale"
            maxlength="30"
          />
        </div>

        <div class="input-group">
          <label class="input-label" for="linkTitle">Title (optional)</label>
          <input
            id="linkTitle"
            v-model="title"
            type="text"
            class="input-field"
            placeholder="e.g. Landing Page"
            maxlength="100"
          />
        </div>
      </div>

      <div style="display: flex; justify-content: flex-end; align-items: center; gap: 16px;">
        <span v-if="error" style="color: var(--danger); font-size: 0.88rem;">{{ error }}</span>
        <button type="submit" class="btn-primary" :disabled="loading || !originalUrl.trim()">
          <span v-if="loading">Shortening...</span>
          <span v-else>✂️ Shorten URL</span>
        </button>
      </div>
    </form>

    <!-- Success Result Display -->
    <div v-if="latestCreated" class="result-banner">
      <div>
        <div style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 2px;">Your Shortened Link:</div>
        <div class="short-link-display">
          <span>{{ latestCreated.shortUrl }}</span>
        </div>
      </div>
      <div style="display: flex; gap: 8px;">
        <button class="btn-copy" @click="copyToClipboard(latestCreated.shortUrl)">
          {{ copied ? '✅ Copied!' : '📋 Copy' }}
        </button>
        <a :href="latestCreated.shortUrl" target="_blank" class="btn-copy" style="text-decoration: none;">
          🚀 Visit
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const emit = defineEmits(['urlCreated']);

const originalUrl = ref('');
const customCode = ref('');
const title = ref('');
const loading = ref(false);
const error = ref('');
const latestCreated = ref(null);
const copied = ref(false);

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const handleSubmit = async () => {
  error.value = '';
  loading.value = true;
  copied.value = false;

  try {
    const res = await fetch(`${API_BASE}/api/urls`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        originalUrl: originalUrl.value.trim(),
        customCode: customCode.value.trim() || null,
        title: title.value.trim() || null,
      }),
    });

    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.error || 'Failed to shorten URL');
    }

    latestCreated.value = data;
    originalUrl.value = '';
    customCode.value = '';
    title.value = '';
    emit('urlCreated', data);
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
};

const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text);
    copied.value = true;
    setTimeout(() => { copied.value = false; }, 2500);
  } catch (err) {
    console.error('Failed to copy', err);
  }
};
</script>
