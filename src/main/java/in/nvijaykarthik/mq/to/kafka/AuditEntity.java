package in.nvijaykarthik.mq.to.kafka;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class AuditEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long id;
	
	@Column
	String messageId;
	
	@Column
	String bussinessRefId;
	
	@Column
	@Lob
	String payload;
	
	@Column
	@Lob
	String headers;
	
	@Column
	String component;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	Date createdTime=new Date();
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getBussinessRefId() {
		return bussinessRefId;
	}
	public void setBussinessRefId(String bussinessRefId) {
		this.bussinessRefId = bussinessRefId;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHeaders() {
		return headers;
	}
	public void setHeaders(String headers) {
		this.headers = headers;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
}
