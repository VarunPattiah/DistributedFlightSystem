package server;

import java.io.Serializable;

import idl.interfaces.PassengerVO;


public class PassengerWrapperVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private configuration.REQUEST_TYPE requestType;
	private String requestId;
	private PassengerVO passengerVO = new PassengerVO();
	private configuration.PASSENGER_STATUS deletePassengerStatus;
	private configuration.PASSENGER_STATUS passengerStatus;
	private String recordType;
	public configuration.REQUEST_TYPE getRequestType() {
		return requestType;
	}
	public void setRequestType(configuration.REQUEST_TYPE requestType) {
		this.requestType = requestType;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public PassengerVO getPassengerVO() {
		return passengerVO;
	}
	public void setPassengerVO(PassengerVO passengerVO) {
		this.passengerVO = passengerVO;
	}
	public configuration.PASSENGER_STATUS getDeletePassengerStatus() {
		return deletePassengerStatus;
	}
	public void setDeletePassengerStatus(configuration.PASSENGER_STATUS deletePassengerStatus) {
		this.deletePassengerStatus = deletePassengerStatus;
	}
	public configuration.PASSENGER_STATUS getPassengerStatus() {
		return passengerStatus;
	}
	public void setPassengerStatus(configuration.PASSENGER_STATUS passengerStatus) {
		this.passengerStatus = passengerStatus;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}



}
