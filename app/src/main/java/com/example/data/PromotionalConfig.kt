package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PromotionalConfig {
    private val _badgeText = MutableStateFlow("🔥 OFA KUBWA YA WIKI")
    val badgeText: StateFlow<String> = _badgeText.asStateFlow()

    private val _bannerTitle = MutableStateFlow("Simu Halisi Zenye Warranty ya Mwaka 1")
    val bannerTitle: StateFlow<String> = _bannerTitle.asStateFlow()

    private val _bannerSubtitle = MutableStateFlow("Dar es Salaam: Free Delivery | Mikoani Tunatuma Tanzania")
    val bannerSubtitle: StateFlow<String> = _bannerSubtitle.asStateFlow()

    private val _bannerImageUrl = MutableStateFlow("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=800&q=80")
    val bannerImageUrl: StateFlow<String> = _bannerImageUrl.asStateFlow()

    fun updateBanner(badge: String, title: String, subtitle: String, imageUrl: String) {
        if (badge.isNotBlank()) _badgeText.value = badge
        if (title.isNotBlank()) _bannerTitle.value = title
        if (subtitle.isNotBlank()) _bannerSubtitle.value = subtitle
        if (imageUrl.isNotBlank()) _bannerImageUrl.value = imageUrl
    }
}
