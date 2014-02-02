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
package ajmal.Helpers.Listeners;

import java.util.LinkedList;
import java.util.List;

/**
 * Objects that extend this class have the ability to run synchronously and notify the main thread through
 * the available listeners.
 * @author Lazaros
 */
public abstract class Listened  implements Runnable{
    List<ActionCompletedListener> completionListenersList= new LinkedList<>();
    List<ProgressMadeListener> progressListenersList= new LinkedList<>();

    public void actionCompleted() {
        for (ActionCompletedListener listener : completionListenersList) {
            listener.OnCompleted();
        }
    }

    public void progressMade(int progress, int max) {
        for (ProgressMadeListener listener : progressListenersList) {
            listener.OnProgressMade(progress, max, String.valueOf(progress)+" / "+String.valueOf(max));
        }
    }

    public void progressMade(int progress, int max, String message) {
        for (ProgressMadeListener listener : progressListenersList) {
            if (progress < max) listener.OnProgressMade(progress, max, message);
        }
    }
    
    public void addCompletionListener( ActionCompletedListener listener ) {
        completionListenersList.add(listener);
    }
    
    public void addProgressListener(ProgressMadeListener listener) {
        progressListenersList.add(listener);
    }
}
