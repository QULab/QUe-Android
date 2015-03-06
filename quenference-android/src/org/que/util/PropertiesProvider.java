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
package org.que.util;


import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads properties from properties file 
 * if the property is not found it will try the system properties.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PropertiesProvider {
  
  /**
   * The name of the property file.
   */
  public static final String PROPERTY_FILE = "quenference.properties";
  
  /**
   * The property which should contains the url
   * for downloading the sessions content.
   */
  public static final String SESSIONS_URL_PROP = "sessions.url";
  
  /**
   * The property which should contains the url
   * for downloading the authors content.
   */
  public static final String AUTHORS_URL_PROP = "authors.url";
  
  
  /**
   * The property which should contains the url
   * for downloading the papers content.
   */
  public static final String PAPERS_URL_PROP = "papers.url";
  
  /**
   * The property which should contain the boolean value if the download is allowed or not.
   */
  public static final String PAPER_DOWNLOAD_KEY = "paper.download";
  
  //============================================================
  
  /**
   * The instance of the PropertiesProvider class.
   */
  private static final PropertiesProvider instance = new PropertiesProvider();
  
  /**
   * The key-value pairs named as properties.
   */
  private final Properties properties;

  /**
   * The default constructor to create a properties provider object.
   */
  private PropertiesProvider() {
    properties = new Properties();
    init();
  }

  /**
   * Returns the instance of the PropertiesProvider.
   * 
   * @return                      the instance
   */
  public static PropertiesProvider getInstance() {
    return instance;
  }
  
  /**
   * Returns the value for the given composite property.
   * The property must contain a template which will be replaced
   * by the composite property value
   * 
   * @param property              the property
   * @param composite             the composite value
   * @return                      the value
   */
  public String getCompositeProperty(String property, String composite) {
    return getProperty(MessageFormat.format(property, composite));
  }
  
  /**
   * Returns an array of values for the given property.
   * 
   * @param property              the property
   * @return                      the values
   */
  public String[] getPropertyArray(String property) {
    String[] pArray = null;
    String p = getProperty(property);
    if (p == null) 
      p = System.getProperty(property);
    if (p != null) {
      if (p.contains(",")) {
        pArray = p.split(",");
      } else {
        pArray = new String[1];
        pArray[0] = p;
      }
    }
    return pArray;
  }
  
  /**
   * Returns the property value for the given property.
   * 
   * @param key                   the property
   * @return                      the value of the property
   */
  public String getProperty(String key) {
    if (properties.isEmpty())
      init();
    
    String p = properties.getProperty(key);
    return p == null ? System.getProperty(key) : p;
  }
  
  /**
   * Returns the property value for the given property.
   * 
   * @param key                   the property
   * @param defaultValue          the default value
   * @return                      the value of the property
   */
  public String getProperty(String key, String defaultValue) {
    if (properties.isEmpty())
      init();
    
    String p = properties.getProperty(key, defaultValue);
    
    return p == null ? System.getProperty(key, defaultValue) : p; 
  }
  
  /**
   * Initialized the PropertiesProvider object.
   * Reads the property file and saves the values into the Properties object.
   */
  private void init() { 
    try {
      //load a properties file from class path, inside static method
      Class c = this.getClass();
      InputStream stream = c.getResourceAsStream(PROPERTY_FILE);
      if (stream != null)
        properties.load(stream);

    } catch (IOException ex) {
      Logger.getLogger(PropertiesProvider.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
    }
  }
}