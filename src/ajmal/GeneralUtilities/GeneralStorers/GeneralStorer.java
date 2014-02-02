/*
 * Copyright (C) 2013 Lazaros Tsochatzidis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ajmal.GeneralUtilities.GeneralStorers;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;


/**
 * A Storer object is designed to provide three kinds of Map. A simple Map, a SortedMap and a MultiMap.
 * @author Lazaros
 */
public interface GeneralStorer {
    public void AddToMultimap(String name,Object key,Object value);
    public Set SearchInMultimap(String name,Object key);
    public Set GetSet(String name);
    public void DeleteMap(String name);
    public SortedMap GetSortedMap(String name);
    public void DeleteSortedMap(String name);
    public Map GetMap(String name);
    public void DeleteSet(String name);
    public long GetLong(String name);
    public long IncrementAndGetLong(String name);
    public void DeleteLong(String name);
    public String getLocation();
    public void setLocation(String location);
    public void insertionMade();
    public void commit();
    public void close();

    void SetLong(String name, long value);
}
