/*
 * Copyright 2014 Quality and Usability Lab, Telekom Innvation Laboratories, TU Berlin..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tel.quenference.db.dao.extractor;

import android.database.Cursor;
import de.tel.quenference.db.dao.CursorExtracting;
import de.tel.quenference.db.entities.PaperEntity;

/**
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PaperExtractor implements CursorExtracting {

  public Object extract(Cursor c) {
    return new PaperEntity(c.getString(0), c.getString(1), c.getString(2),
                           c.getInt(3), c.getInt(4), c.getString(5), 
                           c.getString(6), c.getInt(7), c.getString(8),
                           c.getInt(9), c.getInt(10), c.getString(11));
  }
  
}
