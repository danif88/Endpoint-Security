package endpoint.security.response;

/**
 * 
 * @author danielaahumada
 * Response generic format.
 */

public class AppResponse {
	
    private int status;
    private String error;
    private String data;

    public AppResponse(int status, String error, String data) {
        this.status=status;
        this.error=error;
        this.data=data;
    }

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getData() {
		return data;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setError(String error) {
		this.error=error;
	}
    
}
