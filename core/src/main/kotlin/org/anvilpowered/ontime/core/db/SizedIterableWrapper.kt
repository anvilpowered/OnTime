/*
 *   OnTime - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.ontime.core.db

import org.anvilpowered.anvil.core.db.SizedIterable
import org.anvilpowered.anvil.core.db.SizedIterable as AnvilSizedIterable
import org.jetbrains.exposed.sql.SizedIterable as ExposedSizedIterable

fun <T> ExposedSizedIterable<T>.wrap(): SizedIterable<T> = SizedIterableWrapper(this)

private class SizedIterableWrapper<out T>(
    private val delegate: ExposedSizedIterable<T>,
) : AnvilSizedIterable<T> {
    override fun copy(): SizedIterable<T> = SizedIterableWrapper(delegate.copy())
    override fun count(): Long = delegate.count()
    override fun empty(): Boolean = delegate.empty()
    override fun iterator(): Iterator<T> = delegate.iterator()
    override fun limit(n: Int, offset: Long): SizedIterable<T> = SizedIterableWrapper(delegate.limit(n, offset))
}
