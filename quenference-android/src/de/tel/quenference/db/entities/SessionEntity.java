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
 * Represents the session entity, which contains the values for 
 * a row of the session table.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class SessionEntity extends BaseEntity {
  
  private String chair;
  private String code;
  private String coChair;
  private String datetime;
  private Integer day;
  private Boolean favorite;
  private Integer id;
  private Integer length;
  private String room;
  private String title;
  private String type;
  private String typeName;
  
  public SessionEntity() {
  }

  public SessionEntity(String chair, String code, String coChair, String datetime, Integer day, Integer favorite, Integer id, Integer length, String room, String title, String type, String typeName) {
    this.chair = chair;
    this.code = code;
    this.coChair = coChair;
    this.datetime = datetime;
    this.day = day;
    this.favorite = favorite != 0;
    this.id = id;
    this.length = length;
    this.room = room;
    this.title = title;
    this.type = type;
    this.typeName = typeName;
  }

  
  
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getDay() {
    return day;
  }

  public void setDay(Integer day) {
    this.day = day;
  }

  public String getDatetime() {
    return datetime;
  }

  public void setDatetime(String datetime) {
    this.datetime = datetime;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  public String getChair() {
    return chair;
  }

  public void setChair(String chair) {
    this.chair = chair;
  }

  public String getCoChair() {
    return coChair;
  }

  public void setCoChair(String coChair) {
    this.coChair = coChair;
  }

  public Boolean getFavorite() {
    return favorite;
  }

  public void setFavorite(Boolean favorite) {
    this.favorite = favorite;
  }
  
  

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
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
    final SessionEntity other = (SessionEntity) obj;
    if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "SessionEntity{" + "id=" + id + ", day=" + day + ", datetime=" + datetime + ", title=" + title + ", type=" + type + ", typeName=" + typeName + ", code=" + code + ", length=" + length + ", room=" + room + ", chair=" + chair + ", coChair=" + coChair + '}';
  }

}
