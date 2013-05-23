package cn.edu.ycu.webadmin.remote.rest;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;




@Entity
public class UserInfo extends BaseBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 10002L;
	@Id @GeneratedValue(strategy = GenerationType.AUTO) @Column(length=10)
	private Long id;
	@Column(length=20,nullable=false)
	private String name;
	@Column(length=20,nullable=false)
	private String password;
	@Column(length=20)
	private String other;
	@Column
	private Boolean visible = true;
	@OneToMany(mappedBy = "user",cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private Set<FileInfo> files = new HashSet<FileInfo>();
	
	public UserInfo(){	}
	public UserInfo(String name){
		this.name = name;
	}
	public UserInfo(String name, String passwd){
		this.name = name;
		this.password = passwd;
	}
	
	
	public Long getUserId(){
		return this.id;
	}
	public void setUserId(Long userId) {
		this.id = userId;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public Set<FileInfo> getFiles() {
		return files;
	}
	public void setFiles(Set<FileInfo> files) {
		this.files = files;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfo other = (UserInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		return "name:" + this.name + " password:" + password;
		 
	}
	
	

}
