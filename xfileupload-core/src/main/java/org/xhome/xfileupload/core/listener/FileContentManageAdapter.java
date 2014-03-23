package org.xhome.xfileupload.core.listener;

import org.xhome.xauth.User;
import org.xhome.xfileupload.FileContent;

public class FileContentManageAdapter implements FileContentManageListener {

    public boolean beforeFileContentManage(User oper, short action,
                    FileContent fileContent, Object... args) {
        return true;
    }

    public void afterFileContentManage(User oper, short action, short result,
                    FileContent fileContent, Object... args) {}

}
