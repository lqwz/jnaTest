package com.liqwer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class PropertyTest {
 public static void main(String[] args) { 
  PropertyTest loadProp = new PropertyTest(); 
  InputStream in = loadProp.getClass().getResourceAsStream("/a.properties");
  Properties prop = new Properties(); 
  try {
   prop.load(in); 
  } catch (IOException e) { 
   e.printStackTrace(); 
  }
  Set<String> strings = prop.stringPropertyNames();
  System.out.println(strings);
  System.out.println(prop.getProperty("name")); 
  System.out.println(prop.getProperty("age"));
 } 
}