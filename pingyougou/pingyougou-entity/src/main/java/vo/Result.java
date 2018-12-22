package vo;

public class Result {

    private Boolean success;
    private String message;

    //执行成功
    public static Result ok(String message){
        return new Result(true,message);
    }

    //执行失败
    public static Result fail(String message){
        return new Result(false,message);
    }

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
