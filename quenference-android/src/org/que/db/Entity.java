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
package org.que.db;

/**
 * The Entity enum represents the entities or also named tables from the
 * database.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public enum Entity {

  SESSION(ConferenceDBContract.ConferenceSession.TABLE_NAME),
  AUTHOR(ConferenceDBContract.ConferenceAuthor.TABLE_NAME),
  PAPER(ConferenceDBContract.ConferencePaper.TABLE_NAME),
  PAPER_AUTHORS(ConferenceDBContract.ConferencePaperAuthors.TABLE_NAME);
  /**
   * The table name of the corresponding Entity.
   */
  private String tableName;

  /**
   * The ctor to create an enum obj with a table name.
   *
   * @param table the name of the table
   */
  private Entity(String table) {
    this.tableName = table;
  }

  @Override
  public String toString() {
    return tableName;
  }
}