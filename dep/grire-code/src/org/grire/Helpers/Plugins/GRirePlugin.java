/* This file is part of the GRire project: https://code.google.com/p/grire/
 *
 * GRire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Copyright (C) 2013 Lazaros Tsochatzidis <ltsochat at ee.duth.gr>
 */
package org.grire.Helpers.Plugins;

import net.xeoh.plugins.base.Plugin;
import org.grire.Helpers.Listeners.Listened;


public interface GRirePlugin extends Plugin {

    public Listened setUp(Object...args) throws  Exception;
    public Class getComponentInterface();
    public Class[] getParameterTypes();
    public Class[] getSetUpParameterTypes();
    public String[] getParameterNames();
    public String[] getSetUpParameterNames();
    public String[] getDefaultParameterValues();
    public String[] getDefaultSetUpParameterValues();
    public boolean requiresSetUp();
}
