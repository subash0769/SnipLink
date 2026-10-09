<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <div>
          <h2 style="font-size: 1.4rem; font-weight: 800;">Analytics: /{{ shortCode }}</h2>
          <p style="color: var(--text-muted); font-size: 0.85rem; margin-top: 4px; word-break: break-all;">
            {{ analytics?.originalUrl }}
          </p>
        </div>
        <button class="btn-close" @click="$emit('close')" aria-label="Close modal">&times;</button>
      </div>

      <div v-if="loading" style="text-align: center; padding: 40px; color: var(--text-muted);">
        Loading analytics data...
      </div>

      <div v-else-if="error" style="color: var(--danger); text-align: center; padding: 20px;">
        {{ error }}
      </div>

      <div v-else-if="analytics">
        <!-- Metric Cards -->
        <div class="stat-grid">
          <StatCard label="Total Clicks" :value="analytics.totalClicks" />
          <StatCard label="Unique Referrers" :value="analytics.uniqueReferrers" />
          <StatCard label="Top Browser" :value="analytics.topBrowser" />
          <StatCard label="Top OS" :value="analytics.topOs" />
        </div>

        <!-- Breakdown Row -->
        <div class="breakdown-row">
          <!-- Browsers Breakdown -->
          <div class="breakdown-box">
            <div class="breakdown-title">Browsers</div>
            <div v-if="!analytics.browsers || Object.keys(analytics.browsers).length === 0" style="color: var(--text-dim); font-size: 0.85rem;">
              No browser data yet
            </div>
            <div v-for="(count, browser) in analytics.browsers" :key="browser" style="margin-bottom: 10px;">
              <div class="breakdown-item">
                <span>{{ browser }}</span>
                <span style="font-weight: 700;">{{ count }} ({{ calcPercentage(count, analytics.totalClicks) }}%)</span>
              </div>
              <div class="bar-track">
                <div class="bar-fill" :style="{ width: calcPercentage(count, analytics.totalClicks) + '%' }"></div>
              </div>
            </div>
          </div>

          <!-- Operating Systems Breakdown -->
          <div class="breakdown-box">
            <div class="breakdown-title">Operating Systems</div>
            <div v-if="!analytics.operatingSystems || Object.keys(analytics.operatingSystems).length === 0" style="color: var(--text-dim); font-size: 0.85rem;">
              No OS data yet
            </div>
            <div v-for="(count, os) in analytics.operatingSystems" :key="os" style="margin-bottom: 10px;">
              <div class="breakdown-item">
                <span>{{ os }}</span>
                <span style="font-weight: 700;">{{ count }} ({{ calcPercentage(count, analytics.totalClicks) }}%)</span>
              </div>
              <div class="bar-track">
                <div class="bar-fill" style="background: #8b5cf6;" :style="{ width: calcPercentage(count, analytics.totalClicks) + '%' }"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Referrers Breakdown -->
        <div class="breakdown-box" style="margin-bottom: 24px;">
          <div class="breakdown-title">Traffic Sources / Referrers</div>
          <div v-if="!analytics.referrers || Object.keys(analytics.referrers).length === 0" style="color: var(--text-dim); font-size: 0.85rem;">
            No referrer data yet
          </div>
          <div v-for="(count, refKey) in analytics.referrers" :key="refKey" style="margin-bottom: 8px;">
            <div class="breakdown-item">
              <span style="word-break: break-all;">{{ refKey }}</span>
              <span style="font-weight: 700;">{{ count }} clicks</span>
            </div>
          </div>
        </div>

        <!-- Recent Click Log -->
        <div class="breakdown-box">
          <div class="breakdown-title">Recent Clicks Activity (Last 25)</div>
          <div v-if="!analytics.recentClicks || analytics.recentClicks.length === 0" style="color: var(--text-dim); font-size: 0.85rem;">
            No clicks recorded yet. Try clicking your short link to test!
          </div>
          <div v-else style="overflow-x: auto;">
            <table class="url-table" style="font-size: 0.82rem;">
              <thead>
                <tr>
                  <th>Timestamp</th>
                  <th>IP Address</th>
                  <th>Browser</th>
                  <th>OS</th>
                  <th>Device</th>
                  <th>Referrer</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(click, idx) in analytics.recentClicks" :key="idx">
                  <td>{{ formatDateTime(click.clickedAt) }}</td>
                  <td>{{ click.ipAddress || '—' }}</td>
                  <td>{{ click.browser || '—' }}</td>
                  <td>{{ click.os || '—' }}</td>
                  <td>{{ click.deviceType || '—' }}</td>
                  <td>{{ click.referrer || 'Direct' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import StatCard from './StatCard.vue';

const props = defineProps({
  shortCode: {
    type: String,
    required: true
  }
});

defineEmits(['close']);

const analytics = ref(null);
const loading = ref(true);
const error = ref('');

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const fetchAnalytics = async () => {
  loading.value = true;
  error.value = '';
  try {
    const res = await fetch(`${API_BASE}/api/urls/${props.shortCode}/analytics`);
    if (!res.ok) {
      throw new Error('Failed to retrieve analytics');
    }
    analytics.value = await res.json();
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
};

const calcPercentage = (count, total) => {
  if (!total || total === 0) return 0;
  return Math.round((count / total) * 100);
};

const formatDateTime = (iso) => {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' }) + ' ' + d.toLocaleDateString([], { month: 'short', day: 'numeric' });
};

onMounted(() => {
  fetchAnalytics();
});
</script>
