package com.ycjt.rongcloudim.im;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.ycjt.rongcloudim.common.ThreadManager;
import com.ycjt.rongcloudim.db.DbManager;
import com.ycjt.rongcloudim.db.model.FriendDetailInfo;
import com.ycjt.rongcloudim.db.model.FriendShipInfo;
import com.ycjt.rongcloudim.db.model.UserInfo;
import com.ycjt.rongcloudim.model.Resource;
import com.ycjt.rongcloudim.model.Status;
import com.ycjt.rongcloudim.task.UserTask;

import java.util.ArrayList;
import java.util.List;

import io.rong.callkit.RongCallKit;
import io.rong.imkit.RongIM;
import io.rong.imkit.tools.CharacterParser;

public class IMInfoProvider {
    private final MediatorLiveData<Resource> triggerLiveData = new MediatorLiveData<>(); // 同步信息时用于触发事件使用的变量
    private volatile Observer<Resource> emptyObserver;// 空监听用于触发事件
    private volatile boolean groupMemberIsRequest;
    //    private GroupTask groupTask;
    private UserTask userTask;
    //    private FriendTask friendTask;
    private DbManager dbManager;

    public IMInfoProvider() {
    }

    public void init(Context context) {
        initTask(context);
        initInfoProvider(context);
        dbManager = DbManager.getInstance(context);
    }

    /**
     * 初始化同步数据时使用的任务对象
     */
    private void initTask(Context context) {
        //        groupTask = new GroupTask(context.getApplicationContext());
        //        friendTask = new FriendTask(context.getApplicationContext());
        userTask = new UserTask(context.getApplicationContext());
        emptyObserver = resource -> {
            /*
             * 添加此监听只为触发 LiveData 的 onActive 行为，使其他事件可以执行
             * 此处不做更新操作，信息在存储到数据库时会同步更新
             */
        };
        triggerLiveData.observeForever(emptyObserver);
    }

    /**
     * 初始化信息提供者，包括用户信息，群组信息，群主成员信息
     */
    private void initInfoProvider(Context context) {

        /**
         * 设置用户信息的提供者，供 RongIM 调用获取用户名称和头像信息。
         *
         * @param userInfoProvider 用户信息提供者。
         * @param isCacheUserInfo  设置是否由 IMKit 来缓存用户信息。<br>
         *                         如果 App 提供的 UserInfoProvider
         *                         每次都需要通过网络请求用户数据，而不是将用户数据缓存到本地内存，会影响用户信息的加载速度；<br>
         *                         此时最好将本参数设置为 true，由 IMKit 将用户信息缓存到本地内存中。
         * @see UserInfoProvider
         */
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

            @Override
            public io.rong.imlib.model.UserInfo getUserInfo(String userId) {

                io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(userId, "啊明", Uri.parse("http://rongcloud-web.qiniudn.com/docs_demo_rongcloud_logo.png"));
                RongIM.getInstance().refreshUserInfoCache(userInfo);

                return userInfo;//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
            }

        }, true);

        //        // 获取用户信息
        //        RongIM.setUserInfoProvider(id -> {
        //            updateUserInfo(id);
        //            return null;
        //        }, true);

        //        // 获取群组信息
        //        RongIM.setGroupInfoProvider(id -> {
        //            updateGroupInfo(id);
        //            updateGroupMember(id);
        //            return null;
        //        }, true);
        //
        //        // 获取群组单一成员信息
        //        RongIM.setGroupUserInfoProvider((gid, uid) -> {
        //            // 直接进行全部组内成员获取
        //            updateGroupMember(gid);
        //            return null;
        //        }, true);
        //
        //        // 设置群组内成员
        //        //'@' 功能和VoIP功能在选人界面,需要知道群组内成员信息,开发者需要设置该提供者。
        //        RongIM.getInstance().setGroupMembersProvider((gid, callback) -> {
        //            updateIMGroupMember(gid, callback);
        //        });


        //        // RongCallkit 设置 成员信息
        //        RongCallKit.setGroupMemberProvider((groupId, result) -> {
        //            updateCallGroupMember(groupId, result);
        //            return null;
        //        });

    }

    /**
     * 更新用户信息
     *
     * @param userId
     */
    public void updateUserInfo(String userId) {
        ThreadManager.getInstance().runOnUIThread(() -> {
            LiveData<Resource<UserInfo>> userSource = userTask.getUserInfo(userId);
            triggerLiveData.addSource(userSource, resource -> {
                if (resource.status == Status.SUCCESS || resource.status == Status.ERROR) {
                    // 确认成功或失败后，移除数据源
                    // 在请求成功后，会在插入数据时同步更新缓存
                    triggerLiveData.removeSource(userSource);
                }
            });
        });
    }

    //    /**
    //     * 更新群组信息
    //     *
    //     * @param groupId
    //     */
    //    public void updateGroupInfo(String groupId){
    //        ThreadManager.getInstance().runOnUIThread(() -> {
    //            LiveData<Resource<GroupEntity>> groupSource = groupTask.getGroupInfo(groupId);
    //            triggerLiveData.addSource(groupSource, resource -> {
    //                if (resource.status == Status.SUCCESS || resource.status == Status.ERROR) {
    //                    // 确认成功或失败后，移除数据源
    //                    // 在请求成功后，会在插入数据时同步更新缓存
    //                    triggerLiveData.removeSource(groupSource);
    //                }
    //            });
    //        });
    //    }

    //    /**
    //     * 更新群组成员
    //     *
    //     * @param groupId
    //     */
    //    public void updateGroupMember(String groupId){
    //        ThreadManager.getInstance().runOnUIThread(() -> {
    //            // 考虑到在群内频繁调用此方法,当有请求时不进行请求
    //            if (groupMemberIsRequest) return;
    //
    //            groupMemberIsRequest = true;
    //            LiveData<Resource<List<GroupMember>>> groupMemberSource = groupTask.getGroupMemberInfoList(groupId);
    //            triggerLiveData.addSource(groupMemberSource, resource -> {
    //                if (resource.status == Status.SUCCESS || resource.status == Status.ERROR) {
    //                    // 确认成功或失败后，移除数据源
    //                    // 在请求成功后，会在插入数据时同步更新缓存
    //                    triggerLiveData.removeSource(groupMemberSource);
    //                    groupMemberIsRequest = false;
    //                }
    //            });
    //        });
    //    }

    //    /**
    //     * 请求更新 IM 中群组成员
    //     *
    //     * @param groupId
    //     * @param callback
    //     */
    //    private void updateIMGroupMember(String groupId, RongIM.IGroupMemberCallback callback){
    //        ThreadManager.getInstance().runOnUIThread(() -> {
    //            // 考虑到在群内频繁调用此方法,当有请求时进行请求
    //            if (groupMemberIsRequest) return;
    //
    //            groupMemberIsRequest = true;
    //            LiveData<Resource<List<GroupMember>>> groupMemberSource = groupTask.getGroupMemberInfoList(groupId);
    //            triggerLiveData.addSource(groupMemberSource, resource -> {
    //                if (resource.status == Status.SUCCESS || resource.status == Status.ERROR) {
    //                    // 确认成功或失败后，移除数据源
    //                    // 在请求成功后，会在插入数据时同步更新缓存
    //                    triggerLiveData.removeSource(groupMemberSource);
    //                    groupMemberIsRequest = false;
    //
    //                }
    //
    //                if (resource.status == Status.SUCCESS && resource.data != null) {
    //                    List<GroupMember> data = resource.data;
    //                    List<io.rong.imlib.model.UserInfo> userInfoList = new ArrayList<>();
    //                    for (GroupMember member : data) {
    //                        String name = member.getGroupNickName();
    //                        if (TextUtils.isEmpty(name)) {
    //                            name = member.getAlias();
    //                            if (TextUtils.isEmpty(name)) {
    //                                name = member.getName();
    //                            }
    //                        }
    //
    //                        io.rong.imlib.model.UserInfo info = new io.rong.imlib.model.UserInfo(member.getUserId(), name, Uri.parse(member.getPortraitUri()));
    //                        userInfoList.add(info);
    //                    }
    //                    callback.onGetGroupMembersResult(userInfoList);
    //                }
    //            });
    //        });
    //    }

    //    /**
    //     * 请求音视频中更新群组成员
    //     *
    //     * @param groupId
    //     * @param result
    //     */
    //    private void updateCallGroupMember(String groupId, RongCallKit.OnGroupMembersResult result) {
    //        ThreadManager.getInstance().runOnUIThread(() -> {
    //            // 考虑到在群内频繁调用此方法,当有请求时进行请求
    //            if (groupMemberIsRequest) return;
    //
    //            groupMemberIsRequest = true;
    //            LiveData<Resource<List<GroupMember>>> groupMemberSource = groupTask.getGroupMemberInfoList(groupId);
    //            triggerLiveData.addSource(groupMemberSource, resource -> {
    //                if (resource.status == Status.SUCCESS || resource.status == Status.ERROR) {
    //                    // 确认成功或失败后，移除数据源
    //                    // 在请求成功后，会在插入数据时同步更新缓存
    //                    triggerLiveData.removeSource(groupMemberSource);
    //                    groupMemberIsRequest = false;
    //
    //                }
    //
    //                if (resource.status == Status.SUCCESS && resource.data != null && result != null) {
    //                    List<GroupMember> data = resource.data;
    //                    ArrayList<String> userInfoIdList = new ArrayList<>();
    //                    for (GroupMember member : data) {
    //                        userInfoIdList.add(member.getUserId());
    //                    }
    //                    result.onGotMemberList(userInfoIdList);
    //                }
    //            });
    //        });
    //    }

    //    /**
    //     * 请求更新好友信息
    //     *
    //     * @param friendId
    //     */
    //    public void updateFriendInfo(String friendId) {
    //        ThreadManager.getInstance().runOnUIThread(() -> {
    //            LiveData<Resource<FriendShipInfo>> friendInfo = friendTask.getFriendInfo(friendId);
    //            triggerLiveData.addSource(friendInfo, resource -> {
    //                if (resource.status == Status.SUCCESS || resource.status == Status.ERROR) {
    //                    // 确认成功或失败后，移除数据源
    //                    // 在请求成功后，会在插入数据时同步更新缓存
    //                    triggerLiveData.removeSource(friendInfo);
    //                }
    //            });
    //        });
    //    }

    //    /**
    //     * 获取联系人列表
    //     *
    //     * @param contactInfoCallback
    //     */
    //    public void getAllContactUserInfo(IContactCardInfoProvider.IContactCardInfoCallback contactInfoCallback){
    //        ThreadManager.getInstance().runOnUIThread(() -> {
    //            LiveData<Resource<List<FriendShipInfo>>> allFriends = friendTask.getAllFriends();
    //            triggerLiveData.addSource(allFriends, resource -> {
    //                if (resource.status == Status.SUCCESS || resource.status == Status.ERROR) {
    //                    // 确认成功或失败后，移除数据源
    //                    triggerLiveData.removeSource(allFriends);
    //                    List<FriendShipInfo> friendShipInfoList = resource.data;
    //                    List<io.rong.imlib.model.UserInfo> userInfoList = new ArrayList<>();
    //                    if (friendShipInfoList != null) {
    //                        for (FriendShipInfo info : friendShipInfoList) {
    //                            FriendDetailInfo friendUser = info.getUser();
    //                            if (friendUser != null) {
    //                                io.rong.imlib.model.UserInfo user = new io.rong.imlib.model.UserInfo(friendUser.getId(), friendUser.getNickname(), Uri.parse(friendUser.getPortraitUri()));
    //                                userInfoList.add(user);
    //                            }
    //                        }
    //                    }
    //                    contactInfoCallback.getContactCardInfoCallback(userInfoList);
    //                }
    //            });
    //        });
    //    }

    //    /**
    //     * 获取单一用户联系人
    //     *
    //     * @param userId
    //     * @param contactInfoCallback
    //     */
    //    public void getContactUserInfo(String userId, IContactCardInfoProvider.IContactCardInfoCallback contactInfoCallback){
    //        ThreadManager.getInstance().runOnUIThread(() -> {
    //            LiveData<Resource<FriendShipInfo>> friendInfo = friendTask.getFriendInfo(userId);
    //            triggerLiveData.addSource(friendInfo, resource -> {
    //                if (resource.status == Status.SUCCESS || resource.status == Status.ERROR) {
    //                    // 确认成功或失败后，移除数据源
    //                    triggerLiveData.removeSource(friendInfo);
    //                    FriendShipInfo data = resource.data;
    //                    List<io.rong.imlib.model.UserInfo> userInfoList = new ArrayList<>();
    //                    if (data != null) {
    //                        FriendDetailInfo friendUser = data.getUser();
    //                        if (friendUser != null) {
    //                            io.rong.imlib.model.UserInfo user = new io.rong.imlib.model.UserInfo(friendUser.getId(), friendUser.getNickname(), Uri.parse(friendUser.getPortraitUri()));
    //                            userInfoList.add(user);
    //                        }
    //                    }
    //                    contactInfoCallback.getContactCardInfoCallback(userInfoList);
    //                }
    //            });
    //        });
    //    }

    //    /**
    //     * 更新数据库中群组名称
    //     *
    //     * @param groupId
    //     * @param groupName
    //     */
    //    public void updateGroupNameInDb(String groupId, String groupName) {
    //        ThreadManager.getInstance().runOnWorkThread(() -> {
    //            GroupDao groupDao = dbManager.getGroupDao();
    //            if (groupDao != null) {
    //                int updateResult = groupDao.updateGroupName(groupId, groupName, CharacterParser.getInstance().getSelling(groupName));
    //
    //                // 更新成时同时更新缓存
    //                if (updateResult > 0) {
    //                    GroupEntity groupInfo = groupDao.getGroupInfoSync(groupId);
    //                    if (groupInfo != null) {
    //                        IMManager.getInstance().updateGroupInfoCache(groupId, groupName, Uri.parse(groupInfo.getPortraitUri()));
    //                    }
    //                }
    //            }
    //        });
    //    }

    //    /**
    //     * 删除数据库中群组及对应的群组成员
    //     *
    //     * @param groupId
    //     */
    //    public void deleteGroupInfoInDb(String groupId) {
    //        ThreadManager.getInstance().runOnWorkThread(() -> {
    //            GroupDao groupDao = dbManager.getGroupDao();
    //            if (groupDao != null) {
    //                groupDao.deleteGroup(groupId);
    //            }
    //            GroupMemberDao groupMemberDao = dbManager.getGroupMemberDao();
    //            if (groupMemberDao != null) {
    //                groupMemberDao.deleteGroupMember(groupId);
    //            }
    //        });
    //    }

}
