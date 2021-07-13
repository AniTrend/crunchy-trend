/*
 *    Copyright 2020 AniTrend
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package co.anitrend.support.crunchyroll.data.arch.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

internal interface IDao<T> : ISourceDao {

    /**
     * Inserts a new item into the database ignoring items with the same primary key,
     * for both insert or update behavior use: [upsert]
     *
     * @param attribute item/s to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(attribute: T)

    /**
     * Inserts new items into the database ignoring items with the same primary key,
     * for both insert or update behavior use: [upsert]
     *
     * @param attribute item/s to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(attribute: List<T>)

    /**
     * Updates an item in the underlying database
     *
     * @param attribute item/s to update
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(attribute: T)

    /**
     * Updates a list of items in the underlying database
     *
     * @param attribute item/s to update
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(attribute: List<T>)

    /**
     * Deletes an item from the underlying database
     *
     * @param attribute item/s to delete
     */
    @Delete
    suspend fun delete(attribute: T)

    /**
     * Deletes a list of items from the underlying database
     *
     * @param attribute item/s to delete
     */
    @Delete
    suspend fun delete(attribute: List<T>)

    /**
     * Inserts or updates matching attributes on conflict
     *
     * @param attribute item/s to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(attribute: T)

    /**
     * Inserts or updates matching attributes on conflict
     *
     * @param attribute item/s to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(attribute: List<T>)
}