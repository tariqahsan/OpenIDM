package mil.dtic.openidm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private String _id;   // ForgeRock IDM often uses _id or _ref
    private String userName;
    private String givenName;
    private String sn;
    private String mail;

    // getters / setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }

    public String getSn() { return sn; }
    public void setSn(String sn) { this.sn = sn; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    @Override
    public String toString() {
        return "UserDto{" +
                "_id='" + _id + '\'' +
                ", userName='" + userName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", sn='" + sn + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}

