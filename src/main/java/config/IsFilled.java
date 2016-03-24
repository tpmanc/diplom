package config;

public class IsFilled {
    private boolean isFilled = false;
    private boolean isNeedRestart = false;

    private boolean adUrl = true;
    private boolean adManager = true;
    private boolean adPassword = true;
    private boolean adUserSearch = true;
    private boolean adGroupSearch = true;
    private boolean adGroupFilter = true;
    private boolean adRoleAttribute = true;

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

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public void setNeedRestart(boolean needRestart) {
        isNeedRestart = needRestart;
    }

    public boolean isAdUrl() {
        return adUrl;
    }

    public void setAdUrl(boolean adUrl) {
        this.adUrl = adUrl;
    }

    public boolean isAdManager() {
        return adManager;
    }

    public void setAdManager(boolean adManager) {
        this.adManager = adManager;
    }

    public boolean isAdPassword() {
        return adPassword;
    }

    public void setAdPassword(boolean adPassword) {
        this.adPassword = adPassword;
    }

    public boolean isAdUserSearch() {
        return adUserSearch;
    }

    public void setAdUserSearch(boolean adUserSearch) {
        this.adUserSearch = adUserSearch;
    }

    public boolean isAdGroupSearch() {
        return adGroupSearch;
    }

    public void setAdGroupSearch(boolean adGroupSearch) {
        this.adGroupSearch = adGroupSearch;
    }

    public boolean isAdGroupFilter() {
        return adGroupFilter;
    }

    public void setAdGroupFilter(boolean adGroupFilter) {
        this.adGroupFilter = adGroupFilter;
    }

    public boolean isAdRoleAttribute() {
        return adRoleAttribute;
    }

    public void setAdRoleAttribute(boolean adRoleAttribute) {
        this.adRoleAttribute = adRoleAttribute;
    }
}
