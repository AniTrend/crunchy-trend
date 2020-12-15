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

package co.anitrend.support.crunchyroll.data.news.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import co.anitrend.support.crunchyroll.data.arch.database.common.ICrunchyDatabase
import co.anitrend.support.crunchyroll.data.news.datasource.local.CrunchyRssNewsDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import timber.log.Timber

class NewsContentProvider : ContentProvider(), KoinComponent {

    private val newsDao: CrunchyRssNewsDao by lazy {
        get<ICrunchyDatabase>().crunchyRssNewsDao()
    }

    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     *
     * You should defer nontrivial initialization (such as opening,
     * upgrading, and scanning databases) until the content provider is used
     * (via [.query], [.insert], etc).  Deferred initialization
     * keeps application startup fast, avoids unnecessary work if the provider
     * turns out not to be needed, and stops database errors (such as a full
     * disk) from halting application launch.
     *
     * If you use SQLite, [android.database.sqlite.SQLiteOpenHelper]
     * is a helpful utility class that makes it easy to manage databases,
     * and will automatically defer opening until first use.  If you do use
     * SQLiteOpenHelper, make sure to avoid calling
     * [android.database.sqlite.SQLiteOpenHelper.getReadableDatabase] or
     * [android.database.sqlite.SQLiteOpenHelper.getWritableDatabase]
     * from this method.  (Instead, override
     * [android.database.sqlite.SQLiteOpenHelper.onOpen] to initialize the
     * database when it is first opened.)
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    override fun onCreate() = true

    /**
     * Implement this to handle query requests from clients.
     *
     * Apps targeting [android.os.Build.VERSION_CODES.O] or higher should override
     * [.query] and provide a stub
     * implementation of this method.
     *
     * This method can be called from multiple threads, as described in
     * [Processes and Threads]({@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads).
     *
     * Example client call:
     *
     * <pre>// Request a specific record.
     * Cursor managedCursor = managedQuery(
     * ContentUris.withAppendedId(Contacts.People.CONTENT_URI, 2),
     * projection,    // Which columns to return.
     * null,          // WHERE clause.
     * null,          // WHERE clause value substitution
     * People.NAME + " ASC");   // Sort order.</pre>
     * Example implementation:
     *
     *
     * <pre>// SQLiteQueryBuilder is a helper class that creates the
     * // proper SQL syntax for us.
     * SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
     *
     * // Set the table we're querying.
     * qBuilder.setTables(DATABASE_TABLE_NAME);
     *
     * // If the query ends in a specific record number, we're
     * // being asked for a specific record, so set the
     * // WHERE clause in our query.
     * if((URI_MATCHER.match(uri)) == SPECIFIC_MESSAGE){
     * qBuilder.appendWhere("_id=" + uri.getPathLeafId());
     * }
     *
     * // Make the query.
     * Cursor c = qBuilder.query(mDb,
     * projection,
     * selection,
     * selectionArgs,
     * groupBy,
     * having,
     * sortOrder);
     * c.setNotificationUri(getContext().getContentResolver(), uri);
     * return c;</pre>
     *
     * @param uri The URI to query. This will be the full URI sent by the client;
     * if the client is requesting a specific record, the URI will end in a record number
     * that the implementation should parse and add to a WHERE or HAVING clause, specifying
     * that _id value.
     * @param projection The list of columns to put into the cursor. If
     * `null` all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     * If `null` then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     * the values from selectionArgs, in order that they appear in the selection.
     * The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     * If `null` then the provider is free to define the sort order.
     * @return a Cursor or `null`.
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Timber.tag(TAG).i("""
            query(
                uri: $uri,
                projection: $projection,
                selection: $selection,
                selectionArgs: $selectionArgs,
                sortOrder: $sortOrder
            )
        """.trimIndent())
        return when (uriMatcher.match(uri)) {
            Provider.Route.ALL.ordinal -> {
                val cursor = newsDao.findAllCursor()
                context?.let {
                    cursor.setNotificationUri(it.contentResolver, uri)
                    cursor
                }
            }
            Provider.Route.LIST.ordinal -> {
                val page = uri.getQueryParameter("page")?.toIntOrNull() ?: 1
                val perPage = uri.getQueryParameter("perPage")?.toIntOrNull() ?: 15
                val cursor = newsDao.findCursor(page, perPage)
                context?.let {
                    cursor.setNotificationUri(it.contentResolver, uri)
                    cursor
                }
            }
            Provider.Route.DETAIL.ordinal -> {
                val guid = uri.getQueryParameter("guid").orEmpty()
                val cursor = newsDao.findByIdCursor(guid)
                context?.let {
                    cursor.setNotificationUri(it.contentResolver, uri)
                    cursor
                }
            }
            else -> throw UnsupportedOperationException(
                "Query is either malformed or cannot be handled",
                Throwable(uri.toString())
            )
        }
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * `vnd.android.cursor.item` for a single record,
     * or `vnd.android.cursor.dir/` for multiple items.
     * This method can be called from multiple threads, as described in
     * [Processes and Threads]({@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads).
     *
     *
     * Note that there are no permissions needed for an application to
     * access this information; if your content provider requires read and/or
     * write permissions, or is not exported, all applications can still call
     * this method regardless of their access permissions.  This allows them
     * to retrieve the MIME type for a URI when dispatching intents.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or `null` if there is no type.
     */
    override fun getType(uri: Uri) = "vnd.android.cursor.item/${Provider.AUTHORITY}.${Provider.PATH}"

    /**
     * Implement this to handle requests to insert a new row.
     * As a courtesy, call [notifyChange()][ContentResolver.notifyChange]
     * after inserting.
     * This method can be called from multiple threads, as described in
     * [Processes and Threads]({@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads).
     * @param uri The content:// URI of the insertion request. This must not be `null`.
     * @param values A set of column_name/value pairs to add to the database.
     * This must not be `null`.
     * @return The URI for the newly inserted item.
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    /**
     * Implement this to handle requests to delete one or more rows.
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.
     * As a courtesy, call [notifyChange()][ContentResolver.notifyChange]
     * after deleting.
     * This method can be called from multiple threads, as described in
     * [Processes and Threads]({@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads).
     *
     * The implementation is responsible for parsing out a row ID at the end
     * of the URI, if a specific row is being deleted. That is, the client would
     * pass in `content://contacts/people/22` and the implementation is
     * responsible for parsing the record number (22) when creating a SQL statement.
     *
     * @param uri The full URI to query, including a row ID (if a specific record is requested).
     * @param selection An optional restriction to apply to rows when deleting.
     * @return The number of rows affected.
     * @throws SQLException
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    /**
     * Implement this to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     * As a courtesy, call [notifyChange()][ContentResolver.notifyChange]
     * after updating.
     * This method can be called from multiple threads, as described in
     * [Processes and Threads]({@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads).
     *
     * @param uri The URI to query. This can potentially have a record ID if this
     * is an update request for a specific record.
     * @param values A set of column_name/value pairs to update in the database.
     * This must not be `null`.
     * @param selection An optional filter to match rows to update.
     * @return the number of rows affected.
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    companion object {
        private val TAG = NewsContentProvider::class.java.simpleName

        private object Provider {
            const val AUTHORITY: String = "co.anitrend.crunchy"
            const val PATH: String = "provider/news"

            enum class Route(val descriptor: String) {
                ALL("$PATH/*"),
                DETAIL("$PATH/#"),
                LIST(PATH)
            }
        }

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(Provider.AUTHORITY, Provider.Route.ALL.descriptor, Provider.Route.ALL.ordinal)
            addURI(Provider.AUTHORITY, Provider.Route.LIST.descriptor, Provider.Route.LIST.ordinal)
            addURI(Provider.AUTHORITY, Provider.Route.DETAIL.descriptor, Provider.Route.DETAIL.ordinal)
        }
    }
}