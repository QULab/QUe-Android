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
package de.tel.quenference.db.entities;

/**
 * Represents the author entity, which contains the values for 
 * a row of the author table.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AuthorEntity extends BaseEntity {
  private String affiliation;
  private String firstName;
  private Integer id;
  private String lastName;

  public AuthorEntity() {
  }

  public AuthorEntity(String affiliation, String firstName, Integer id, String lastName) {
    this.affiliation = affiliation;
    this.firstName = firstName;
    this.id = id;
    this.lastName = lastName;
  }
  
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getAffiliation() {
    return affiliation;
  }

  public void setAffiliation(String affiliation) {
    this.affiliation = affiliation;
  }
  
  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
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
    final AuthorEntity other = (AuthorEntity) obj;
    if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "AuthorEntity{" + "id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", affiliation=" + affiliation + '}';
  }
}
