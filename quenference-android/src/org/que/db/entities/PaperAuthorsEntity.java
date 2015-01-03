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
package org.que.db.entities;

import java.io.Serializable;

/**
 * Represents the paper authors entity, which contains the values for 
 * a row of the paper authors table.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PaperAuthorsEntity implements Serializable {
  
  private Integer authorID;
  private Integer paperID;

  public PaperAuthorsEntity() {
  }

  public PaperAuthorsEntity(Integer authorID, Integer paperID) {
    this.authorID = authorID;
    this.paperID = paperID;
  }

  public Integer getAuthorID() {
    return authorID;
  }

  public void setAuthorID(Integer authorID) {
    this.authorID = authorID;
  }

  public Integer getPaperID() {
    return paperID;
  }

  public void setPaperID(Integer paperID) {
    this.paperID = paperID;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 23 * hash + (this.authorID != null ? this.authorID.hashCode() : 0);
    hash = 23 * hash + (this.paperID != null ? this.paperID.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PaperAuthorsEntity other = (PaperAuthorsEntity) obj;
    if (this.authorID != other.authorID && (this.authorID == null || !this.authorID.equals(other.authorID))) {
      return false;
    }
    if (this.paperID != other.paperID && (this.paperID == null || !this.paperID.equals(other.paperID))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "PaperAuthorsEntity{" + "authorID=" + authorID + ", paperID=" + paperID + '}';
  }
  
}
