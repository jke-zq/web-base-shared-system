package cn.edu.ycu.webadmin.remote.rest;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class URIResource {
    @XmlElement
	private String uriResource;
    @XmlElement
    private String className;
    @XmlElement
	private String getMethodName;
    @XmlElement
	private String putMethodName;
    @XmlElement
	private String postMethodName;
    @XmlElement
	private String deleteMethodName;
    @XmlElement
	private String postArgClass;
    @XmlElement
	private String postRtnClass;
    @XmlElement
	private String putArgClass;
    @XmlElement
	private String putRtnClass;
    @XmlElement
	private String delArgClass;
    @XmlElement
	private String delRtnClass;
    @XmlElement
    private HashMap<String,String> aliasMap;
    private HashMap<String,String> putMethodsMap;
    private HashMap<String,String> postMethodsMap;
    private HashMap<String,String> deleteMethodsMap;
    
	public HashMap<String, String> getPutMethodsMap() {
		return putMethodsMap;
	}

	public void setPutMethodsMap(HashMap<String, String> putMethodsMap) {
		this.putMethodsMap = putMethodsMap;
	}

	public HashMap<String, String> getPostMethodsMap() {
		return postMethodsMap;
	}

	public void setPostMethodsMap(HashMap<String, String> postMethodsMap) {
		this.postMethodsMap = postMethodsMap;
	}

	public HashMap<String, String> getDeleteMethodsMap() {
		return deleteMethodsMap;
	}

	public void setDeleteMethodsMap(HashMap<String, String> deleteMethodsMap) {
		this.deleteMethodsMap = deleteMethodsMap;
	}

	public URIResource(){}

	public URIResource(String uriResource, String className, String getMethodName, String putMethodName,
         String postMethodName, String deleteMethodName, String postArgClass, String postRtnClass,
         String putArgClass, String putRtnClass, String delArgClass, String delRtnClass)
   {
      super();
      this.uriResource = uriResource;
      this.className = className;
      this.getMethodName = getMethodName;
      this.putMethodName = putMethodName;
      this.postMethodName = postMethodName;
      this.deleteMethodName = deleteMethodName;
      this.postArgClass = postArgClass;
      this.postRtnClass = postRtnClass;
      this.putArgClass = putArgClass;
      this.putRtnClass = putRtnClass;
      this.delArgClass = delArgClass;
      this.delRtnClass = delRtnClass;
   }

	public String getUriResource()
   {
      return uriResource;
   }

   public void setUriResource(String uriResource)
   {
      this.uriResource = uriResource;
   }

   public String getClassName()
   {
      return className;
   }

   public void setClassName(String className)
   {
      this.className = className;
   }

   public String getGetMethodName()
   {
      return getMethodName;
   }

   public void setGetMethodName(String getMethodName)
   {
      this.getMethodName = getMethodName;
   }

   public String getPutMethodName()
   {
      return putMethodName;
   }

   public void setPutMethodName(String putMethodName)
   {
      this.putMethodName = putMethodName;
   }

   public String getPostMethodName()
   {
      return postMethodName;
   }

   public void setPostMethodName(String postMethodName)
   {
      this.postMethodName = postMethodName;
   }

   public String getDeleteMethodName()
   {
      return deleteMethodName;
   }

   public void setDeleteMethodName(String deleteMethodName)
   {
      this.deleteMethodName = deleteMethodName;
   }

   public String getPostArgClass()
   {
      return postArgClass;
   }

   public void setPostArgClass(String postArgClass)
   {
      this.postArgClass = postArgClass;
   }

   public String getPostRtnClass()
   {
      return postRtnClass;
   }

   public void setPostRtnClass(String postRtnClass)
   {
      this.postRtnClass = postRtnClass;
   }

   public String getPutArgClass()
   {
      return putArgClass;
   }

   public void setPutArgClass(String putArgClass)
   {
      this.putArgClass = putArgClass;
   }

   public String getPutRtnClass()
   {
      return putRtnClass;
   }

   public void setPutRtnClass(String putRtnClass)
   {
      this.putRtnClass = putRtnClass;
   }

   public String getDelArgClass()
   {
      return delArgClass;
   }

   public void setDelArgClass(String delArgClass)
   {
      this.delArgClass = delArgClass;
   }

   public String getDelRtnClass()
   {
      return delRtnClass;
   }

   public void setDelRtnClass(String delRtnClass)
   {
      this.delRtnClass = delRtnClass;
   }

   public HashMap<String, String> getAliasMap()
   {
      return aliasMap;
   }

   public void setAliasMap(HashMap<String, String> aliasMap)
   {
      this.aliasMap = aliasMap;
   }

   @Override
   public String toString()
   {
      return "URIResource [uriResource=" + uriResource + ", className=" + className + ", getMethodName="
            + getMethodName + ", putMethodName=" + putMethodName + ", postMethodName=" + postMethodName
            + ", deleteMethodName=" + deleteMethodName + ", postArgClass=" + postArgClass + ", postRtnClass="
            + postRtnClass + ", putArgClass=" + putArgClass + ", putRtnClass=" + putRtnClass
            + ", delArgClass=" + delArgClass + ", delRtnClass=" + delRtnClass + ", aliasMap=" + aliasMap
            + "]";
   }

}
