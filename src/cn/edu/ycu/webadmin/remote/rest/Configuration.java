package cn.edu.ycu.webadmin.remote.rest;



import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import cn.edu.ycu.webadmin.remote.rest.resource.URIResource;


@XmlRootElement
public class Configuration {
	
	private List<URIResource> uriList = new ArrayList<URIResource>();
	
	public Configuration(List<URIResource> list) {
		super();
		this.uriList = list;
	}
	
	public Configuration() {
	}
	@XmlElementWrapper(name = "uriResourceList")
	@XmlElement(name="uriResource")
	public void setUriList(List<URIResource> list) {
		this.uriList = list;
	}
	
	public List<URIResource> getUriList() {
		return uriList;
	}


	public void AddUri(URIResource uri){
		this.uriList.add(uri);
	}

	public static Configuration loadConf(String fileName) {
		try {
			final JAXBContext jaxbContext = JAXBContext
					.newInstance(Configuration.class);
			Configuration conf = (Configuration) jaxbContext
					.createUnmarshaller()
					.unmarshal(new File(fileName));
			System.out.println("unmarshaller result: "+conf.toString());
			return conf;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "Configuration [uriList=" + Arrays.toString(this.uriList.toArray()) + "]";
	}

}
