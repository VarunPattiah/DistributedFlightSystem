package server;

import java.io.Serializable;

import idl.interfaces.PassengerVO;

/**
 * Pojo to send data across server through UDP
 * @author VarunPattiah
 *
 */
public class PassengerWrapperVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Constants.REQUEST_TYPE requestType;
	private String requestId;
	private PassengerVO passengerVO = new PassengerVO();
	private Constants.PASSENGER_STATUS deletePassengerStatus;
	private Constants.PASSENGER_STATUS passengerStatus;
	private String recordType;
	public Constants.REQUEST_TYPE getRequestType() {
		return requestType;
	}
	public void setRequestType(Constants.REQUEST_TYPE requestType) {
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
	public Constants.PASSENGER_STATUS getDeletePassengerStatus() {
		return deletePassengerStatus;
	}
	public void setDeletePassengerStatus(Constants.PASSENGER_STATUS deletePassengerStatus) {
		this.deletePassengerStatus = deletePassengerStatus;
	}
	public Constants.PASSENGER_STATUS getPassengerStatus() {
		return passengerStatus;
	}
	public void setPassengerStatus(Constants.PASSENGER_STATUS passengerStatus) {
		this.passengerStatus = passengerStatus;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}



}
