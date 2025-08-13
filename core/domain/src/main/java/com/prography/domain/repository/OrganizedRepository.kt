package com.prography.domain.repository

interface OrganizedRepository {
    suspend fun addOrganizedIds(ids: List<String>)
    suspend fun getOrganizedIds(): Set<String>
}