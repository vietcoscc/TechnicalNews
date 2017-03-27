package com.example.vaio.technicalnews.model;

import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by vaio on 27/03/2017.
 */

public class FireBaseReference {

    //
    public static final String FORUM = "Forum";
    public static final String NAME = "name";
    public static final String POSITION = "position";
    public static final String ARR_COMMENT = "arrComment";
    public static final String POST_NUMBER = "postNumber";
    public static final String TOPIC_NUMBER = "topicNumber";
    //
    public static final String ROOM_CHAT = "Room chat";
    public static final String AREA = "area";
    public static final String ARR_CHAT = "arrChat";
    public static final String ONLINE_NUMBER = "onlineNumber";
    //
    public static final String TOPIC = "Topic";
    public static final String TOPIC_KEY = "Topic key";
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public static DatabaseReference getArrChatRef(String key) {
        return getRoomChatRef().child(key).child(ARR_CHAT);
    }

    public static DatabaseReference getForumRef() {
        return databaseReference.child(FORUM);
    }

    public static DatabaseReference getRoomChatRef() {
        return databaseReference.child(ROOM_CHAT);
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
        return getForumRef().child(groupForumItemKey).child(ForumFragment.ARR_CHILD_FORUM_ITEM).child(childForumPosition + "").child(POST_NUMBER);
    }

}
