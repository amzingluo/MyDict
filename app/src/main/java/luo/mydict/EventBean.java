package luo.mydict;

public class EventBean {

    private int type;
    public static final int TYPE_UPDATE_WORD_LAUNCH=1;
    public static final int TYPE_UPDATE_WORD_HARD=1;
    public EventBean() {
    }
    public EventBean(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
