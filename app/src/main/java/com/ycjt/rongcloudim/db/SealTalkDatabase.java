package com.ycjt.rongcloudim.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.ycjt.rongcloudim.db.dao.FriendDao;
import com.ycjt.rongcloudim.db.dao.GroupDao;
import com.ycjt.rongcloudim.db.dao.GroupMemberDao;
import com.ycjt.rongcloudim.db.dao.UserDao;
import com.ycjt.rongcloudim.db.model.BlackListEntity;
import com.ycjt.rongcloudim.db.model.FriendInfo;
import com.ycjt.rongcloudim.db.model.GroupEntity;
import com.ycjt.rongcloudim.db.model.GroupMemberInfoEntity;
import com.ycjt.rongcloudim.db.model.UserInfo;

@Database(entities = {UserInfo.class, FriendInfo.class, GroupEntity.class, GroupMemberInfoEntity.class, BlackListEntity.class}, version = 1, exportSchema = false)
@TypeConverters(com.ycjt.rongcloudim.db.TypeConverters.class)
public abstract class SealTalkDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();

    public abstract FriendDao getFriendDao();

    public abstract GroupDao getGroupDao();

    public abstract GroupMemberDao getGroupMemberDao();
}
