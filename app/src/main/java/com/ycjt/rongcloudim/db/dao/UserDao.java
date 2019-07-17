package com.ycjt.rongcloudim.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ycjt.rongcloudim.db.model.UserInfo;

import java.util.List;


@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE id=:id")
    LiveData<UserInfo> getUserById(String id);

    @Query("SELECT * FROM user WHERE id=:id")
    UserInfo getUserByIdSync(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserInfo userInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserList(List<UserInfo> userInfo);

    @Query("UPDATE user SET name=:name, name_spelling=:nameSpelling, portrait_uri=:portraitUrl WHERE id=:id")
    int updateNameAndPortrait(String id, String name, String nameSpelling, String portraitUrl);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUserListIgnoreExist(List<UserInfo> userInfoList);

    @Query("UPDATE user SET name=:name,name_spelling=:nameSpelling WHERE id=:id")
    int updateName(String id, String name, String nameSpelling);

    @Query("UPDATE user SET alias=:alias,alias_spelling=:aliasSpelling,order_spelling=:aliasSpelling WHERE id=:id")
    int updateAlias(String id, String alias, String aliasSpelling);

    @Query("UPDATE user SET portrait_uri=:portraitUrl WHERE id=:id")
    int updatePortrait(String id, String portraitUrl);

    @Query("UPDATE user SET friend_status=:friendStatus WHERE id=:id")
    int updateFriendStatus(String id, int friendStatus);

}
