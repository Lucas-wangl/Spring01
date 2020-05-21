package cn.edu.scujcc.model;

public class Result<T> {
	public static final int OK = 1;
	public static final int DUPLICATED = -1;
	public static final int ERROR = 0;
	private int status;
	private String message;
	private T data;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public Result<T> ok(){
		Result<T> result = new Result<T>();
		result.setStatus(OK);
		result.setMessage("�����ɹ�");
		return result;
	}
	public Result<T> error(){
		Result<T> result = new Result<T>();
		result.setStatus(ERROR);
		result.setMessage("����ʧ��");
		return result;
	}
}
