<template>
  <div>
    <div class="section-title">
      <span>Shortened Links ({{ urls.length }})</span>
      <button class="btn-sm btn-analytics" @click="$emit('refresh')" title="Refresh list">
        🔄 Refresh
      </button>
    </div>

    <div v-if="urls.length === 0" class="card" style="text-align: center; color: var(--text-muted); padding: 48px;">
      <p style="font-size: 1.1rem; font-weight: 600;">No short links created yet.</p>
      <p style="font-size: 0.88rem; margin-top: 6px;">Paste a destination URL above to create your first short link!</p>
    </div>

    <div v-else class="table-wrapper">
      <table class="url-table">
        <thead>
          <tr>
            <th>Link Info</th>
            <th>Short URL</th>
            <th>Destination</th>
            <th>Clicks</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in urls" :key="item.id">
            <td>
              <div style="font-weight: 700; color: var(--text-main);">{{ item.title || 'Untitled' }}</div>
              <div style="font-size: 0.75rem; color: var(--text-dim);">
                {{ formatDate(item.createdAt) }}
              </div>
            </td>
            <td>
              <a :href="item.shortUrl" target="_blank" style="color: var(--primary); text-decoration: none; font-weight: 600;">
                /{{ item.shortCode }}
              </a>
            </td>
            <td>
              <div style="max-width: 260px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: var(--text-muted); font-size: 0.85rem;" :title="item.originalUrl">
                {{ item.originalUrl }}
              </div>
            </td>
            <td>
              <span class="click-badge">
                📊 {{ item.clickCount }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button class="btn-sm btn-analytics" @click="$emit('viewAnalytics', item.shortCode)">
                  Analytics
                </button>
                <button class="btn-sm btn-delete" @click="$emit('deleteUrl', item.id)">
                  Delete
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
defineProps({
  urls: {
    type: Array,
    default: () => []
  }
});

defineEmits(['viewAnalytics', 'deleteUrl', 'refresh']);

const formatDate = (dateString) => {
  if (!dateString) return '';
  const d = new Date(dateString);
  return d.toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};
</script>
