package rs.pstech.bozovic.nemanja.contacts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemanja on 7.9.15..
 */
public class SimpleContact {
    String mId;
    String mName;
//    String mPhotoId; //TODO

    public SimpleContact(String id, String name){
        mId = id;
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }
}
