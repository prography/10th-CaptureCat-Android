package com.prography.data.repository

import com.prography.domain.repository.OrganizedRepository
import com.prography.datastore.organized.OrganizedDataStore
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class OrganizedRepositoryImpl @Inject constructor(
    private val organizedDataStore: OrganizedDataStore
) : OrganizedRepository {
    override suspend fun addOrganizedIds(ids: List<String>) {
        organizedDataStore.addOrganizedIds(ids)
    }

    override suspend fun getOrganizedIds(): Set<String> {
        return organizedDataStore.organizedIds.first()
    }
}
