/*
   Copyright 2008 davidwheeler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.tensin.sonos.model;


/**
 * Listens for events from the QueueModel.
 *
 * @author David WHEELER
 * @author Serge SIMON
 */
public interface QueueModelListener {

  /**
   * The entries have changed.
   *
   * @param model the model
   */
  void entriesChanged(QueueModel model);

  /**
   * The value of NowPlaying has changed.
   *
   * @param model the model
   */
  void nowPlayingChanged(QueueModel model);

}
