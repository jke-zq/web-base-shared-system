package cn.edu.ycu.webadmin.remote.rest.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.edu.ycu.webadmin.remote.rest.BaseBean;


@Entity
public class FileInfo extends BaseBean implements Serializable {
	
	    /**
		 * 
		 */
		private static final long serialVersionUID = -7210258366793425401L;
		@Id@GeneratedValue(strategy = GenerationType.AUTO) @Column(length=10)
		private Integer id;
		@ManyToOne(cascade={CascadeType.MERGE,CascadeType.PERSIST})
		@JoinColumn(name="userId")
		private UserInfo user;
		@Column(nullable=false,length=80)
		private String filePath;
		@Column
		private Boolean visible = true;
		@Column(length=200)
		private String fileDescription;
		@Temporal(TemporalType.TIMESTAMP)
		private Date uploadtime = new Date();
		@Column
		private Boolean shared = false;
		@ManyToOne(cascade=CascadeType.PERSIST)
		@JoinColumn(name="typeId")
		private FileType fileType;
		//add for the slice uploading the large file.
		@Column(length=200)
		private String unSlices;
		@Column
		private Boolean finished = false;
		@Column
		private int fileSize = 0;
		
		public FileInfo(){}
		
		public FileInfo(String filePath){
			this.filePath = filePath;
		}

		public FileInfo(String path,String desp, int size){
			this.fileDescription = desp;
			this.filePath = path;
			this.fileSize = size;
		}
		public FileType getFileType() {
			return fileType;
		}
		
		public String getExt(){
			return this.filePath.substring(this.filePath.lastIndexOf('.')+1);
		}
		
		
		public void setFileType(FileType fileType) {
			this.fileType = fileType;
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
		
		
		public Date getUploadtime() {
			return uploadtime;
		}
		public void setUploadtime(Date uploadtime) {
			this.uploadtime = uploadtime;
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
		

		public UserInfo getUser() {
			return user;
		}
		public void setUser(UserInfo user) {
			this.user = user;
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
			FileInfo other = (FileInfo) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
		public String getName(){
			return this.filePath.substring(this.filePath.lastIndexOf("/")+1);
		}
		@Override
		public String toString(){
			return "[userName:" + user.getName() + " description:" + fileDescription + " path:" + filePath  + "]";
		}
		public Boolean getVisible() {
			return visible;
		}
		public void setVisible(Boolean visible) {
			this.visible = visible;
		}

		public Boolean getFinished() {
			return finished;
		}
		public void setFinished(Boolean finished) {
			this.finished = finished;
		}
		public String getUnSlices() {
			return unSlices;
		}
		public void setUnSlices(String unSlices) {
			this.unSlices = unSlices;
		}

		public int getFileSize() {
			return fileSize;
		}

		public void setFileSize(int fileSize) {
			this.fileSize = fileSize;
		}
		
}
