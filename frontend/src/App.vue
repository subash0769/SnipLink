<template>
  <div>
    <Navbar />

    <main class="container">
      <section class="hero">
        <h1>Shorten, Share & Track URLs</h1>
        <p>A lightning-fast URL shortener with real-time click tracking, device analytics, and geo stats.</p>
      </section>

      <UrlForm @urlCreated="handleUrlCreated" />

      <UrlList
        :urls="urls"
        @viewAnalytics="openAnalytics"
        @deleteUrl="handleDeleteUrl"
        @refresh="fetchUrls"
      />
    </main>

    <AnalyticsModal
      v-if="selectedShortCode"
      :shortCode="selectedShortCode"
      @close="selectedShortCode = null"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Navbar from './components/Navbar.vue';
import UrlForm from './components/UrlForm.vue';
import UrlList from './components/UrlList.vue';
import AnalyticsModal from './components/AnalyticsModal.vue';

const urls = ref([]);
const selectedShortCode = ref(null);

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const fetchUrls = async () => {
  try {
    const res = await fetch(`${API_BASE}/api/urls`);
    if (res.ok) {
      urls.value = await res.json();
    }
  } catch (err) {
    console.warn('Backend connection pending:', err.message);
  }
};

const handleUrlCreated = (newUrl) => {
  urls.value.unshift(newUrl);
};

const handleDeleteUrl = async (id) => {
  if (!confirm('Are you sure you want to delete this short link?')) return;
  try {
    const res = await fetch(`${API_BASE}/api/urls/${id}`, { method: 'DELETE' });
    if (res.ok) {
      urls.value = urls.value.filter(u => u.id !== id);
    }
  } catch (err) {
    console.error('Failed to delete URL:', err);
  }
};

const openAnalytics = (shortCode) => {
  selectedShortCode.value = shortCode;
};

onMounted(() => {
  fetchUrls();
});
</script>
