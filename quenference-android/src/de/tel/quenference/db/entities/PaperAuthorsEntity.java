/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tel.quenference.db.entities;

import java.io.Serializable;

/**
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
