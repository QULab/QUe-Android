/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tel.quenference.db.entities;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PaperEntity extends BaseEntity {

  private String abstrct;
  private String dateTime;
  private String dateTimeEnd;
  private Boolean favorite;
  private Integer id;
  private String keywords;
  private String mainAuthor;
  private Integer mainAuthorId;
  private String paperCode;
  private Integer session;
  private Integer submissionId;
  private String title;

  public PaperEntity() {
  }

  public PaperEntity(String abstrct, String dateTime, String dateTimeEnd, Integer favorite, Integer id, String keywords, String mainAuthor, Integer mainAuthorId, String paperCode, Integer session, Integer submissionId, String title) {
    this.abstrct = abstrct;
    this.dateTime = dateTime;
    this.dateTimeEnd = dateTimeEnd;
    this.favorite = favorite != 0;
    this.id = id;
    this.keywords = keywords;
    this.mainAuthor = mainAuthor;
    this.mainAuthorId = mainAuthorId;
    this.paperCode = paperCode;
    this.session = session;
    this.submissionId = submissionId;
    this.title = title;
  }

  


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getSubmissionId() {
    return submissionId;
  }

  public void setSubmissionId(Integer submissionId) {
    this.submissionId = submissionId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMainAuthor() {
    return mainAuthor;
  }

  public void setMainAuthor(String mainAuthor) {
    this.mainAuthor = mainAuthor;
  }

  public Integer getMainAuthorId() {
    return mainAuthorId;
  }

  public void setMainAuthorId(Integer mainAuthorId) {
    this.mainAuthorId = mainAuthorId;
  }

  public String getPaperCode() {
    return paperCode;
  }

  public void setPaperCode(String paperCode) {
    this.paperCode = paperCode;
  }

  public String getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  public String getDateTimeEnd() {
    return dateTimeEnd;
  }

  public void setDateTimeEnd(String dateTimeEnd) {
    this.dateTimeEnd = dateTimeEnd;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public String getAbstrct() {
    return abstrct;
  }

  public void setAbstrct(String abstrct) {
    this.abstrct = abstrct;
  }

  public Integer getSession() {
    return session;
  }

  public void setSession(Integer session) {
    this.session = session;
  }

  public Boolean getFavorite() {
    return favorite;
  }

  public void setFavorite(Boolean favorite) {
    this.favorite = favorite;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
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
    final PaperEntity other = (PaperEntity) obj;
    if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "PaperEntity{" + "id=" + id + ", submissionId=" + submissionId + ", title=" + title + ", abstrct=" + abstrct + ", mainAuthor=" + mainAuthor + ", mainAuthorId=" + mainAuthorId + ", paperCode=" + paperCode + ", dateTime=" + dateTime + ", dateTimeEnd=" + dateTimeEnd + ", keywords=" + keywords + ", session=" + session + ", favorite=" + favorite + '}';
  }
}
