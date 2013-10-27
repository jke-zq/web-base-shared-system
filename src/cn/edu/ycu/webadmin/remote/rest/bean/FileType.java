package cn.edu.ycu.webadmin.remote.rest.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import cn.edu.ycu.webadmin.remote.rest.BaseBean;



@Entity
public class FileType extends BaseBean implements Serializable{

	private static final long serialVersionUID = 10001L;
	@Id@GeneratedValue(strategy = GenerationType.AUTO) @Column(length=10)
	private Integer id;
	@Column(length=20)
	private String name;
	@Column(length=50)
	private String typeDesc;
	@Column
	private Boolean visible = true;
	@OneToMany(mappedBy="parentType",cascade={CascadeType.REFRESH,CascadeType.REMOVE})
	private Set<FileType> childType = new HashSet<FileType>();
	@ManyToOne(cascade=CascadeType.REFRESH)
	@JoinColumn(name="parentId")
	private FileType parentType;
	
	public FileType(){}
	
	public FileType(Integer id){
		this.id = id;
	}
	
	public FileType(String typeName){
		this.name = typeName;
	}
	
	public FileType(String typeName, String typeDesc){
		this.name = typeName;
		this.setTypeDesc(typeDesc);
	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer typeId) {
		this.id = typeId;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String typeName) {
		this.name = typeName;
	}
	

	
	
	
	public Set<FileType> getChildType() {
		return childType;
	}

	public void setChildType(Set<FileType> childType) {
		this.childType = childType;
	}
	

	public FileType getParentType() {
		return parentType;
	}

	public void setParentType(FileType parentType) {
		this.parentType = parentType;
	}
	

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	
	public String listValue(String sep){
		
		if(sep == null || "".equals(sep)){
			sep = "#";
		}
		
		return this.id + sep + this.name + sep + this.getTypeDesc() +sep +this.childType.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		FileType other = (FileType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	
	
	
}
