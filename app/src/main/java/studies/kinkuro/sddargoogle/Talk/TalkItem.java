package studies.kinkuro.sddargoogle.Talk;

/**
 * Created by alfo6-2 on 2018-05-18.
 */

public class TalkItem {

    private int no;
    private String Title;
    private String name;
    private String msg;
    private String imgUrl;
    private String Date;

    public TalkItem() {    }

    public TalkItem(int no, String title, String name, String msg, String imgUrl, String date) {
        this.no = no;
        Title = title;
        this.name = name;
        this.msg = msg;
        this.imgUrl = imgUrl;
        Date = date;
    }

    public int getNo() {        return no;    }
    public void setNo(int no) {        this.no = no;    }

    public String getTitle() {        return Title;    }
    public void setTitle(String title) {        Title = title;    }

    public String getName() {        return name;    }
    public void setName(String name) {        this.name = name;    }

    public String getMsg() {        return msg;    }
    public void setMsg(String msg) {        this.msg = msg;    }

    public String getImgUrl() {        return imgUrl;    }
    public void setImgUrl(String imgUrl) {        this.imgUrl = imgUrl;    }

    public String getDate() {        return Date;    }
    public void setDate(String date) {        Date = date;    }

}
