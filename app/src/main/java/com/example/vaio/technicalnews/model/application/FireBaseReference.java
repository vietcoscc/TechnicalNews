package com.example.vaio.technicalnews.model.application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by vaio on 27/03/2017.
 */

public class FireBaseReference {

    //
    public static final String FORUM = "Forum";

    public static final String POSITION = "position";
    public static final String ARR_COMMENT = "arrComment";
    public static final String ARR_REPLY = "arrReply";
    public static final String POST_NUMBER = "postNumber";
    public static final String TOPIC_NUMBER = "topicNumber";
    public static final String ARR_CHILD_FORUM_ITEM = "arrChildForumItem";
    //
    public static final String ROOM_CHAT = "Room chat";
    public static final String AREA = "area";
    public static final String ARR_CHAT = "arrChat";
    public static final String ONLINE_NUMBER = "onlineNumber";
    //
    public static final String TOPIC = "Topic";
    public static final String SUBJECT = "subject";
    public static final String CONTENT = "content";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String NUMBER_CARE = "numberCare";
    public static final String NUMBER_VIEW = "numberView";
    public static final String NUMBER_REPLY = "numberReply";
    public static final String MAIL = "mail";
    public static final String NAME = "name";
    public static final String PHOTO_PATH = "photoPath";
    public static final String ARR_FAVORITE = "arrFavorite";
    //
    public static final String TOPIC_KEY = "Topic key";
    private static final String ADMIN = "Admin";
    private static final String BAN = "Ban";
    public static final String DELETED = "Deleted";
    public static final String NOTIFICATION = "Notificaition";
    //
    public static final String CHAT_ROOM = "Chat room";
    //
    public static final String ACCOUNT = "Account";

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    public static DatabaseReference getForumRef() {
        return databaseReference.child(FORUM);
    }

    public static DatabaseReference getChildForumItemRef(String groupForumName, String childForunName) {
        return databaseReference.child(TOPIC).child(groupForumName).child(childForunName);
    }

    public static DatabaseReference getTopicKeyRef(String groupForumName, String childForunName, String key) {
        return getChildForumItemRef(groupForumName, childForunName).child(key);
    }

    public static DatabaseReference getArrCommentRef(String groupForumName, String childForunName, String key) {
        return getTopicKeyRef(groupForumName, childForunName, key).child(ARR_COMMENT);
    }

    public static DatabaseReference getPostNumberRef(String groupForumItemKey, int childForumPosition) {
        return getForumRef().child(groupForumItemKey).child(ARR_CHILD_FORUM_ITEM).child(childForumPosition + "").child(POST_NUMBER);
    }

    public static DatabaseReference getAdminRef() {
        return databaseReference.child(ADMIN);
    }

    public static DatabaseReference getBanRef() {
        return databaseReference.child(BAN);
    }

    public static DatabaseReference getDeletedRef() {
        return databaseReference.child(DELETED);
    }

    public static DatabaseReference getNotifocationRef() {
        return databaseReference.child(NOTIFICATION);
    }

    public static DatabaseReference getChatRoomRef() {
        return databaseReference.child(CHAT_ROOM);
    }

    public static DatabaseReference getRoomChatRef(String chatRoom) {
        return databaseReference.child(ROOM_CHAT).child(chatRoom);
    }

    public static DatabaseReference getAccountRef() {
        return databaseReference.child(ACCOUNT);
    }

    public static DatabaseReference getUserIdRef(String uid) {
        return getAccountRef().child(uid);
    }

    public static DatabaseReference getArrFavoriteRef(String groupForumName, String childForunName, String key) {
        return getTopicKeyRef(groupForumName, childForunName, key).child(ARR_FAVORITE);
    }

    public static DatabaseReference getNumberCareRef(String groupForumName, String childForunName, String key) {
        return getTopicKeyRef(groupForumName, childForunName, key).child(NUMBER_CARE);
    }
}
