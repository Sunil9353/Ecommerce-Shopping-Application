package com.ecommerce.shopping.utility;

public class ResponseStructure<T> {
	private String message;
	private int status;
	private T data;
	public String getMessage() {
		return message;
	}
	public int getStatus() {
		return status;
	}
	public T getData() {
		return data;
	}
	public ResponseStructure<T> setMessage(String message) {
		this.message = message;
		return this;
	}
	public ResponseStructure<T> setStatus(int status) {
		this.status = status;
		return this;
	}
	public ResponseStructure<T> setData(T data) {
		this.data = data;
		return this;
	}
	
	

}
