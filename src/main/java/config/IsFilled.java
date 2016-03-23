package config;

public class IsFilled {
    private boolean isFilled = false;
    private boolean isNeedRestart = false;

    public boolean isFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }

    public boolean isNeedRestart() {
        return isNeedRestart;
    }

    public void setIsNeedRestart(boolean isNeedRestart) {
        this.isNeedRestart = isNeedRestart;
    }
}
