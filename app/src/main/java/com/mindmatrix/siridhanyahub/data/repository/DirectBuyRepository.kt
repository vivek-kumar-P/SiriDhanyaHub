package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.FpoDao
import com.mindmatrix.siridhanyahub.data.local.entity.FpoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectBuyRepository @Inject constructor(
    private val fpoDao: FpoDao
) {
    fun observeAll(): Flow<List<FpoEntity>> = fpoDao.observeAll()

    fun observeById(fpoId: Int): Flow<FpoEntity?> = fpoDao.observeById(fpoId)
}
