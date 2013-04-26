package com.polycom.edge.webadmin.remote.rest;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class EntityClass extends BaseBean implements Serializable{


		private static final long serialVersionUID = 10004L;
		@Id@GeneratedValue(strategy = GenerationType.AUTO) @Column(length=10)
		private Integer id;
		@Column(nullable=false,length=80)
		private String filePath;
		@Column
		private Boolean visible = true;
		@Column(length=200)
		private String fileDescription;
		@Column
		private Boolean shared = false;

		public EntityClass(){}
		public EntityClass(String fielPath){
			this.filePath = fielPath;
		}

	
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		
		
		public String getFilePath() {
			return filePath;
		}
		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
		
		
		public String getFileDescription() {
			return fileDescription;
		}
		public void setFileDescription(String fileDescription) {
			this.fileDescription = fileDescription;
		}
		
		
		public Boolean getShared() {
			return shared;
		}
		public void setShared(Boolean shared) {
			this.shared = shared;
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
			EntityClass other = (EntityClass) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
		

}
