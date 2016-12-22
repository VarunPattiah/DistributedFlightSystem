package front;

import java.io.Serializable;

public class Message implements Serializable{

	
	private static final long serialVersionUID = 8222295000987513816L;
	/**
	 * 
	 */
	private Long messageID;
	private int processID;
	private String message;
	private String method;
	private String managerId;
	private String firstName;
	private String lastName;
	private String destination;
	private String status;
	private String dateofBooking;
	private String classType;
	private String recordType;
	private String fieldName;
	private String passengerID;
	private String currentCity;
	private String otherCity;
	private String newValue;
	private String address;
	private String phone;
	private String recordId; 
	private String remoteServerName;
	private String messageType;
	
	
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getDateofBooking() {
		return dateofBooking;
	}
	public void setDateofBooking(String dateofBooking) {
		this.dateofBooking = dateofBooking;
	}
	public String getClassType() {
		return classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getPassengerID() {
		return passengerID;
	}
	public void setPassengerID(String passengerID) {
		this.passengerID = passengerID;
	}
	public String getCurrentCity() {
		return currentCity;
	}
	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}
	public String getOtherCity() {
		return otherCity;
	}
	public void setOtherCity(String otherCity) {
		this.otherCity = otherCity;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}
	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	/**
	 * @return the messageID
	 */
	public Long getMessageID() {
		return messageID;
	}
	/**
	 * @param messageID the messageID to set
	 */
	public void setMessageID(Long messageID) {
		this.messageID = messageID;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the badgeId
	 */
	public String getManagerId() {
		return managerId;
	}
	/**
	 * @param managerId the badgeId to set
	 */
	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the processID
	 */
	public int getProcessID() {
		return processID;
	}
	/**
	 * @param processID the processID to set
	 */
	public void setProcessID(int processID) {
		this.processID = processID;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the lastDate
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the lastDate to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the recordId
	 */
	public String getRecordId() {
		return recordId;
	}
	/**
	 * @param recordId the recordId to set
	 */
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	/**
	 * @return the remoteServerName
	 */
	public String getRemoteServerName() {
		return remoteServerName;
	}
	/**
	 * @param remoteServerName the remoteServerName to set
	 */
	public void setRemoteServerName(String remoteServerName) {
		this.remoteServerName = remoteServerName;
	}
	
	
}
