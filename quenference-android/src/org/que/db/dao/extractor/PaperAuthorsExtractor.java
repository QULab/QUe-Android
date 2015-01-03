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
package org.que.db.dao.extractor;

import android.database.Cursor;
import org.que.db.dao.CursorExtracting;
import org.que.db.entities.PaperAuthorsEntity;

/**
 * The implementation of the CursorExtracting interface, to extract from the
 * database cursor the paper authors entity.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PaperAuthorsExtractor implements CursorExtracting {

  /**
   * The extract method which will be used to extract the values.
   *
   * @param c the cursor which contains the values
   * @return the extracted object
   */
  public Object extract(Cursor c) {
    return new PaperAuthorsEntity(c.getInt(0), c.getInt(1));
  }
  
  
}
