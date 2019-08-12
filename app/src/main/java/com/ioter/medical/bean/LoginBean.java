package com.ioter.medical.bean;


public class LoginBean extends BaseEntity {

    /**
     * access_token : s6OO3jRUOd52O1LE-6PlwQhdr9-ORYWXPbuU0l-1kJil7iNgHpwyojTRizv9WsKridjf4-nw0HxWdUnvytgJ7F_akt4htGTYmeubbblDd5NAAXjbHMNTOaJihQ9rT-pgYMT74SiRELfeWbQ4tYA0IcutQ5Tizp-4V4_Nd694o0kqnrHyUzt7qZ259JkEINyzCJY-5x8T5w5HLHj4p7ACgAscHt8Bxi3GGkdVETgpeC13AV40gdom5yATCSpI_RqXQ3mA77SCTYPUFV_Grmx5wd02GAb12fI6J86XhWjoGBR9opE_oAvgBpK9L9uFgrhT
     * token_type : bearer
     * expires_in : 3599
     * as:client_id :
     * UserName : admin
     * .issued : Wed, 06 Mar 2019 06:00:58 GMT
     * .expires : Wed, 06 Mar 2019 07:00:58 GMT
     */

    private String access_token;
    private String RealName;
    private String Id;

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
