package api;

public class UnsuccessReg {
    private String error;

    public UnsuccessReg(String error) {
        this.error = error;
    }
    public UnsuccessReg(){}

    public String getError() {
        return error;
    }
}
