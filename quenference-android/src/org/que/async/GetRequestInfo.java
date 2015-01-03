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
package org.que.async;

/**
 * Represents a key-value pair for an URL and an ETAG.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class GetRequestInfo {
  
  /**
   * The URL.
   */
  private String url;
  
  /**
   * The ETAG for the corresponding URL.
   */
  private String etag;

  /**
   * The ctor of the GetRequestInfo.
   * 
   * @param url   the URL
   * @param etag  the ETAG
   */
  public GetRequestInfo(String url, String etag) {
    this.url = url;
    this.etag = etag;
  }

  /**
   * Returns the URL.
   * 
   * @return    the URL
   */
  public String getUrl() {
    return url;
  }
  
  /**
   * Replaces the URL.
   * @param url     the URL
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Returns the ETAG for the corresponding URL.
   * @return      the ETAG
   */
  public String getEtag() {
    return etag;
  }

  /**
   * Replaces the ETAG for the corresponding URL.
   * @param etag    the ETAG
   */
  public void setEtag(String etag) {
    this.etag = etag;
  }
}
