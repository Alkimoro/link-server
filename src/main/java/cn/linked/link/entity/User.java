package cn.linked.link.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    public static final String STRING_KEY_ID = "userId";

    private Long id;

    private String name;
    private String password;
    private int gender;
    private String birthday;
    private String mail;
    private String phoneNumber;
    private String address;
    private String signature;
    private String imageUrl;
    private Date modifyTime;
    private Date createTime;

}
