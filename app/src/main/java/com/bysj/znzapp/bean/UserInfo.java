package com.bysj.znzapp.bean;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @Author: huowei
 * @CreateDate: 2021/3/19 11:39
 * @Describe:
 * @Version: 1.2.5
 */
@Data
public class UserInfo {
    /**
     * userId : test
     * userName : test
     * avatar : /path.png
     * post : 网格员
     * token : 123
     * departName : 西门社区第一网格
     * xzName : 雉城街道
     */

    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String userId;
    private String account;
    private String userName;
    private String avatar;
    private String post;
    private String postStr;
    private String token;
    private String departName;
    private String xzName;
    private String email;
    private String phone;
    private boolean isJdUser;
    private String displayDeptName;
    private String userDataLevel;
    @Transient
    private List<String> actionCodes;
    @Transient
    private List<AccountUser> accountUsers;
    //智慧党建	party_building
    //人员走访	staff_visit
    //巡场排查	patrol_investigation
    //创建直播	create_live
    //视频监控	video_monitor
    //工作轨迹	work_track
    //视频通讯	video_communication(乡镇)   x_video_communication(县级)  这两个权限一般不能共存

    //通知公告：announcement
    //工作动态：work_dynamics
    //经验交流：exper_exchange
    //待办事件：upcoming_event
    //工作日志：work_log
    //事件管理：event_management
    //人口管理：person_management
    //人员流动：staff_flow
    //实有人口：exactly_person
    //重点管控人员：focuscontrol_person
    //统计分析：statistical_analysis
    //场所管理：place_management
    //组织管理：organization_management
    //非公有制经济组织：nopublic_management
    //社会组织：social_organization
    //组织统计：organization_analysis

    //巡查管理:
    //	待办任务：task_01
    //	已办任务：task_02
    //	指派任务：task_03
    //	日常巡查：Patrol_01
    //	专项检查：Patrol_02
    //	巡查记录：Patrol_03

    //  place_operate 场所流入流出权限

    //场所管理：
    //	娱乐场所：place_01
    //	餐饮场所：place_02
    //	生活配套：place_03
    //	酒店住宿：place_04
    //	交通水利：place_05
    //	寄递业场所：place_07
    //	文化教育：place_08
    //	医疗卫生：place_09
    //	旅游景点：place_10
    //	金融机构：place_11
    //	工矿企业：place_12
    //	建筑工地：place_13
    //	护路护线：place_14
    //	宗教活动场所：place_15
    //	其他：place_16
    //	住宅小区：place_17
    //	场所总况：place_00
    //	场所统计：place_99

    @Generated(hash = 1279772520)
    public UserInfo() {
    }

    @Generated(hash = 1943820381)
    public UserInfo(Long id, String userId, String account, String userName,
                    String avatar, String post, String postStr, String token,
                    String departName, String xzName, String email, String phone,
                    boolean isJdUser, String displayDeptName, String userDataLevel) {
        this.id = id;
        this.userId = userId;
        this.account = account;
        this.userName = userName;
        this.avatar = avatar;
        this.post = post;
        this.postStr = postStr;
        this.token = token;
        this.departName = departName;
        this.xzName = xzName;
        this.email = email;
        this.phone = phone;
        this.isJdUser = isJdUser;
        this.displayDeptName = displayDeptName;
        this.userDataLevel = userDataLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        if (userId == null) {
            return "";
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        if (account == null) {
            return "";
        }
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserName() {
        if (userName == null) {
            return "";
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        if (avatar == null) {
            return "";
        }
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPost() {
        if (post == null) {
            return "";
        }
        return post;
    }

    public String getPostStr() {
        if (postStr == null) {
            return "";
        }
        return postStr;
    }

    public void setPostStr(String postStr) {
        this.postStr = postStr;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getToken() {
        if (token == null) {
            return "";
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDepartName() {
        if (departName == null) {
            return "";
        }
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getXzName() {
        if (xzName == null) {
            return "";
        }
        return xzName;
    }

    public void setXzName(String xzName) {
        this.xzName = xzName;
    }

    public String getEmail() {
        if (email == null) {
            return "";
        }
        return email;
    }

    public String getDisplayDeptName() {
        return displayDeptName;
    }

    public void setDisplayDeptName(String displayDeptName) {
        this.displayDeptName = displayDeptName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        if (phone == null) {
            return "";
        }
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getActionCodes() {
        if (actionCodes == null) {
            return actionCodes = new ArrayList<>();
        }
        return actionCodes;
    }

    public void setActionCodes(List<String> actionCodes) {
        this.actionCodes = actionCodes;
    }

    public boolean isJdUser() {
        return isJdUser;
    }

    public void setJdUser(boolean jdUser) {
        isJdUser = jdUser;
    }

    public boolean getIsJdUser() {
        return this.isJdUser;
    }

    public void setIsJdUser(boolean isJdUser) {
        this.isJdUser = isJdUser;
    }

    public String getUserDataLevel() {
        return userDataLevel;
    }

    public void setUserDataLevel(String userDataLevel) {
        this.userDataLevel = userDataLevel;
    }

    public List<AccountUser> getAccountUsers() {
        return accountUsers;
    }

    public void setAccountUsers(List<AccountUser> accountUsers) {
        this.accountUsers = accountUsers;
    }

    public static class AccountUser {

        /**
         * userId : ADMIN-1
         * userName : 长兴广电
         * lxdh : null
         * dzxj : null
         * ssdwbs : 1143492092887040
         * lrr : ADMIN
         * rxrq : 2018-11-01 09:45:40
         * isadmin : 0
         * defaultuser : ADMIN
         * isstate : 1
         * displayName : 雉城街道
         * departmentName : 雉城街道
         */

        private String userId;
        private String userName;
        private String lxdh;
        private String dzxj;
        private String ssdwbs;
        private String lrr;
        private String rxrq;
        private String isadmin;
        private String defaultuser;
        private String isstate;
        private String displayName;
        private String departmentName;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getLxdh() {
            return lxdh;
        }

        public void setLxdh(String lxdh) {
            this.lxdh = lxdh;
        }

        public String getDzxj() {
            return dzxj;
        }

        public void setDzxj(String dzxj) {
            this.dzxj = dzxj;
        }

        public String getSsdwbs() {
            return ssdwbs;
        }

        public void setSsdwbs(String ssdwbs) {
            this.ssdwbs = ssdwbs;
        }

        public String getLrr() {
            return lrr;
        }

        public void setLrr(String lrr) {
            this.lrr = lrr;
        }

        public String getRxrq() {
            return rxrq;
        }

        public void setRxrq(String rxrq) {
            this.rxrq = rxrq;
        }

        public String getIsadmin() {
            return isadmin;
        }

        public void setIsadmin(String isadmin) {
            this.isadmin = isadmin;
        }

        public String getDefaultuser() {
            return defaultuser;
        }

        public void setDefaultuser(String defaultuser) {
            this.defaultuser = defaultuser;
        }

        public String getIsstate() {
            return isstate;
        }

        public void setIsstate(String isstate) {
            this.isstate = isstate;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

    }
}
